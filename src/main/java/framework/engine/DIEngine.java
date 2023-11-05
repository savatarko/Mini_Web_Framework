package framework.engine;

import framework.annotations.*;
import framework.annotations.httprequest.HttpRequest;
import framework.annotations.httprequest.HttpType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class DIEngine {

    private static Map<Class, Object> singletonClassesMap = new HashMap<>();

    private static Map<HttpRequest, Method> httpMap = new HashMap<>();
    private static Map<Class, Object> controllers = new HashMap<>();

    static {
        try {
            init();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void init() throws InvocationTargetException, InstantiationException, IllegalAccessException {
        String basepath = "";
        List<Class> classes = Discovery.getAllClasses();
        for(Class clazz: classes){
            if(clazz.isAnnotationPresent(Qualifier.class)){
                Qualifier qualifier = (Qualifier) clazz.getAnnotation(Qualifier.class);
                DependencyContainer.registerClass(qualifier.value(), clazz);
            }
        }

        for (Class clazz : classes) {
            if (clazz.isAnnotationPresent(Path.class)) {
                Path path = (Path) clazz.getAnnotation(Path.class);
                if (path.path() == null) {
                    throw new RuntimeException("Path cant be null!");
                }
                basepath = path.path();
            }
            if (clazz.isAnnotationPresent(Controller.class)) {
                for (Method method : clazz.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(Path.class)) {
                        Path path = method.getAnnotation(Path.class);
                        if (path.path() == null) {
                            throw new RuntimeException("Path cant be null!");
                        }
                        if (method.isAnnotationPresent(GET.class)) {
                            HttpRequest request = new HttpRequest(basepath + path.path(), HttpType.GET);
                            if (httpMap.containsKey(request)) {
                                throw new RuntimeException("Only one method can be declared on a path!");
                            }
                            httpMap.put(request, method);
                        } else if (method.isAnnotationPresent(POST.class)) {
                            HttpRequest request = new HttpRequest(basepath + path.path(), HttpType.POST);
                            if (httpMap.containsKey(request)) {
                                throw new RuntimeException("Only one method can be declared on a path!");
                            }
                            httpMap.put(request, method);
                        } else throw new RuntimeException("You have to declare the request type(GET OR POST)");
                    }
                }
                initializeSubClass(clazz);
            }
        }
    }

    private static Object initializeSubClass(Class clazz) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Map<Class, Object> objectMap = new HashMap<>();
        if (singletonClassesMap.containsKey(clazz)) {
            return singletonClassesMap.get(clazz);
        }
        for (Field f : clazz.getDeclaredFields()) {
            if (f.isAnnotationPresent(Autowired.class)) {
                if (!f.isAnnotationPresent(Qualifier.class)) {
                    throw new RuntimeException("You have to put a qualifier tag on a autowired field!!");
                }
                if(!f.getType().isInterface()) {
                    objectMap.put(f.getType(), initializeSubClass(f.getType()));
                }
                else {
                    Qualifier qualifier = (Qualifier) f.getAnnotation(Qualifier.class);
                    objectMap.put(f.getType(), initializeSubClass(DependencyContainer.getClass(qualifier.value())));
                }

            }
        }
        if(clazz.isAnnotationPresent(Controller.class)){
            Constructor c;
            try {
                c = clazz.getDeclaredConstructor();
            }
            catch (Exception e){
                throw new RuntimeException("error");
            }
            Object o = c.newInstance();
            for(Field f: clazz.getFields()){
                if(f.isAnnotationPresent(Autowired.class)){
                    Autowired autowired = (Autowired) f.getAnnotation(Autowired.class);
                    if(singletonClassesMap.containsKey(f.getType())){
                        f.set(o, singletonClassesMap.get(f.getType()));
                        if(autowired.verbose()){
                            System.out.println("Initialized " + f.getType().getName() + " " + f.getName() + " in " + clazz.getName() + " on " + LocalDateTime.now().toString() + " with " + singletonClassesMap.get(f.getType()).toString());
                        }
                    }
                    else{
                        f.set(o, objectMap.get(f.getType()));
                        if(autowired.verbose()){
                            System.out.println("Initialized " + f.getType().getName() + " " + f.getName() + " in " + clazz.getName() + " on " + LocalDateTime.now().toString() + " with " + objectMap.get(f.getType()).toString());
                        }
                    }
                }
            }
            controllers.put(clazz, o);
            return o;
        }
        if (clazz.isAnnotationPresent(Bean.class) || clazz.isAnnotationPresent(Service.class) || clazz.isAnnotationPresent(Component.class)) {
            Constructor c;
            Object o;
            try {
                c = clazz.getDeclaredConstructor();
            }
            catch (Exception e){
                throw new RuntimeException("error");
            }
            o = c.newInstance();
            for(Field f: clazz.getFields()){
                if(f.isAnnotationPresent(Autowired.class)){
                    Autowired autowired = (Autowired) f.getAnnotation(Autowired.class);
                    if(singletonClassesMap.containsKey(f.getType())){
                        f.set(o, singletonClassesMap.get(f.getType()));
                        if(autowired.verbose()){
                            System.out.println("Initialized " + f.getType().getName() + " " + f.getName() + " in " + clazz.getName() + " on " + LocalDateTime.now().toString() + " with " + singletonClassesMap.get(f.getType()).toString());
                        }
                    }
                    else{
                        f.set(o, objectMap.get(f.getType()));
                        if(autowired.verbose()){
                            System.out.println("Initialized " + f.getType().getName() + " " + f.getName() + " in " + clazz.getName() + " on " + LocalDateTime.now().toString() + " with " + objectMap.get(f.getType()).toString());
                        }
                    }
                }
            }
            if (clazz.isAnnotationPresent(Qualifier.class)) {
                Qualifier qualifier = (Qualifier) clazz.getAnnotation(Qualifier.class);
                if (qualifier.value() == null) {
                    throw new RuntimeException("Empty Qualifier!");
                }
                DependencyContainer.registerClass(qualifier.value(), clazz);
            }
            if (clazz.isAnnotationPresent(Service.class)) {
                    singletonClassesMap.put(clazz, o);
                    return singletonClassesMap.get(clazz);
            } else if (clazz.isAnnotationPresent(Bean.class)) {
                Bean bean = (Bean) clazz.getAnnotation(Bean.class);
                if (bean.scope() == BeanType.SINGLETON) {
                    try {
                        singletonClassesMap.put(clazz, o);
                        return singletonClassesMap.get(clazz);
                    } catch (Exception e) {
                        throw new RuntimeException();
                    }
                }
                else if(bean.scope() == BeanType.PROTOTYPE){
                    return o;
                }
            }
            else if(clazz.isAnnotationPresent(Component.class)){
                return o;
            }
        }
        throw new RuntimeException("Something autowired has to be bean service or component!");
        //return null;
    }

    public static Map<HttpRequest, Method> getHttpMap() {
        return httpMap;
    }

    public static Map<Class, Object> getControllers() {
        return controllers;
    }
}
