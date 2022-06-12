package bmstu.iu7m.osipov.services.interpret;

import bmstu.iu7m.osipov.structures.lists.LinkedStack;
import bmstu.iu7m.osipov.utils.ProcessNumber;

public class TypeChecker {

    public static Object GetRawValue(Object value){
        return value;
    }

    public static String GetStrValue(Object value){
        return value.toString();
    }

    public static Object CheckValue(Object value) throws Exception {
        if(value == null)
            throw new NullPointerException("Cannot compute value. Value is null.");
        throw new Exception("Cannot define value for type: " + value.getClass());
    }
    public static Object CheckExpressionType(LinkedStack<Object> expr, String etype, String op){
        Object val1 = expr.top();
        expr.pop();
        if(etype.equals("unaryop"))
           return ParseUnaryOperator(val1, op);
        else if(etype.equals("operator") || etype.equals("relop") || etype.equals("boolop")) {
            Object val2 = expr.top();
            expr.pop();
            return ParseBinaryOperator(val2, val1, op);
        }
        else
            return val1;
    }

    private static Object ParseUnaryOperator(Object n, String nodeVal){
        String t1 = "";
        if(n instanceof String)
            t1 = (String) n;
        double d1 = ProcessNumber.parseNumber(t1);
        switch (nodeVal){
            case "-":{
                d1 = -d1; //negate.
            }
            case "+":{
                break;
            }
            case "++":{
                d1 += 1;
                break;
            }
            case "--":{
                d1 -= 1;
                break;
            }
            case "not":{
                d1 = (d1 >= 1) ? 0 : 1; //negate 1 to 0 and vice versa.
                break;
            }
        }
        if(Math.floor(d1) == d1) //is integer [4.0, 5.0]
            n = Integer.toString((int) d1);
        else
            n = Double.toString(d1); //double [4.0001]

        return n;
    }

    private static Object ParseBinaryOperator(Object n1, Object n2, String nodeVal){
        String t1 = "";
        String t2 = "";
        if(n1 instanceof String && n2 instanceof String) {
            t1 = (String) n1;
            t2 = (String) n2;
        }

        double d1 = ProcessNumber.parseNumber(t1);
        double d2 = ProcessNumber.parseNumber(t2);

        switch (nodeVal){
            case "<":{
                d1 = (d1 < d2) ? 1 : 0;
                break;
            }
            case "<=":{
                d1 = (d1 <= d2) ? 1 : 0;
                break;
            }
            case ">":{
                d1 = (d1 > d2) ? 1 : 0;
                break;
            }
            case ">=":{
                d1 = (d1 >= d2) ? 1 : 0;
                break;
            }
            case "==":{
                d1 = (d1 == d2) ? 1 : 0;
                break;
            }
            case "<>":{
                d1 = (d1 != d2) ? 1 : 0;
                break;
            }
            case "&&": case "and": {
                d1 = (d1 >= 1 && d2 >= 1) ? 1 : 0;
                break;
            }
            case "||": case "or": {
                d1 = (d1 >= 1 || d2 >= 1) ? 1 : 0;
                break;
            }
            case "=>":{
                d1 = (d1 >= 1 && d2 <= 1) ? 0 : 1; //(1, 0) -> 0, else -> 1.
                break;
            }


            case "*":{
                d1 = d1 * d2;
                break;
            }
            case "/":{
                d1 = d1 / d2;
                break;
            }
            case "+":{
                d1 = d1 + d2;
                break;
            }
            case "-":{
                d1 = d1 - d2;
                break;
            }
            case "%":{
                d1 = d1 % d2;
                break;
            }
            case "^":{
                d1 = Math.pow(d1, d2);
                break;
            }

        } //end inner switch relop.

        String val = null;
        if(Math.floor(d1) == d1) //is integer [4.0, 5.0]
            val = Integer.toString((int)d1);
        else
            val = Double.toString(d1); //double [4.0001]
        return val;
    }
}
