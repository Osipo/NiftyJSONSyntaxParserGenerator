package bmstu.iu7m.osipov.services.interpret;

import bmstu.iu7m.osipov.services.grammars.AstNode;
import bmstu.iu7m.osipov.services.grammars.AstSymbol;
import bmstu.iu7m.osipov.structures.graphs.Elem;
import bmstu.iu7m.osipov.structures.lists.LinkedList;
import bmstu.iu7m.osipov.structures.lists.LinkedStack;
import bmstu.iu7m.osipov.structures.lists.Triple;
import bmstu.iu7m.osipov.structures.trees.*;
import bmstu.iu7m.osipov.utils.ProcessNumber;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public abstract class BaseInterpreter {

    private boolean loop = false;

    protected int blocks = 0;

    protected SequencesInterpreter curSequence = null;

    protected List<Triple<Node<AstSymbol>, Node<AstSymbol>, Integer>> labels;

    public abstract void interpret(PositionalTree<AstSymbol> ast);

    protected abstract void execFunction(FunctionInterpreter f,
                                         PositionalTree<AstSymbol> ast,
                                         LinkedStack<Object> exp,
                                         LinkedStack<FunctionInterpreter> functions,
                                         VisitorsNextIteration<AstSymbol> nextItr,
                                         LinkedList<Elem<?>> vector_i,
                                         Map<String, Integer> vnames_idxs,
                                         int vector_len);

    protected void applyOperation(PositionalTree<AstSymbol> ast, AtomicReference<Env> context, Node<AstSymbol> cur,
                                  LinkedStack<Object> exp, LinkedStack<List<Elem<Object>>> lists,
                                  ArrayList<List<Elem<Object>>> indices, ArrayList<Variable> params,
                                  LinkedStack<FunctionInterpreter> functions,
                                  LinkedStack<ArrayList<Object>> args,
                                  VisitorsNextIteration<AstSymbol> nextIteration,
                                  LinkedList<Elem<?>> vector_i,
                                  Map<String, Integer> vnames_idxs,
                                  int vector_len) throws Exception {
        if (context == null || cur == null || cur.getValue() == null)
            return;

        String opType = cur.getValue().getType();
        String nodeVal = cur.getValue().getValue();
        //System.out.println(opType + "/" + nodeVal);
        switch (opType){
            case "char": case "string": {
                checkList(ast, ast.parent(cur), cur, context.get(), opType, nodeVal, exp, lists, args, nextIteration, vector_i);
                break;
            }
            case "number": {
                nodeVal = ProcessNumber.parseNumber(nodeVal) + ""; //get parsed double as str.
                checkList(ast, ast.parent(cur), cur, context.get(), opType, nodeVal, exp, lists, args, nextIteration, vector_i);
                break;
            }
            case "pass": {
                nextIteration.setOpts(3); //3 => skip siblings add detach node.
                break;
            }
            case "loop": { //loop while
                if(loop){ //passed one iteration -> check condition again.
                    Node<AstSymbol> cond_node = ast.leftMostChild(cur);
                    nextIteration.setNextNode(cond_node);
                    this.loop = false;
                }
                break;
            }
            case "goto": {
                String finalNodeVal = nodeVal;
                int block_i = blocks;
                Triple<Node<AstSymbol>, Node<AstSymbol>, Integer>  lentry = null;
                List<Triple<Node<AstSymbol>, Node<AstSymbol>, Integer>> lentries = this.labels
                        .stream()
                        .filter(x -> x.getV2().getValue().getValue().equals(finalNodeVal)
                                && PositionalTreeUtils.isAncestorOf(ast, x.getV1(), cur)
                        ).sorted((x, y) -> y.getV3() - x.getV3()).collect(Collectors.toList());

                while(block_i > 0){
                    for(int ii = 0; ii < lentries.size(); ii++){ //for labels with name (nodeVal)
                        if(lentries.get(ii).getV3() == block_i){
                            lentry = lentries.get(ii);
                            block_i = -1; //break while.
                            break; //end for.
                        }
                    }
                    block_i--;
                }

                if(lentry == null)
                    throw new Exception("Cannot find label '" + nodeVal + "' The label must be declared within current context and only once.");

                nextIteration.setNextNode(lentry.getV2());
                nextIteration.setOpts(10);
                break;
            }
            case "start": {
                if(ast.parent(cur).getValue().getType().equals("list")) { //start > list
                    ArrayList<Elem<Object>> items = new ArrayList<>();
                    lists.push(items);
                }
                else if(ast.parent(cur).getValue().getType().equals("args")){ //start > args
                    ArrayList<Object> args_i = new ArrayList<>();
                    args.push(args_i);
                }

                // start/vector node at expressions (start > expressions > matrix)
                else if(nodeVal.equals("vector") && vector_i == null){
                    nextIteration.setOpts(1); //skip all siblings.
                    this.curSequence = new SequencesInterpreter(
                            ast, ast.parent(cur), ast.rightSibling(ast.parent(cur)), context.get()
                            ,exp, lists, indices, functions, args, this
                    );
                }
                break;
            }
            case "matrix": {
                nextIteration.setOpts(0);
                this.curSequence.generateItems(ast.parent(cur)); //parent of matrix is always list/items.
                this.curSequence = null;
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
                checkAssign(ast, cur, context.get(), opType, nodeVal, exp, lists, indices, functions, args, vector_i, vnames_idxs, vector_len);
                break;
            }
            case "params": { //move all vars from params to context.

                Node<AstSymbol> f_body = ast.rightSibling(cur);

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


                    execFunction(f, ast, exp, functions, nextIteration, vector_i, vnames_idxs, vector_len);
                    f.setContext(f.getContext().getPrev()); //set context.

                    //get next arguments caller. (args + args)
                    if(exp.top() == null && ast.rightSibling(cur) != null && ast.rightSibling(cur).getValue().getType().equals("args")){
                        //System.out.println("Function '" + f.getFunctionName() + "' returns new lambda function");

                        Variable f_2 = new Variable("0$_" + f.getFunctionName());
                        f_2.setFunction(functions.top());
                        functions.pop();
                        context.get().add(f_2); //add generated variable (name is illegal for input) of N + 1 function

                        //System.out.println("Call anonymous returned function: " + f_2.getValue());

                        ast.parent(cur).getValue().setValue(f_2.getValue()); //change current call node of args to anonymous func

                        break;
                    }
                    else if(exp.top() == null) {
                        //System.out.println("Function '" + f.getFunctionName() + "' returns new lambda function");
                        //System.out.println(functions);
                        break;
                    }

                    //System.out.println("call " + f.getFunctionName() + " = " + exp.top());
                    // args > call > args (function call is expression of argument of another function call)
                    if(ast.parent(ast.parent(cur)).getValue().getType().equals("args")){
                        args.top().add(TypeChecker.GetRawValue(exp.top()));
                        exp.pop();
                    }
                    //args > call > list
                    else if(ast.parent(ast.parent(cur)).getValue().getType().equals("list")){
                        lists.top().add(new Elem<>(TypeChecker.GetRawValue(exp.top())));
                        exp.pop();
                    }
                }
                break;
            }

            //operators
            case "unaryop":
            case "relop":
            case "boolop":
            case "operator": {
                Object val = TypeChecker.CheckExpressionType(exp, opType, nodeVal);
                if(ast.parent(cur) == null)
                    exp.push(val);
                else
                    checkList(ast, ast.parent(cur), cur, context.get(), opType, val, exp, lists, args, nextIteration, vector_i);
                break;
            }

            case "range": {
                String t1 = null;
                String t2 = null;
                double d1 = 0;
                double d2 = 0;
                if(nodeVal.equals("range")){
                    t1 = exp.top().toString();
                    exp.pop();
                    t2 = exp.top().toString();
                    exp.pop();
                    d2 = ProcessNumber.parseNumber(t1); //because of STACK exp.
                    d1 = ProcessNumber.parseNumber(t2);
                }
                else if (nodeVal.equals("rangeStart") || nodeVal.equals("rangeEnd")){
                    t1 = exp.top().toString();
                    exp.pop();
                    d1 = ProcessNumber.parseNumber(t1);
                }

                if(nodeVal.equals("rangeEnd")){
                    d2 = d1;
                    d1 = 0; // [0..d1]
                }

                //range > list > access.
                boolean isAccess = ast.parent(cur).getValue().getType().equals("list") && ast.parent(ast.parent(cur)).getValue().getType().equals("access");

                if(nodeVal.equals("rangeStart") && isAccess)
                {
                    List<Node<AstSymbol>> chl = ast.getChildren(ast.parent(ast.parent(ast.parent(cur)))); // range > list > access > parent (assing/aclist)
                    String listName = chl.get(chl.size() - 1).getValue().getValue();
                    Variable v = context.get().get(listName);
                    if(v == null || v.getItems() == null)
                        throw new Exception("Variable '" + listName + "' is not defined as list!");

                    d2 = v.getItems().size() - 1; //[d1..size - 1]
                }
                else if(nodeVal.equals("rangeStart")) //[d1..]
                {
                    throw new Exception("the end range-index is not belongs to the access list expression!");
                }

                d1 = Math.floor(d1);
                d2 = Math.floor(d2);
                ArrayList<Elem<Object>> vals = new ArrayList<>();
                if(d1 > d2){
                    for(int i = (int)d1; i >= (int)d2; i--)
                        lists.top().add(new Elem<>(i));
                }
                else {
                    for(int i = (int)d1; i <= (int)d2; i++)
                        lists.top().add(new Elem<>(i));
                }
                break;
            }// end operator

            case "vector": { //node vector/expressions.
                if(vector_i == null)
                    break;
                List<Elem<Object>> vector_items = new ArrayList<>();
                int offsetTop = vector_len - 1;

                for(int i = 0; i < vector_len; i++){
                    vector_items.add(new Elem<>(exp.topFrom(offsetTop)));
                    offsetTop--;
                }
                for(int i = 0; i < vector_len; i++)
                    exp.pop();

                if(vector_items.size() == 1)
                    lists.top().add(vector_items.get(0));
                else if(vector_items.size() > 0)
                    lists.top().add(new Elem<>(vector_items)); //add as single list into outer list.
                //System.out.println("clist_vector: " + vector_items);
                //System.out.println(lists.top().toString()); //CHECK TRAVERSE THROUGH TREE PROCESSORS!
                break;
            }
        } //end switch of nodeType
    } //end method

    protected void checkAssign(PositionalTree<AstSymbol> ast, Node<AstSymbol> cur, Env context,
                             String nType,
                             String nVal,
                             LinkedStack<Object> exp,
                             LinkedStack<List<Elem<Object>>> lists,
                             ArrayList<List<Elem<Object>>> indices,
                             LinkedStack<FunctionInterpreter> functions,
                             LinkedStack<ArrayList<Object>> args,
                             LinkedList<Elem<?>> vector_i,
                             Map<String, Integer> vnames_idxs,
                             int vector_len) throws Exception {
        Variable v = null;
        //System.out.println("expr = " + exp.top() + " / " + nVal);
        //System.out.println("Parent: " + ast.parent(cur).getValue());

        if(ast.parent(cur).getValue().getType().equals("assign") && ast.rightSibling(cur) == null){ //variable parent is assign and it is lvalue (exp = var)
            if(lists.top() != null){ //list expression.
                v = new Variable(nVal); //ast.value (variable name)
                context.add(v);
                v.setItems(lists.top());
                v.setStrVal(v.getItems().toString()); //error of null item!!!!!
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
                System.out.println("access: " + nVal + " = " + v.getStrVal());
                return;
            } //end indices.
            else { //simple expression. (nor access nor list [a = exp])
                v = context.get(nVal);
                if(v == null) {
                    v = new Variable(nVal); //ast.value (variable name)
                    context.add(v);
                }

                if(!functions.isEmpty()){ //expression is function [a = function]
                    v.setFunction(functions.top());
                    functions.pop();
                }
                else { //expression is literal [a = expr]
                    v.setStrVal(TypeChecker.GetStrValue(exp.top())) ;
                    System.out.println(nVal + " = " + v.getStrVal());
                    exp.pop();
                }
                return;
            }
        } //end assing type.

        v = context.get(nVal); //get variable from context if present.

        if(v == null && vector_i == null) //if not in context AND no vector.
            throw new Exception("Cannot find variable with name \'" + nVal + "\'. Define variable before use it!");

        else if(v == null && vector_i != null && vnames_idxs != null && vector_len > 0){ //try get from vector.
            int vector_idx = vnames_idxs.getOrDefault(nVal, -1);
            if(vector_idx == -1)
                throw new Exception("Cannot find variable with name \'" + nVal + "\'. Define variable before use it!");

            v = new Variable(nVal); //new sequence variable.
            checkTypeAndGetValue(ast, cur, v, context, vector_i.get(vector_idx));
        }
        checkAccess(ast, cur, v, context, exp, lists, indices, functions, args, vector_i);
    }

    protected void checkAccess(PositionalTree<AstSymbol> ast, Node<AstSymbol> cur, Variable v,
                             Env context,  LinkedStack<Object> exp, LinkedStack<List<Elem<Object>>> lists,
                             ArrayList<List<Elem<Object>>> indices, LinkedStack<FunctionInterpreter> functions,
                             LinkedStack<ArrayList<Object>> args, LinkedList<Elem<?>> vector_i) throws Exception
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

    //Check what is expression part of (whole itself, part of expr, list item, arg item, as if condition, as while condition)
    protected void checkList(PositionalTree<AstSymbol> ast, Node<AstSymbol> parent, Node<AstSymbol> cur, Env context,
                             String nType, Object nVal,
                             LinkedStack<Object> exp,
                             LinkedStack<List<Elem<Object>>> lists,
                             LinkedStack<ArrayList<Object>> args,
                             VisitorsNextIteration<AstSymbol> nextItr,
                             LinkedList<Elem<?>> vector_i) throws Exception {

        if(parent.getValue().getType().equals("list"))
            lists.top().add(new Elem<>(nVal));

        else if(parent.getValue().getType().equals("args"))
            args.top().add(nVal);

        else if(parent.getValue().getType().equals("if") && ast.leftMostChild(parent).equals(cur) && nVal.equals("1")){
            //if true -> goto if.
            LinkedNode<AstSymbol> node_pass = new LinkedNode<>();
            node_pass.setValue(new AstNode("pass", "pass")); //add pass before else_node.
            node_pass.setIdx(ast.getCount() + 3);
            node_pass.setParent((LinkedNode<AstSymbol>) parent); //ERROR: SubType is fixed! Make it flexible!!!
            ast.getRealChildren(parent).add(2, node_pass); //add before else.
        }
        else if(parent.getValue().getType().equals("if") && ast.leftMostChild(parent).equals(cur) && nVal.equals("0")){
            //if false -> goto else.
            nextItr.setOpts(2); //0 => default, 1 => skip all siblings, 2 => skip one sibling.
            // => got else node (second right sibling of cond) :: ( ->condition, if, else)
        }
        else if(parent.getValue().getType().equals("loop") && ast.leftMostChild(parent).equals(cur)
                && (
                        (nVal.equals("1") && parent.getValue().getValue().equals("while"))
                    ||  (nVal.equals("0") && parent.getValue().getValue().equals("until"))
                )
        )
        {
            //while true
            this.loop = true;
            return;
        }
        else if(parent.getValue().getType().equals("loop") && ast.leftMostChild(parent).equals(cur)
                &&
                (
                        (nVal.equals("0") && parent.getValue().getValue().equals("while"))
                    ||  (nVal.equals("1") && parent.getValue().getValue().equals("until"))
                )
        )
        {
            //while false
            nextItr.setOpts(2);  //0 => default, 1 => skip all siblings, 2 => skip one sibling.
            this.loop = false;
        }
        else
            exp.push(nVal); //part of expr or expr itself -> add to expression stack.
    }

    protected List<Elem<Object>> scanAccess(Variable v, ArrayList<List<Elem<Object>>> indices, int offset){
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
                    List inner_list = ((List) prev_list.get(0).getV1());        //extract j_item of list.
                    Object j_item = inner_list.get( ((j < 0) ? inner_list.size() + j : j) );
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

            indices.get(i).clear(); //remove read ptrs at current index.
        }
        indices.subList(delStart, indices.size()).clear(); //remove all elements at [delStart...indices.size() - 1]
        return prev_list;
    }


    private void checkTypeAndGetValue(
            PositionalTree<AstSymbol> ast,
            Node<AstSymbol> cur,
            Variable v,
            Env context,
            Elem<?> value)
    {
        if(value.getV1() instanceof Elem<?>)
            value = (Elem<?>) value.getV1();

        if(value.getV1() instanceof String)
            v.setStrVal((String) value.getV1());
        else if(value.getV1() instanceof Integer)
            v.setStrVal(((Integer) value.getV1()).toString());
        else if(value.getV1() instanceof Double)
            v.setStrVal(((Double) value.getV1()).toString());
        else if(value.getV1() instanceof List){
            v.setItems((List<Elem<Object>>) value.getV1());
            v.setStrVal( ((List<Elem<Object>>) value.getV1()).toString());
        }
        else if(value.getV1() instanceof FunctionInterpreter){
            v.setFunction((FunctionInterpreter) value.getV1());
        }
    }


} //end class