package bmstu.iu7m.osipov.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class ClassObjectBuilder {

    public static Object createInstance(String className, Class<?>[] paramTypes, Object[] paramValues){
        Class type = null;
        Object obj = null;
        try{
            type = Class.forName(className);
        }
        catch (ClassNotFoundException e) { }

        if(type == null)
            return obj;

        Constructor<?> ctr = null;
        try {
            ctr = type.getDeclaredConstructor(paramTypes);
        } catch (NoSuchMethodException | SecurityException e){}
        if(ctr == null)
            return obj;

        try {
            obj = ctr.newInstance(paramValues);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e){}

        return obj;
    }

    public static Constructor<?> getDeclaredConstructor(String className, Class<?>[] paramTypes) {
        Class<?> type = null;
        Constructor<?> ctr = null;
        try {
            type = Class.forName(className);
        } catch (ClassNotFoundException e) { }

        if (type == null)
            return ctr;

        try {
            ctr = type.getDeclaredConstructor(paramTypes);
        } catch (NoSuchMethodException | SecurityException e) {
            System.out.println(e);
        }
        return ctr;
    }

    public static Object createInstance(Constructor<?> constructor, Object[] paramValues){
        Object obj = null;
        if(constructor == null)
            return obj;
        try {
            obj = constructor.newInstance(paramValues);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e){}

        return obj;
    }
}