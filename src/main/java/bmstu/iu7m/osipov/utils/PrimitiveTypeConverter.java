package bmstu.iu7m.osipov.utils;

public class PrimitiveTypeConverter {

    /**
     * Casts Object src to the specified type.
     * @param type return type
     * @param src source object to be cast.
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
            }
            case "byte": case "Byte":{
                result = (byte)ProcessNumber.parseNumber(src);
                break;
            }
            case "short": case "Short":{
                result = (short)ProcessNumber.parseNumber(src);
                break;
            }
            case "int": case "Integer":{
                result = (int) ProcessNumber.parseNumber(src);
                break;
            }
            case "long": case "Long":{
                result = (long)ProcessNumber.parseNumber(src);
                break;
            }
            case "float": case "Float":{
                result = (float)ProcessNumber.parseNumber(src);
                break;
            }
            case "double": case "Double":{
                result = ProcessNumber.parseNumber(src);
                break;
            }
            default:{
                result = src;
            }
        }
        return result;
    }
}
