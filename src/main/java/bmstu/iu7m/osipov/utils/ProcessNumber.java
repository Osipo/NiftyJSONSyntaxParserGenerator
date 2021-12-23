package bmstu.iu7m.osipov.utils;

import bmstu.iu7m.osipov.services.grammars.directives.ElementProcessorSDT;

//Process octal,hex,binary,decimal scientific literals to decimal number.
public class ProcessNumber {

    private static final String digits = "0123456789ABCDEF";

    public static double parse(String num, String e, char c, int base, int sign, int esign){//c - type of exponent.
        if(num == null || num.length() == 0)
            return Double.NaN;

        double exp = 1;

        if((c == 'P' || c == 'p') && e != null && e.length() > 0)//binary exponent.
            exp = Math.pow(2.0, parseNumber(e, 10));
        else if((c == 'E' || c == 'e' || c == 'H' || c == 'h') && e != null && e.length() > 0)//H and h are exponent for hex numbers.
            exp = Math.pow(10.0, parseNumber(e, 10));
        else if(c == 'E' || c == 'e' || c == 'H' || c == 'h' || c == 'P' || c == 'p'){ // e == null or e.length() == 0
            return Double.NaN;
        }
        if(esign < 0)
            exp = 1 / exp;

        double result = parseNumber(num, base);

        result = result * exp;
        if(sign < 0)
            result = 0 - result;
        return result;
    }

    private static double parseNumber(String num, int base){
        num = num.toUpperCase();
        double val = 0;
        int i = 0;
        while(i < num.length())
        {
            char c = num.charAt(i);
            if(c == '.') {
                i++;
                break;
            }
            int d = digits.indexOf(c);
            if(d == -1 || d >= base)
                return Double.NaN;
            val = base * val + d;
            i++;
        }
        int power = 1;
        while(i < num.length()){ // read part after '.' symbol
            char c = num.charAt(i);
            int d = digits.indexOf(c);
            if(d == -1 || d >= base)
                return Double.NaN;
            power *= base;
            val = base * val + d;
            i++;
        }
        return val / power;
    }


    //Shortened version of parse method, where other 4 arguments (exp, type_of_exp, base, sign)
    //are computed by num string.
    //This method returns NaN if str throws NumberFormatException
    //For example, str like '-0x' or '0b01P' are illegal (because the former does not contain digits, the latter has no digits after exponent symbol)
    //Exponent symbol requires digits (E1 or E10 or P-1).
    public static double parseNumber(String str){
        if(str == null || str.length() == 0)
            return Double.NaN;
        int sign = 1;
        int esign = 1;//exponent sign
        int base = 10;
        int i = 0;
        if(str.charAt(0) == '-') {
            sign = -1;
            i = 1;
        }
        if(i > 0 && i == str.length()) //str is '-'
            return Double.NaN;

        // suffix '0x' => 16 (hex)
        if(str.charAt(i) == '0' && (i + 1 != str.length()) && str.charAt(i + 1) == 'x') {
            base = 16;
            i += 2;
        }
        //suffix '0b' => 2 (binary)
        else if(str.charAt(i) == '0' && (i + 1 != str.length()) && str.charAt(i + 1) == 'b') {
            base = 2;
            i += 2;
        }
        //suffix '0c' => 8 (octal)
        else if(str.charAt(i) == '0' && (i + 1 != str.length()) && str.charAt(i + 1) == 'c'){
            base = 8;
            i += 2;
        }
        if(i == str.length())// str does not contains digits but '-suffix' or 'suffix' (-0x -0b -0c 0x 0b 0c)
            return Double.NaN;

        //compute exponent type.
        int idx = str.indexOf('H');
        idx = (idx == -1) ? str.indexOf('h') : idx;
        idx = (idx == -1) ? str.indexOf('P') : idx;
        idx = (idx == -1) ? str.indexOf('p') : idx;
        idx = (idx == -1 && base != 16) ? str.indexOf('E') : idx;
        idx = (idx == -1 && base != 16) ? str.indexOf('e') : idx;

        char etype = (idx == -1) ? 'N' : str.charAt(idx);

        if(idx + 1 == str.length())// no more digits after exponent letter ('12E' or 'FFP')
            return Double.NaN;

        String exp = null;

        if(str.charAt(idx + 1) == '-' && idx != -1 && idx + 2 == str.length())
            return Double.NaN;
        if(str.charAt(idx + 1) == '-' && idx != -1){
            esign = -1;
            exp = str.substring(idx + 2);
        }
        else
            exp = str.substring(idx + 1);

        idx = (idx == -1) ? str.length() : idx; //if no exponent then idx is the length of the str.

        String number = str.substring(i, idx);
        return parse(number, exp, etype, base, sign, esign);
    }

    public static Number parseNumber(String num, Class<?> type){
        if(type == null)
            return Double.NaN;
        type = ClassObjectBuilder.getBoxedTypes().getOrDefault(type.getSimpleName(), type);
        if(!Number.class.isAssignableFrom(type) || num == null || num.length() == 0)
            return Double.NaN;

        boolean isMax = num.equalsIgnoreCase("MAX_VALUE");
        boolean isMin = num.equalsIgnoreCase("MIN_VALUE");
        boolean isInf = num.equalsIgnoreCase("INFINITY");
        switch (type.getSimpleName()){
            case "byte": case "Byte":{
                return  (isMax) ? Byte.MAX_VALUE :
                        (isMin) ? Byte.MIN_VALUE :
                                (byte)parseNumber(num);
            }
            case "short": case "Short":{
                return  (isMax) ? Short.MAX_VALUE :
                        (isMin) ? Short.MIN_VALUE :
                                (short)parseNumber(num);
            }
            case "int": case "Integer":{
                return  (isMax) ? Integer.MAX_VALUE :
                        (isMin) ? Integer.MIN_VALUE :
                                (int)parseNumber(num);
            }
            case "long": case"Long":{
                return  (isMax) ? Long.MAX_VALUE :
                        (isMin) ? Long.MIN_VALUE :
                                (long)parseNumber(num);
            }
            case "float": case "Float":{
                return  (isMax) ? Float.MAX_VALUE :
                        (isMin) ? Float.MIN_VALUE :
                        (isInf) ? Float.POSITIVE_INFINITY :
                                (float)parseNumber(num);
            }
            case "double": case "Double": {
                return  (isMax) ? Double.MAX_VALUE :
                        (isMin) ? Double.MIN_VALUE :
                        (isInf) ? Double.POSITIVE_INFINITY :
                                parseNumber(num);
            }
            default:{
                return Double.NaN;
            }
        }// end of switch
    }// end of method
}