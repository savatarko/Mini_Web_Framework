package framework.engine;

import java.util.HashMap;
import java.util.Map;

public class DependencyContainer {

    private static Map<String, Class> implementationMap = new HashMap<>();


    public static void registerClass(String name, Class clazz){
        if(implementationMap.containsKey(name) && !implementationMap.get(name).equals(clazz)){
            throw new RuntimeException("Two qualifiers with the same name:" + name);
        }
        implementationMap.put(name, clazz);
    }

    public static Class getClass(String name){
        if(!implementationMap.containsKey(name)){
            throw new RuntimeException("Could not find class for given interface " + name);
        }
        return implementationMap.get(name);
    }
}
