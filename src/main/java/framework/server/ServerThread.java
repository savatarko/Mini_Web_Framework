package framework.server;



import framework.annotations.httprequest.HttpRequest;
import framework.annotations.httprequest.HttpType;
import framework.engine.DIEngine;
import framework.server.request.Header;
import framework.server.request.Helper;
import framework.server.request.Request;
import framework.server.request.enums.Method;
import framework.server.request.exceptions.RequestNotValidException;
import framework.server.response.JsonResponse;
import framework.server.response.Response;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServerThread implements Runnable{

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Map<HttpRequest, java.lang.reflect.Method> httpMap = new HashMap<>();
    private Map<Class, Object> controllers = new HashMap<>();

    public ServerThread(Socket socket){
        this.socket = socket;
        this.httpMap = DIEngine.getHttpMap();
        this.controllers = DIEngine.getControllers();

        try {
            in = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream()));

            out = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    socket.getOutputStream())), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        try {
            Request request = this.generateRequest();
            if(request == null) {
                in.close();
                out.close();
                socket.close();
                return;
            }


            System.out.println(request.getLocation());
            // Response example

            HttpRequest req = new HttpRequest(request.getLocation(), request.getMethod());

            if(!httpMap.containsKey(req)){
                throw new RequestNotValidException("");
            }
            java.lang.reflect.Method answer = httpMap.get(req);

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("answer", answer.invoke(controllers.get(answer.getDeclaringClass())));
            Response response = new JsonResponse(responseMap);

            out.println(response.render());

            in.close();
            out.close();
            socket.close();

        } catch (IOException | RequestNotValidException e) {
            out.println("error");
            e.printStackTrace();
        }
        catch (Exception e){
            out.println("error");
            e.printStackTrace();
        }
    }

    private Request generateRequest() throws IOException, RequestNotValidException {
        String command = in.readLine();
        if(command == null) {
            return null;
        }

        String[] actionRow = command.split(" ");
        HttpType method = HttpType.valueOf(actionRow[0]);
        String route = actionRow[1];
        Header header = new Header();
        HashMap<String, String> parameters = Helper.getParametersFromRoute(route);

        do {
            command = in.readLine();
            String[] headerRow = command.split(": ");
            if(headerRow.length == 2) {
                header.add(headerRow[0], headerRow[1]);
            }
        } while(!command.trim().equals(""));

        if(method.equals(Method.POST)) {
            int contentLength = Integer.parseInt(header.get("content-length"));
            char[] buff = new char[contentLength];
            in.read(buff, 0, contentLength);
            String parametersString = new String(buff);

            HashMap<String, String> postParameters = Helper.getParametersFromString(parametersString);
            for (String parameterName : postParameters.keySet()) {
                parameters.put(parameterName, postParameters.get(parameterName));
            }
        }

        Request request = new Request(method, route, header, parameters);

        return request;
    }
}
