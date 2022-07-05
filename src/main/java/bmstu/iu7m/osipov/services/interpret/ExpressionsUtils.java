package bmstu.iu7m.osipov.services.interpret;

import bmstu.iu7m.osipov.structures.graphs.Elem;
import bmstu.iu7m.osipov.utils.ProcessNumber;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExpressionsUtils {

    //NUMBER AND NUMBER
    public static void ParseNumberNumberExpr(Object n1, Object n2, String op, Variable v) throws Exception {
        String t1 = null;
        String t2 = null;
        Double r_d1 = 0D;
        Double r_d2 = 0D;
        double d1 = 0d;
        double d2 = 0d;
        if(n1 instanceof String) {
            t1 = (String) n1;
            r_d1 = ProcessNumber.parseOnlyNumber(t1);
        }
        else if(n1 instanceof Number) {
            r_d1 = ((Number) n1).doubleValue();
        }

        if(n2 instanceof String) {
            t2 = (String) n2;
            r_d2 = ProcessNumber.parseOnlyNumber(t2);
        }
        else if(n2 instanceof Number) {
            r_d2 = ((Number) n2).doubleValue();
        }


        //cannot parse. NaN is illegal for str_numbers
        if(r_d1 == null || r_d2 == null){
            if(t1 == null)
                t1 = ((Double) d1).toString();//num to str
            if(t2 == null)
                t2 = ((Double) d2).toString();//num to str
            ParseAllStrVar(t1, t2, r_d1, r_d2, op, v); //op with strings.
            return;
        }
        else {
            d1 = r_d1;
            d2 = r_d2;
        }

        switch (op){
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

        if(v != null) {
            v.setStrVal(val); //change type of variable if it was not primitive.
            v.setItems(null);
            v.setFunction(null);
            System.out.println(v.getValue() + " " + op + "= " + v.getStrVal());
        }
    }

    //One of operand was str that can not be parsed to valid number. So all expression will be a single str.
    //If op is relop => then returned type is boolean that is [1..] as true, and false at other cases.
    public static Object AllStrExpr(String s1, String s2, Double sn_1, Double sn_2, String op) throws Exception {
        int b = 2;
        String str = null;
        double l1 = (sn_1 != null) ? sn_1 : s1.length();
        double l2 = (sn_2 != null) ? sn_2 : s2.length();

        Double num = (sn_1 != null) ? sn_1 : sn_2;
        String rep_str = (sn_1 != null) ? s1 : s2;

        switch (op){
            case "<":{
                b = (l1 < l2) ? 1 : 0;
                break;
            }
            case "<=":{
                b = (l1 <= l2) ? 1 : 0;
                break;
            }
            case ">":{
                b = (l1 > l2) ? 1 : 0;
                break;
            }
            case ">=":{
                b = (l1 >= l2) ? 1 : 0;
                break;
            }
            case "==":{
                b = (l1 == l2) ? 1 : 0;
                break;
            }
            case "<>":{
                b = (l1 != l2) ? 1 : 0;
                break;
            }
            case "&&": case "and": {
                b = (l1 >= 1 && l2 >= 1) ? 1 : 0;
                break;
            }
            case "||": case "or": {
                b = (l1 >= 1 || l2 >= 1) ? 1 : 0;
                break;
            }
            case "=>":{
                b = (l1 >= 1 && l2 <= 1) ? 0 : 1; //(1, 0) -> 0, else -> 1.
                break;
            }

            case "*": {
                StringBuilder sb = new StringBuilder();
                if(num == null) {
                    for (int ii = 0; ii < s1.length(); ii++)
                        for (int jj = 0; jj < s2.length(); jj++)
                            sb.append(s1.charAt(ii)).append(s2.charAt(jj));
                }
                else{
                    int r = (int)Math.floor(num);
                    sb.append(rep_str.repeat(r)); //repeat str r times.
                }
                str = sb.toString();
                break;
            }
            case "+":{
                str = s1.concat(s2);
                break;
            }
            case "-":{
                str = s1.replace(s2, "");
            }
            default: {
                throw new Exception("Operator '" + op + "' is not supported.");
            }

        } //end inner switch relop.

        if(b != 2) //if boolean return it.
            return b;

        return str;
    }

    public static void ParseAllStrVar(String s1, String s2, Double sn_1, Double sn_2, String op, Variable v) throws Exception {
        Object str_or_bool_exp = AllStrExpr(s1, s2, sn_1, sn_2, op);
        if(v != null) {
            v.setStrVal(str_or_bool_exp.toString()); //change type of variable if it was not primitive.
            v.setItems(null);
            v.setFunction(null);
            System.out.println(v.getValue() + " " + op + "= " + v.getStrVal());
        }
    }

    //NUMBER AND NUMBER
    public static Object ParseNumberNumberExpr(Object n1, Object n2, String op) throws Exception {
        String t1 = null;
        String t2 = null;
        Double r_d1 = 0D;
        Double r_d2 = 0D;
        double d1 = 0d;
        double d2 = 0d;
        if(n1 instanceof String) {
            t1 = (String) n1;
            r_d1 = ProcessNumber.parseOnlyNumber(t1);
        }
        else if(n1 instanceof Number) {
            r_d1 = ((Number) n1).doubleValue();
        }

        if(n2 instanceof String) {
            t2 = (String) n2;
            r_d2 = ProcessNumber.parseOnlyNumber(t2);
        }
        else if(n2 instanceof Number) {
            r_d2 = ((Number) n2).doubleValue();
        }

        //cannot parse. NaN is illegal for str_numbers
        if(r_d1 == null || r_d2 == null){
            if(t1 == null)
                t1 = ((Double) d1).toString();//num to str
            if(t2 == null)
                t2 = ((Double) d2).toString();//num to str
            return AllStrExpr(t1, t2, r_d1, r_d2, op); //op with strings.
        }
        else {
            d1 = r_d1;
            d2 = r_d2;
        }

        switch (op){
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
            default: {
                throw new Exception("Operator '" + op + "' is not supported.");
            }

        } //end inner switch relop.

        String val = null;
        if(Math.floor(d1) == d1) //is integer [4.0, 5.0]
            val = Integer.toString((int)d1);
        else
            val = Double.toString(d1); //double [4.0001]
        return val;
    }


    //LIST *= NUMBER.
    public static void ParseListAndNumberExpr(Object n1, Object n2, String op, Variable v) throws Exception {
        List<Elem<Object>> t1 = null;
        String t2 = "";
        Double r_d2 = 0d;
        double d2 = 0d;
        if(n1 instanceof List) {
            t1 = (List<Elem<Object>>) n1;
        }
        if(n2 instanceof String){
            t2 = (String) n2;
            r_d2 = ProcessNumber.parseOnlyNumber(t2);
        }
        else if(n2 instanceof Number)
            r_d2 = ((Number) n2).doubleValue();

        //cannot parse str_num n2
        if(r_d2 == null){
            ParseNumberAndList(t2, t1, op, v);
            return;
        }
        else{
            d2 = r_d2;
        }

        AddToEachItem(t1, d2, op);

        if(v != null) {
            v.setItems(t1);
            v.setStrVal(v.getItems().toString());
            v.setFunction(null);
            System.out.println(v.getValue() + " " + op + "= " + v.getStrVal());
        }
    }

    //LIST *= NUMBER.
    public static Object ParseListAndNumberExpr(Object n1, Object n2, String op) throws Exception {
        List<Elem<Object>> t1 = null;
        String t2 = "";
        Double r_d2 = 0d;
        double d2 = 0d;
        if(n1 instanceof List) {
            t1 = (List<Elem<Object>>) n1;
        }
        if(n2 instanceof String){
            t2 = (String) n2;
            r_d2 = ProcessNumber.parseOnlyNumber(t2);
        }
        else if(n2 instanceof Number)
            r_d2 = ((Number) n2).doubleValue();

        //cannot parse str_num n2
        if(r_d2 == null){
            return ParseNumberAndList(t2, t1, op);
        }
        else{
            d2 = r_d2;
        }
        AddToEachItem(t1, d2, op); //recursive call for inner lists.
        return t1;
    }

    private static void AddToEachItem(List<Elem<Object>> items, double value, String op){
        ArithmeticOperation method = null;
        switch (op){
            case "*":{
                method = (i, v) -> {
                    String s = i.getV1().toString();
                    double d1 = ProcessNumber.parseNumber(s);
                    d1 = d1 * v;
                    i.setV1(d1);
                };
                break;
            } //end case.
            case "/":{
                method = (i, v) -> {
                    String s = i.getV1().toString();
                    double d1 = ProcessNumber.parseNumber(s);
                    d1 = d1 / v;
                    i.setV1(d1);
                };
                break;
            }
            case "+":{
                method = (i, v) -> {
                    String s = i.getV1().toString();
                    double d1 = ProcessNumber.parseNumber(s);
                    d1 = d1 + v;
                    i.setV1(d1);
                };
                break;
            }
            case "-":{
                method = (i, v) -> {
                    String s = i.getV1().toString();
                    double d1 = ProcessNumber.parseNumber(s);
                    d1 = d1 - v;
                    i.setV1(d1);
                };
                break;
            }
            case "%":{
                method = (i, v) -> {
                    String s = i.getV1().toString();
                    double d1 = ProcessNumber.parseNumber(s);
                    d1 = d1 % v;
                    i.setV1(d1);
                };
                break;
            }
            case "^":{
                method = (i, v) -> {
                    String s = i.getV1().toString();
                    double d1 = ProcessNumber.parseNumber(s);
                    d1 = Math.pow(d1, v);
                    i.setV1(d1);
                };
                break;
            }
        } //end inner switch

        for(Elem<Object> item : items){
            if(item.getV1() instanceof List)
                AddToEachItem((List<Elem<Object>>) item.getV1(), value, op);
            else if(item.getV1() instanceof String || item.getV1() instanceof Number){
                method.apply(item, value);
            }
        } // end for.
    }

    //NUMBER AND LIST
    //NUMBER += LIST
    public static void ParseNumberAndList(Object n1, Object n2, String op, Variable v) throws Exception{
        List<Elem<Object>> t2 = null;
        String t1 = "";
        Double r_d2 = 0D;
        double d2 = 0d;
        String val = null;
        if(n2 instanceof List) {
            t2 = (List<Elem<Object>>) n2;
        }
        if(n1 instanceof String){
            t1 = (String) n1;
            r_d2 = ProcessNumber.parseOnlyNumber(t1);
        }
        else if(n1 instanceof Number) {
            r_d2 = ((Number) n1).doubleValue();
        }

        //cannot parse str_num n1
        if(r_d2 == null){
            val = t1;
        }
        else
            d2 = r_d2;
        if(Math.floor(d2) == d2 && val == null) //is integer [4.0, 5.0]
            val = Integer.toString((int)d2);
        else if(val == null)
            val = Double.toString(d2); //double [4.0001]

        switch (op){
            case "+":{
                t2.add(new Elem<>(val));
                break;
            }
            case "-":{
                t2.removeAll(Collections.singleton(new Elem(val)));
                break;
            }
            default:{
                throw new Exception("Operator '" + op + "' is not supported.");
            }
        }

        if(v != null) {
            v.setItems(t2);
            v.setStrVal(v.getItems().toString());
            v.setFunction(null);
            System.out.println(v.getValue() + " " + op + "= " + v.getStrVal());
        }
    }

    public static Object ParseNumberAndList(Object n1, Object n2, String op) throws Exception {
        List<Elem<Object>> t2 = null;
        String t1 = "";
        Double r_d2 = 0D;
        double d2 = 0d;
        String val = null;
        if(n2 instanceof List) {
            t2 = (List<Elem<Object>>) n2;
        }
        if(n1 instanceof String){
            t1 = (String) n1;
            r_d2 = ProcessNumber.parseOnlyNumber(t1);
        }
        else if(n1 instanceof Number) {
            r_d2 = ((Number) n1).doubleValue();
        }
        //cannot parse str_num n1
        if(r_d2 == null){
            val = t1;
        }
        else
            d2 = r_d2;
        if(Math.floor(d2) == d2 && val == null) //is integer [4.0, 5.0]
            val = Integer.toString((int)d2);
        else if(val == null)
            val = Double.toString(d2); //double [4.0001]

        switch (op){
            case "+":{
                t2.add(new Elem<>(val));
                break;
            }
            case "-":{
                t2.removeAll(Collections.singleton(new Elem(val)));
                break;
            }
            default:{
                throw new Exception("Operator '" + op + "' is not supported.");
            }
        }
        return t2;
    }

    //LIST AND LIST
    public static void ParseListAndListExpr(Object n1, Object n2, String op, Variable v) throws Exception {
        List<Elem<Object>> t1 = null;
        List<Elem<Object>> t2 = null;
        if(n1 instanceof List && n2 instanceof List) {
            t1 = (List<Elem<Object>>) n1;
            t2 = (List<Elem<Object>>) n2;
        }

        List<Elem<Object>> nList = t1;
        switch (op){
            case "+": {
                nList.addAll(t2);
                break;
            }
            case "-": {
                nList.removeAll(t2);
                break;
            }
            case "*": {
                nList = MakeCartesian(nList, t2);
                break;
            }
            default: {
                throw new Exception("Operator '" + op + "' is not defined for lists.");
            }
        }

        if(v != null) {
            v.setItems(nList);
            v.setStrVal(v.getItems().toString());
            v.setFunction(null);
            System.out.println(v.getValue() + " " + op + "= " + v.getStrVal());
        }
    }

    //LIST AND LIST
    public static Object ParseListAndListExpr(Object n1, Object n2, String op) throws Exception {
        List<Elem<Object>> t1 = null;
        List<Elem<Object>> t2 = null;
        if(n1 instanceof List && n2 instanceof List) {
            t1 = (List<Elem<Object>>) n1;
            t2 = (List<Elem<Object>>) n2;
        }

        List<Elem<Object>> nList = t1;
        switch (op){
            case "+": {
                nList.addAll(t2);
                break;
            }
            case "-": {
                nList.removeAll(t2);
                break;
            }
            case "*": {
                nList = MakeCartesian(nList, t2);
                break;
            }
            default: {
                throw new Exception("Operator '" + op + "' is not defined for lists.");
            }
        }
        return nList;
    }

    private static List<Elem<Object>> MakeCartesian(List<Elem<Object>> a, List<Elem<Object>> b){
        List<Elem<Object>> result = new ArrayList<>();
        List<Elem<Object>> v_i = null;
        for(Elem<?> item : a){
            for(Elem<?> item2: b){
                v_i = new ArrayList<>();
                v_i.add(new Elem<>(item.getV1()));
                v_i.add(new Elem<>(item2.getV1()));
                result.add(new Elem<>(v_i)); //add result vector.
            } //inner for
        }// for
        return result;
    }

    public static void ParseListAndFunction(Object n1, Object n2, String op, Variable v) throws Exception {
        List<Elem<Object>> t1 = null;
        FunctionInterpreter t2 = null;
        if(n1 instanceof List && n2 instanceof FunctionInterpreter){
            t1 = (List<Elem<Object>>) n1;
            t2 = (FunctionInterpreter) n2;
        }
        switch (op){
            case "+": {
                t1.add(new Elem<>(t2)); //add f to list.
                break;
            }
            case "-":{
                t1.removeAll(Collections.singleton(new Elem<>(t2)));
                break;
            }
            default: {
                throw new Exception("Operator '" + op + "' is not defined for operands [list, function].");
            }
        }
        if(v != null) {
            v.setItems(t1);
            v.setStrVal(v.getItems().toString());
            v.setFunction(null);
            System.out.println(v.getValue() + " " + op + "= " + v.getStrVal());
        }
    }

    public static Object ParseListAndFunction(Object n1, Object n2, String op) throws Exception {
        List<Elem<Object>> t1 = null;
        FunctionInterpreter f = null;
        if(n1 instanceof List && n2 instanceof FunctionInterpreter){
            t1 = (List<Elem<Object>>) n1;
            f = (FunctionInterpreter) n2;
        }
        switch (op){
            case "+": {
                t1.add(new Elem<>(f)); //add f to list.
                break;
            }
            case "-":{
                t1.removeAll(Collections.singleton(new Elem<>(f)));
                break;
            }
            default: {
                throw new Exception("Operator '" + op + "' is not defined for operands [list, function].");
            }
        }
        return t1;
    }
}