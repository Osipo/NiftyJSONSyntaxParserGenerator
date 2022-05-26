package bmstu.iu7m.osipov.services.interpret;

import bmstu.iu7m.osipov.services.grammars.AstSymbol;
import bmstu.iu7m.osipov.structures.graphs.Elem;
import bmstu.iu7m.osipov.structures.graphs.Pair;
import bmstu.iu7m.osipov.structures.lists.LinkedStack;
import bmstu.iu7m.osipov.structures.trees.Node;
import bmstu.iu7m.osipov.structures.trees.PositionalTree;
import bmstu.iu7m.osipov.structures.trees.VisitorMode;
import bmstu.iu7m.osipov.structures.trees.VisitorsNextIteration;
import bmstu.iu7m.osipov.utils.ProcessNumber;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class BaseInterpreter {

    private boolean skip = false;

    public void interpret(PositionalTree<AstSymbol> ast) {
        AtomicReference<Env> env = new AtomicReference<>();
        env.set(new Env(null));
        LinkedStack<String> exp = new LinkedStack<>();
        LinkedStack<List<Elem<Object>>> lists = new LinkedStack<>();
        ArrayList<List<Elem<Object>>> indices = new ArrayList<>();

        ArrayList<Variable> params = new ArrayList<>();
        LinkedStack<FunctionInterpreter> functions = new LinkedStack<>();
        LinkedStack<ArrayList<Object>> args = new LinkedStack<ArrayList<Object>>();

        VisitorsNextIteration<AstSymbol> nextItr = new VisitorsNextIteration<>();

        ast.visit(VisitorMode.PRE, (n) -> {
            if(n.getValue().getType().equals("program"))
                env.set(new Env(env.get()));
            else if(n.getValue().getType().equals("end"))
                env.set(env.get().getPrev());
            else if(n.getValue().getType().equals("assign")) {
                ast.visitFrom(VisitorMode.POST, (c, next) -> {
                    try {
                        applyOperation(ast, env, c, exp, lists, indices, params, functions, args, nextItr);
                    } catch (Exception e) {
                        System.err.println(e);
                    }
                }, n, nextItr);
            }
        });
    }

    private void execFunction(FunctionInterpreter f, PositionalTree<AstSymbol> ast, LinkedStack<String> exp, LinkedStack<FunctionInterpreter> functions, VisitorsNextIteration<AstSymbol> nextItr){
        AtomicReference<Env> env2 = new AtomicReference<>();
        env2.set(f.getContext());
        Node<AstSymbol> root = f.getRoot();

        LinkedStack<List<Elem<Object>>> lists = new LinkedStack<>();
        ArrayList<List<Elem<Object>>> indices = new ArrayList<>();

        ArrayList<Variable> params = new ArrayList<>();
        LinkedStack<ArrayList<Object>> args = new LinkedStack<ArrayList<Object>>();

        ast.visitFrom(VisitorMode.PRE, p -> {
            if(p.getValue().getType().equals("program"))
                env2.set(new Env(env2.get()));
            else if(p.getValue().getType().equals("end"))
                env2.set(env2.get().getPrev());
            else if ( (ast.parent(p) != null && ast.parent(p).getValue().getType().equals("program")) ||
                      (ast.parent(p) == null) //is root.
            )
            {
                ast.visitFrom(VisitorMode.POST, (c, next) -> {
                    try {
                        applyOperation(ast, env2, c, exp, lists, indices, params, functions, args, nextItr);
                    } catch (Exception e) {
                        System.err.println(e);
                    }
                }, p, nextItr);
            }
        }, root);
    }

    private void applyOperation(PositionalTree<AstSymbol> ast, AtomicReference<Env> context, Node<AstSymbol> cur,
                                LinkedStack<String> exp, LinkedStack<List<Elem<Object>>> lists,
                                ArrayList<List<Elem<Object>>> indices, ArrayList<Variable> params,
                                LinkedStack<FunctionInterpreter> functions,
                                LinkedStack<ArrayList<Object>> args,
                                VisitorsNextIteration<AstSymbol> nextIteration) throws Exception {
        if (context == null || cur == null || cur.getValue() == null)
            return;

        String opType = cur.getValue().getType();
        String nodeVal = cur.getValue().getValue();
        switch (opType){
            case "char": case "string": {
                checkList(ast.parent(cur), context.get(), opType, nodeVal, exp, lists, args);
                break;
            }
            case "number":{
                nodeVal = ProcessNumber.parseNumber(nodeVal) + ""; //get parsed double as str.
                checkList(ast.parent(cur), context.get(), opType, nodeVal, exp, lists, args);
                break;
            }
            case "start":{
                if(ast.parent(cur).getValue().getType().equals("list")) { //start > list
                    ArrayList<Elem<Object>> items = new ArrayList<>();
                    lists.push(items);
                }
                else if(ast.parent(cur).getValue().getType().equals("args")){ //start > args
                    ArrayList<Object> args_i = new ArrayList<>();
                    args.push(args_i);
                }
                break;
            }
            case "list": {
                if(ast.parent(cur).getValue().getType().equals("list")){
                    //inner list
                    List<Elem<Object>> inner = lists.top();
                    lists.pop(); //remove inner list from stack.
                    lists.top().add(new Elem<>(inner)); //add list as single item instead of addAll of its items.
                }
                else if(ast.parent(cur).getValue().getType().equals("access")){
                    ArrayList<Elem<Object>> idx_items = new ArrayList<>(lists.top()); //move list from lists to indices
                    lists.pop();
                    indices.add(idx_items); //lists > indices.
                }
                break;
            }
            case "variable": {
                if(ast.parent(cur).getValue().getType().equals("params")){ //add parameter to params list.
                    params.add(new Variable(nodeVal, 1)); //category = 1 means parameter.
                    return;
                } //end  lambda variable_parameters.
                checkAssign(ast, cur, context.get(), opType, nodeVal, exp, lists, indices, functions, args);
                break;
            }
            case "params": { //move all vars from params to context.

                Node<AstSymbol> f_body = ast.rightSibling(cur);

                //delete connection of f_body with lambda node.
                //ast.detachNode(f_body);

                //skip next iteration for f_body node.
                nextIteration.setOpts(1); //just skip siblings.

                //and create function of first order.
                FunctionInterpreter fun = new FunctionInterpreter(f_body, context.get(), params);
                functions.push(fun);
                params.clear();
                break;
            }

            case "lambda": {
                if(ast.parent(cur) != null && ast.parent(cur).getValue().getType().equals("list")){ //lambda in list/items.
                    lists.top().add(new Elem<>(functions.top())); //move lambda from stack to list.
                    //System.out.println("lambda function added to list");
                    functions.pop();
                }
                nextIteration.setOpts(0); //flush skip flag.
                break;
            }
            case "args": { //args > call/functionName
                if(ast.parent(cur).getValue().getType().equals("call")){
                    ArrayList<Object> args_i = args.top();
                    FunctionInterpreter f = context.get().get(ast.parent(cur).getValue().getValue(), v -> v.getFunction() != null).getFunction();
                    f.bindArguments(args_i); //throw Exception if cannot bind.
                    args_i.clear();// flush processed args.
                    args.pop();
                    execFunction(f, ast, exp, functions, nextIteration);

                    //get next arguments caller. (args + args)
                    if(exp.top() == null && ast.rightSibling(cur) != null && ast.rightSibling(cur).getValue().getType().equals("args")){
                        System.out.println("Function '" + f.getFunctionName() + "' returns new lambda function");

                        Variable f_2 = new Variable("0$_" + f.getFunctionName());
                        f_2.setFunction(functions.top());
                        functions.pop();
                        context.get().add(f_2); //add generated variable (name is illegal for input) of N + 1 function

                        System.out.println("Call anonymous returned function: " + f_2.getValue());

                        ast.parent(cur).getValue().setValue(f_2.getValue()); //change current call node of args to anonymous func

                        break;
                    }
                    else if(exp.top() == null) {
                        System.out.println("Function '" + f.getFunctionName() + "' returns new lambda function");
                        System.out.println(functions);
                        break;
                    }

                    System.out.println("call " + f.getFunctionName() + " = " + exp.top());
                    // args > call > args (function call is expression of argument of another function call)
                    if(ast.parent(ast.parent(cur)).getValue().getType().equals("args")){
                        args.top().add(exp.top());
                        exp.pop();
                    }
                    //args > call > list
                    else if(ast.parent(ast.parent(cur)).getValue().getType().equals("list")){
                        lists.top().add(new Elem<>(exp.top()));
                        exp.pop();
                    }
                }
                break;
            }
            // unary operators (such as sign ('-', '+') or inc, dec ('++', '--').
            case "unaryop":{
                String t1 = exp.top();
                exp.pop();
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
                }
                String val = null;
                if(Math.floor(d1) == d1) //is integer [4.0, 5.0]
                    val = Integer.toString((int)d1);
                else
                    val = Double.toString(d1); //double [4.0001]

                if(ast.parent(cur) == null)
                    exp.push(val);
                else
                    checkList(ast.parent(cur), context.get(), opType, val, exp, lists, args);
                break;
            }

            case "operator": {
                String t1 = exp.top();
                exp.pop();
                String t2 = exp.top();
                exp.pop();

                double d2 = ProcessNumber.parseNumber(t1); //because of STACK exp.
                double d1 = ProcessNumber.parseNumber(t2);
                switch (nodeVal){
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
                } //end inner switch of nodeVal

                String val = null;
                if(Math.floor(d1) == d1) //is integer [4.0, 5.0]
                    val = Integer.toString((int)d1);
                else
                    val = Double.toString(d1); //double [4.0001]

                if(ast.parent(cur) == null)
                    exp.push(val);
                else
                    checkList(ast.parent(cur), context.get(), opType, val, exp, lists, args);
                break;
            } // end operator
        } //end switch of nodeType
    } //end method

    private void checkAssign(PositionalTree<AstSymbol> ast, Node<AstSymbol> cur, Env context,
                             String nType,
                             String nVal,
                             LinkedStack<String> exp,
                             LinkedStack<List<Elem<Object>>> lists,
                             ArrayList<List<Elem<Object>>> indices,
                             LinkedStack<FunctionInterpreter> functions,
                             LinkedStack<ArrayList<Object>> args) throws Exception {
        Variable v = null;
        //System.out.println("expr = " + exp.top() + " / " + nVal);
        //System.out.println("Parent: " + ast.parent(cur).getValue());

        if(ast.parent(cur).getValue().getType().equals("assign") && ast.rightSibling(cur) == null){ //variable parent is assign and it is lvalue (exp = var)
            if(lists.top() != null){ //list expression.
                v = new Variable(nVal); //ast.value (variable name)
                context.add(v);
                v.setItems(lists.top());
                v.setStrVal(v.getItems().toString());
                System.out.println(nVal + " = " + v.getStrVal());
                lists.pop();
                return;
            } //end list expression.
            else if(indices.size() != 0){ //access expression with assign operator. (a[idx] = exp)
                v = context.get(nVal); //get root list.
                if(v == null)
                    throw new Exception("Cannot find variable with name \'" + nVal + "\'. Define variable before use it!");

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
                            Object j_item = ((List) prev_list.get(0).getV1()).get(j); //extract j_item of list.
                            if(j_item instanceof Elem)
                                content.add((Elem) j_item);
                        }
                        else
                            content.add(prev_list.get(j));

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
                System.out.println("access: " + nVal + " = " + v.getStrVal());
                return;
            } //end indices.
            else { //simple expression. (nor access nor list [a = exp])
                v = new Variable(nVal); //ast.value (variable name)
                context.add(v);
                if(!functions.isEmpty()){ //expression is function [a = function]
                    v.setFunction(functions.top());
                    functions.pop();
                }
                else { //expression is literal [a = expr]
                    v.setStrVal(exp.top());
                    System.out.println(nVal + " = " + v.getStrVal());
                    exp.pop();
                }
                return;
            }
        } //end assing type.
        v = context.get(nVal);
        if(v == null)
            throw new Exception("Cannot find variable with name \'" + nVal + "\'. Define variable before use it!");
        checkAccess(ast, cur, v, context, exp, lists, indices, functions, args);
    }

    private void checkAccess(PositionalTree<AstSymbol> ast, Node<AstSymbol> cur, Variable v,
                             Env context,  LinkedStack<String> exp, LinkedStack<List<Elem<Object>>> lists,
                             ArrayList<List<Elem<Object>>> indices, LinkedStack<FunctionInterpreter> functions,
                             LinkedStack<ArrayList<Object>> args) throws Exception
    {
        Node<AstSymbol> parent = ast.parent(cur);
        if(parent.getValue().getType().equals("access")){ //variable > access

            int offset = ast.getChildren(ast.leftMostChild(parent)).size(); //access/list > access/indices > count(children)
            List<Elem<Object>> content = scanAccess(v, indices, offset);

            if(ast.parent(parent).getValue().getType().equals("list")){ //variable > access > list
                lists.top().addAll(content);
            }
            else if(ast.parent(parent).getValue().getType().equals("args")) { //variable > access > args
                if(content.size() == 1)
                    args.top().add(content.get(0));
                else
                    args.top().add(content);
            }
            else if(content.size() == 1 && content.get(0).getV1() instanceof FunctionInterpreter){ //function element
                functions.push((FunctionInterpreter) content.get(0).getV1());
            }
            else if(content.size() == 1) { //primitive element
                exp.push(content.get(0).getV1().toString()); //get first elem
            }
            else if(content.size() > 1) //list expression. (list element)
                lists.push(content);
        }
        else if (parent.getValue().getType().equals("list")) // variable > list.
            lists.top().add(new Elem<>(v.getStrVal()));
        else if(parent.getValue().getType().equals("args")) // variable > args.
            args.top().add(v);
        else
            exp.push(v.getStrVal());
    }

    private void checkList(Node<AstSymbol> parent, Env context, String nType, String nVal, LinkedStack<String> exp, LinkedStack<List<Elem<Object>>> lists, LinkedStack<ArrayList<Object>> args) throws Exception {
        if(parent.getValue().getType().equals("list"))
            lists.top().add(new Elem<>(nVal));
        else if(parent.getValue().getType().equals("args"))
            args.top().add(nVal);
        else
            exp.push(nVal);
    }

    private List<Elem<Object>> scanAccess(Variable v, ArrayList<List<Elem<Object>>> indices, int offset){
        ArrayList<Elem<Object>> content = new ArrayList<>(); //extracted content.
        List<Elem<Object>> prev_list = new ArrayList<>(v.getItems());

        int i = indices.size() - offset;
        int delStart = i;
        for (; i < indices.size(); i++) {

            //extract each ptr (number in brackets [])
            for(Elem<Object> ptr : indices.get(i)){
                Integer j = null;
                if(ptr.getV1() instanceof String)
                    j = (Integer) ProcessNumber.parseNumber((String) ptr.getV1(), Integer.class);//Integer.parseInt((String) ptr.getV1());
                else if(ptr.getV1() instanceof Integer)
                    j = (Integer) ptr.getV1();


                if(i > 0 && prev_list.size() == 1 && prev_list.get(0).getV1() instanceof List){
                    Object j_item = ((List) prev_list.get(0).getV1()).get(j); //extract j_item of list.
                    if(j_item instanceof Elem)
                        content.add((Elem) j_item);
                }
                else
                    content.add(prev_list.get(j));

            }
            prev_list.clear();
            prev_list.addAll(content); //switch to current extracted content after iteration.
            content.clear();

            //next iteration content will be scanned.

            indices.get(i).clear(); //remove read ptrs at current index.
        }
        indices.subList(delStart, indices.size()).clear(); //remove all elements at [delStart...indices.size() - 1]
        return prev_list;
    }

} //end class