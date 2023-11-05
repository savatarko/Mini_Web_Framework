package framework.engine;

import framework.annotations.Autowired;
import framework.engine.DIEngine;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.stream.Collectors;

public class Discovery {
    private static List<Class> output = new ArrayList<>();

    public static void main(String[] args) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String[] classPathEntries = System.getProperty("java.class.path").split(File.pathSeparator);
        for(String x:classPathEntries){
            System.out.println("test");
            System.out.println(x);
        }

        for (String classpathEntry : System.getProperty("java.class.path").split(System.getProperty("path.separator"))) {
                File file = new File(classpathEntry);
                fileRecursion(file);
            }
    }


    private static String basePath = "";

    public static List<Class> getAllClasses(){
        output = new ArrayList<>();
        for (String classpathEntry : System.getProperty("java.class.path").split(System.getProperty("path.separator"))) {
            File file = new File(classpathEntry);
            fileRecursion(file);
        }
        return output;
    }

    public static void fileRecursion(File file){
        try {
            for (File x : file.listFiles()) {
                if(x.getName().endsWith(".class")){
                    Class clazz = Class.forName(basePath + "." + x.getName().replaceAll(".class", ""));
                    output.add(clazz);
                }
                else{
                    if(!basePath.equals(""))
                        basePath = basePath + "." + x.getName();
                    else basePath = x.getName();
                    fileRecursion(x);
                    if(basePath.contains("."))
                        basePath = basePath.substring(0, basePath.lastIndexOf("."));
                }
            }
        }
        catch (Exception e){
            System.out.println(e);
        }
    }
}
