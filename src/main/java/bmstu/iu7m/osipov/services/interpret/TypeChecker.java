package bmstu.iu7m.osipov.services.interpret;

import bmstu.iu7m.osipov.services.grammars.AstSymbol;
import bmstu.iu7m.osipov.structures.graphs.Elem;
import bmstu.iu7m.osipov.structures.lists.LinkedStack;
import bmstu.iu7m.osipov.structures.trees.Node;
import bmstu.iu7m.osipov.structures.trees.PositionalTree;
import bmstu.iu7m.osipov.structures.trees.PositionalTreeUtils;
import bmstu.iu7m.osipov.utils.ProcessNumber;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TypeChecker {

    public static Object GetRawValue(Object value){
        return value;
    }

    public static String GetStrValue(Object value){
        return value.toString();
    }

    public static void saveExpressionInVariable(Variable v, Object expr) throws Exception {
        if(expr == null)
            throw new NullPointerException("Error! Expect non-nullable value to be assigned!");
        if(expr instanceof Elem<?>)
            while (expr instanceof Elem<?>)
                expr = ((Elem<?>) expr).getV1();
        if(expr instanceof String || expr instanceof Number){
            v.setStrVal(expr.toString());
        }
        else if(expr instanceof List){
            //System.out.println("to " + v.getValue() + " assign list ");

            List<Elem<Object>> el = (List<Elem<Object>>) expr;

            /* LOG
            for(int ii = 0; ii < el.size(); ii++) {
                if (el.get(ii).getV1() instanceof List) {
                    List<Elem<Object>> el_el1 = (List<Elem<Object>>) el.get(ii).getV1();
                    for (int i = 1; i < el_el1.size(); i++)
                        System.out.print(el_el1.get(i).getV1().toString() + " ");
                }
                System.out.print("\n");
            }
            System.out.println("---");
            */

            v.setItems(el);
            v.setStrVal(v.getItems().toString());
        }
        else if(expr instanceof FunctionInterpreter){
            v.setFunction((FunctionInterpreter) expr);
        }
    }

    //Get concrete value extracted from Stack expressions.
    public static Object CheckValue(Object value, String info) throws Exception {
        if(info == null)
            info = "";
        if(value == null)
            throw new NullPointerException("Cannot compute value. " + info + " Value is null.");

        //extract elem.
        if(value instanceof Elem<?>)
            while(value instanceof Elem<?>)
                value = ((Elem<?>) value).getV1();

        if(value instanceof String || value instanceof Number || value instanceof List || value instanceof FunctionInterpreter)
            return value;
        else if(value instanceof Variable){
            Variable vv = (Variable) value;
            if(vv.getSubModule() != null)
                return vv.getSubModule();
            else if(vv.getFunction() != null)
                return vv.getFunction();
            else if(vv.isList())
                return vv.getItems();
            else {
                return vv.getStrVal(); //primitive -> return str.
            }
        }
        else
            throw new Exception("Cannot define value for type: " + value.getClass());
    }

    //Return type of the variable.
    public static int CheckVariableType(Variable var, String info, boolean isNew) throws Exception {
        if(info == null)
            info = "";

        if(var == null)
            throw new NullPointerException("Cannot define type of variable '" + info + "'. Variable is not defined.");
        if(var.getSubModule() != null)
            return 4; //4 => dictionary or subModule.
        if(var.getFunction() != null)
            return 3; //3 => function.
        else if(var.isList())
            return 2; //2 => list.
        else if(var.getStrVal() != null)
            return 1; //1 => primitive.
        else if(isNew)
            return 0; //0 => new variable defined.
        else
            throw new Exception("Cannot define type for variable: '" + var.getValue() + "'. Variable is not initialized.");
    }

    public static Object CheckExpressionType(LinkedStack<Object> expr, String etype, String op) throws Exception {
        Object val1 = expr.top();
        expr.pop();
        if(etype.equals("unaryop"))
           return ParseUnaryOperator(val1, op);
        else if(etype.equals("operator") || etype.equals("relop") || etype.equals("boolop")) {
            Object val2 = expr.top();
            expr.pop();
            return CombineExpression(val2, val1, op);
            //return ParseBinaryOperator(val2, val1, op);
        }
        else
            return val1;
    }

    private static Object ParseUnaryOperator(Object n, String nodeVal) throws Exception {
        String t1 = "";
        Double r_d1 = 0d;
        double d1 = 0;
        if(n instanceof String) {
            t1 = (String) n;
            r_d1 = ProcessNumber.parseOnlyNumber(t1);
        }
        else if(n instanceof Number){
            r_d1 = ((Number) n).doubleValue();
        }
        if(r_d1 != null) //str_num n parsed successful.
            d1 = r_d1;

        else
            throw new Exception("Unary operators defined only for numeric type (numbers)!");

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

    //TODO: Expect Raw Values got from CheckValue()
    private static Object CombineExpression(Object oldVal, Object expVal, String op) throws Exception {

        //Raw number.
        if(oldVal instanceof Number && (expVal instanceof Number || expVal instanceof String))
            return ExpressionsUtils.ParseNumberNumberExpr(oldVal, expVal, op);
        else if(oldVal instanceof Number && expVal instanceof List)
            return ExpressionsUtils.ParseNumberAndList(oldVal, expVal, op);
        else if(oldVal instanceof Number && expVal instanceof FunctionInterpreter)
            throw new Exception("You try using '" + op + "' with anonymous function definition.\n But the first operand is not a list.");

        if(oldVal instanceof String && (expVal instanceof String || expVal instanceof Number)) { //v += expr
            return ExpressionsUtils.ParseNumberNumberExpr(oldVal, expVal, op);
        }
        else if(oldVal instanceof String && expVal instanceof List){ //v += list.
            return ExpressionsUtils.ParseNumberAndList(oldVal, expVal, op);
        }
        else if(oldVal instanceof  String && expVal instanceof FunctionInterpreter){
            throw new Exception("You try using '" + op + "' with anonymous function definition.\n But the first operand is not a list.");
        }
        else if(oldVal instanceof List && (expVal instanceof String || expVal instanceof Number)){//list += expr
            return ExpressionsUtils.ParseListAndNumberExpr(oldVal, expVal, op);
        }
        else if(oldVal instanceof List && expVal instanceof List){//list += list
            return ExpressionsUtils.ParseListAndListExpr(oldVal, expVal, op);
        }
        else if(oldVal instanceof List && expVal instanceof FunctionInterpreter){//list += function_def
            return ExpressionsUtils.ParseListAndFunction(oldVal, expVal, op);
        }
        else if(oldVal instanceof FunctionInterpreter){
            throw new Exception("You try using '" + op + "' with function definition.\n This is illegal.");
        }
        else if(oldVal instanceof Env && expVal instanceof Env){
            return ExpressionsUtils.parseModules((Env) oldVal, (Env) expVal, op);
        }
        return null;
    }

    private static void CombineAssign(Variable v, Object oldVal, Object expVal, String op) throws Exception {
        int operationType = -1;

        //Raw number.
        if(oldVal instanceof Number && (expVal instanceof Number || expVal instanceof String))
            ExpressionsUtils.ParseNumberNumberExpr(oldVal, expVal, op, v);
        else if(oldVal instanceof Number && expVal instanceof List)
            ExpressionsUtils.ParseNumberAndList(oldVal, expVal, op, v);
        else if(oldVal instanceof Number && expVal instanceof FunctionInterpreter)
            throw new Exception("You try using '" + op + "' with anonymous function definition.\n But the first operand is not a list.");

        if(oldVal instanceof String && (expVal instanceof String || expVal instanceof Number)){ //v += expr
            operationType = 1;
            ExpressionsUtils.ParseNumberNumberExpr(oldVal, expVal, op, v);
        }
        else if(oldVal instanceof String && expVal instanceof List){//v += list.
            operationType = 2;
            ExpressionsUtils.ParseNumberAndList(oldVal, expVal, op, v);
        }
        else if(oldVal instanceof String && expVal instanceof FunctionInterpreter){//v += function_def
            throw new Exception("You try using '" + op + "' with anonymous function definition.\n But the first operand is not a list.");
        }
        else if(oldVal instanceof List && (expVal instanceof String || expVal instanceof Number)){//list += expr
            operationType = 3;
            ExpressionsUtils.ParseListAndNumberExpr(oldVal, expVal, op, v);
        }
        else if(oldVal instanceof List && expVal instanceof List){//list += list
            operationType = 4;
            ExpressionsUtils.ParseListAndListExpr(oldVal, expVal, op, v);
        }
        else if(oldVal instanceof List && expVal instanceof FunctionInterpreter){//list += function_def
            operationType = 5;
            ExpressionsUtils.ParseListAndFunction(oldVal, expVal, op, v);
        }
        else if(oldVal instanceof FunctionInterpreter){
            throw new Exception("You try using '" + op + "' with '" + v.getValue() + "' function definition.\n This is illegal.");
        }
    }

    public static void ResolveAssignOperation(  PositionalTree<AstSymbol> ast,
                                                Node<AstSymbol> curNode,
                                                Env context,
                                                LinkedStack<Object> exp,
                                                LinkedStack<List<Elem<Object>>> lists,
                                                ArrayList<List<Elem<Object>>> indices,
                                                LinkedStack<FunctionInterpreter> functions,
                                                String operator,
                                                String vName
    ) throws Exception
    {
        Variable v = null;
        int  vType = -1;
        boolean isNewVar = false;

        switch (operator){
            case "+=": case "-=": case "*=": case "/=": case "%=": case "^=": {

                v = context.get(vName);



                vType = CheckVariableType(v, vName, false); //check that both variables are defined and compute their type.
                Object oldVal = null;
                List<Elem<Object>> prev_list = null;

                //Added index check.
                if(indices.size() != 0){ //when access expression lvalue[index_1][index_2]... = expr.
                    if(v == null) //lvalue must be defined!
                        throw new Exception("Cannot find variable with name \'" + vName + "\'. Define variable before use it!");

                    ArrayList<Elem<Object>> content = new ArrayList<>(); //extracted content.
                    prev_list = new ArrayList<>(v.getItems());
                    boolean hasFunctionExpr = false;

                    for (int i = 0; i < indices.size(); i++) {

                        //extract each ptr (number in brackets [])
                        for(Elem<Object> ptr : indices.get(i)){
                            Integer j = null;
                            if(ptr.getV1() instanceof String)
                                j = (Integer) ProcessNumber.parseNumber((String) ptr.getV1(), Integer.class);//Integer.parseInt((String) ptr.getV1());
                            else if(ptr.getV1() instanceof Integer)
                                j = (Integer) ptr.getV1();

                            if(i > 0 && prev_list.size() == 1 && prev_list.get(0).getV1() instanceof List){ //prev index got list.

                                List inner_li =  ((List) prev_list.get(0).getV1());
                                Object j_item = inner_li.get( ((j < 0) ? inner_li.size() + j : j)  ); //extract j_item of list.

                                if(j_item instanceof Elem)
                                    content.add((Elem) j_item);
                            }
                            else {
                                j = (j < 0) ? prev_list.size() + j : j;
                                content.add(prev_list.get(j));
                            }
                        }
                        prev_list.clear();
                        prev_list.addAll(content); //switch to current extracted content after iteration.
                        content.clear();

                        //next iteration content will be scanned.

                        indices.get(i).clear(); //remove read ptr
                    }

                    indices.clear(); //remove read access.
                    oldVal = prev_list;
                    vType = 20;
                }

                if(vType == 3)
                    oldVal = v.getFunction();
                else if(vType == 2)
                    oldVal = v.getItems();
                else if(vType != 20)
                    oldVal = v.getStrVal();

                Object expValue = null; //attached value.
                if(lists.top() != null){
                    expValue = lists.top();
                    lists.pop();
                }
                else{
                    expValue = exp.top();
                    exp.pop();
                }

                if(prev_list != null){
                    for(int i = 0; i < prev_list.size(); i++){
                        Elem<Object> nitem = prev_list.get(i);
                        Object nval = CombineExpression(nitem.getV1(), expValue, operator.substring(0, operator.length() - 1));
                        nitem.setV1(nval);
                    }
                }
                else
                    CombineAssign(v, oldVal, expValue, operator.substring(0, operator.length() - 1));
                break;
            }
            case "=": {

                //TODO: Make independent vars for dict
                //v = context.get(vName); //if inner context presented it extracted the nearest outer block variable.
                v = context.getAtCurrent(vName); //get only from current context.

                if(lists.top() != null) {
                    if(v == null) {
                        v = new Variable(vName); //ast.value (variable name)
                        context.add(v);
                        isNewVar = true;
                    }
                    vType = CheckVariableType(v, vName, isNewVar); //if you want to enable static system type (e.g. vars in C#)

                    v.setItems(lists.top());
                    v.setStrVal(v.getItems().toString()); //error of null item!!!!!
                    System.out.println(vName + " = " + v.getStrVal());
                    lists.pop();
                    return;
                }
                else if(indices.size() != 0){ //when access expression lvalue[index_1][index_2]... = expr.
                    v = context.get(vName);
                    if(v == null) //lvalue must be defined!
                        throw new Exception("Cannot find variable with name \'" + vName + "\'. Define variable before use it!");

                    ArrayList<Elem<Object>> content = new ArrayList<>(); //extracted content.
                    List<Elem<Object>> prev_list = new ArrayList<>(v.getItems());
                    boolean hasFunctionExpr = false;

                    for (int i = 0; i < indices.size(); i++) {

                        //extract each ptr (number in brackets [])
                        for(Elem<Object> ptr : indices.get(i)){
                            Integer j = null;
                            if(ptr.getV1() instanceof String)
                                j = (Integer) ProcessNumber.parseNumber((String) ptr.getV1(), Integer.class);//Integer.parseInt((String) ptr.getV1());
                            else if(ptr.getV1() instanceof Integer)
                                j = (Integer) ptr.getV1();

                            if(i > 0 && prev_list.size() == 1 && prev_list.get(0).getV1() instanceof List){ //prev index got list.

                                List inner_li =  ((List) prev_list.get(0).getV1());
                                Object j_item = inner_li.get( ((j < 0) ? inner_li.size() + j : j)  ); //extract j_item of list.

                                if(j_item instanceof Elem)
                                    content.add((Elem) j_item);
                            }
                            else {
                                j = (j < 0) ? prev_list.size() + j : j;
                                content.add(prev_list.get(j));
                            }

                            if(!functions.isEmpty()){
                                content.get(content.size() - 1).setV1(functions.top());
                                hasFunctionExpr = true;
                            }
                            else if(!(content.get(content.size() - 1).getV1() instanceof List)) {
                                content.get(content.size() - 1).setV1(exp.top());
                                //System.out.println("expr = " + exp.top());
                                //System.out.println("changed: " + nVal + v.getStrVal());
                            }
                        }
                        prev_list.clear();
                        prev_list.addAll(content); //switch to current extracted content after iteration.
                        content.clear();

                        //next iteration content will be scanned.

                        indices.get(i).clear(); //remove read ptr
                    }

                    indices.clear(); //remove read access.

                    if(hasFunctionExpr)
                        functions.pop();
                    else
                        exp.pop(); //remove expression to be assigned for each list item.

                    v.setStrVal(v.getItems().toString());
                    System.out.println("access: " + vName + " = " + v.getStrVal());
                    return;
                }

                //Primitive Expressions or Functions.
                if (v == null) {
                    v = new Variable(vName);
                    context.add(v);
                    isNewVar = true;
                }
                vType = CheckVariableType(v, vName, isNewVar);

                //is function
                if (!functions.isEmpty()) { //expression is function [a = function]
                    v.setFunction(functions.top());;
                    functions.pop();
                }
                else if(exp.top() instanceof Env)
                {
                    //Variable is dictionary or subModule.
                    Env subModule = (Env) exp.top();
                    exp.pop();
                    v.setSubModule(subModule);

                    //PRINT Dictionary.
                    Iterator<Variable> sitr = subModule.currentIterator();
                    System.out.println(vName + " = dictionary{{");
                    while(sitr.hasNext()){
                        Variable sitem = sitr.next();
                        System.out.print("\t" + sitem.getValue() + " = ");
                        if(sitem.getFunction() != null)
                            System.out.print(" <<function>>\n");
                        else if(sitem.getSubModule() != null)
                            System.out.print(" <<dictionary>>\n");
                        else
                            System.out.print(sitem.getStrVal() + "\n");
                    }
                    System.out.println("}}");
                }
                else { //set from expression. [a = top(expr)]; Note that in the expression stack there may be lists.
                    saveExpressionInVariable(v, exp.top());
                    System.out.println(vName + " = " + v.getStrVal());
                    exp.pop();
                }
                return;
            } //end simple assign '='
        }//end switch
    }
}