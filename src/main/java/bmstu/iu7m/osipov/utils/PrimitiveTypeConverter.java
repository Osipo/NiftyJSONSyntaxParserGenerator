package bmstu.iu7m.osipov.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;

public class PrimitiveTypeConverter {

    /**
     * Casts Object src to the specified type.
     * @param type return type
     * @param src source string to be cast.
     * @return cast object with target type.
     */
    public static Object castTo(Class<?> type, String src){
        Object result = null;
        switch (type.getSimpleName()){
            case "boolean": case "Boolean":{
                if(src.equalsIgnoreCase("true"))
                    result = true;
                else if(src.equalsIgnoreCase("false"))
                    result = false;
                break;
            }
            case "byte": case "Byte":{
                result = ProcessNumber.parseNumber(src, type).byteValue();
                break;
            }
            case "short": case "Short":{
                result = ProcessNumber.parseNumber(src, type).shortValue();
                break;
            }
            case "int": case "Integer":{
                result = ProcessNumber.parseNumber(src, type).intValue();
                break;
            }
            case "long": case "Long":{
                result = ProcessNumber.parseNumber(src, type).longValue();
                break;
            }
            case "float": case "Float":{
                result = ProcessNumber.parseNumber(src, type).floatValue();
                break;
            }
            case "double": case "Double":{
                result = ProcessNumber.parseNumber(src, type).doubleValue();
                break;
            }
            case "String":{
                result = src;
                break;
            }
        }
        if(type.isEnum()){
            result = Enum.valueOf((Class<? extends Enum>)type, src);
        }
        if(result != null)
            return result;

        try{
            Field f = type.getDeclaredField(src); //get static value of specified property 'src' of the type.
            result = f.get(null);
        } catch (NoSuchFieldException | NullPointerException | IllegalAccessException e){}
        return result;
    }



    public static Object[] convertConstructorArguments(Constructor<?> c, Object[] args, int offset){
        Parameter[] actual_params = c.getParameters();
        for(int i = offset; i < args.length; i++){
            args[i] = PrimitiveTypeConverter.castTo(actual_params[i].getType(), args[i].toString());
        }
        return args;
    }

    //new version with covariance.
    public static Object[] convertConstructorArguments(Constructor<?> c, Class<?>[] apTypes, Object[] args, int offset){
        Class<?>[] pTypes = c.getParameterTypes();
        Class<?> ptype = null;
        for(int i = offset; i < args.length; i++){
            if(pTypes[i].isAssignableFrom(apTypes[i]))
                ptype = apTypes[i];
            else
                ptype = pTypes[i];
            args[i] = PrimitiveTypeConverter.castTo(ptype, args[i].toString());
        }
        return args;
    }
}