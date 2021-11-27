package bmstu.iu7m.osipov.utils;

import java.util.Map;

public class ClassObjectBuilder {
    public static Object createInstance(String className, Map<String, String> attrs){
        Class type = null;
        Object obj = null;
        try{
            type = Class.forName(className);
        }
        catch (ClassNotFoundException e){

        }
        return obj;
    }
}
