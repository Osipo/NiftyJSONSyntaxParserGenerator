package bmstu.iu7m.osipov.services.interpret;

import bmstu.iu7m.osipov.services.grammars.AstNode;
import bmstu.iu7m.osipov.services.grammars.AstSymbol;
import bmstu.iu7m.osipov.services.interpret.external.ExternalFunctionInterpreter;
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

    protected LinkedStack<ArrayList<Variable>> accumulators; //for reduces.
    protected LinkedStack<Boolean> isAccumulatorItems;

    public abstract void interpret(PositionalTree<AstSymbol> ast);

    protected abstract void execFunction(FunctionInterpreter f,
                                         PositionalTree<AstSymbol> ast,
                                         LinkedStack<Object> exp,
                                         LinkedStack<FunctionInterpreter> functions,
                                         VisitorsNextIteration<AstSymbol> nextItr,
                                         LinkedList<Elem<?>> vector_i,
                                         Map<String, Integer> vnames_idxs,
                                         int vector_len,
                                         LinkedStack<SequencesInterpreter> matrices,
                                         boolean isReduce
                                         );

    protected void applyOperation(PositionalTree<AstSymbol> ast, AtomicReference<Env> context, Node<AstSymbol> cur,
                                  LinkedStack<Object> exp, LinkedStack<List<Elem<Object>>> lists,
                                  ArrayList<List<Elem<Object>>> indices, ArrayList<Variable> params,
                                  LinkedStack<FunctionInterpreter> functions,
                                  LinkedStack<ArrayList<Object>> args,
                                  VisitorsNextIteration<AstSymbol> nextIteration,
                                  LinkedList<Elem<?>> vector_i,
                                  Map<String, Integer> vnames_idxs,
                                  int vector_len,
                                  LinkedStack<SequencesInterpreter> matrices,
                                  boolean isReduce
                                  ) throws Exception {
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
                Object num_val = ProcessNumber.parseOnlyNumber(nodeVal); //push raw numeric value (not string)!
                if(num_val != null && ((Double) num_val) % 1 == 0)
                    num_val = ((Double) num_val).intValue();
                checkList(ast, ast.parent(cur), cur, context.get(), opType, num_val, exp, lists, args, nextIteration, vector_i);
                break;
            }
            case "pass": {
                nextIteration.setOpts(3); //3 => skip siblings add detach node.
                break;
            }
            case "loop": { //loop while
                if(cur.getValue().getCond()){ //passed one iteration -> check condition again.
                    Node<AstSymbol> cond_node = ast.leftMostChild(cur);
                    nextIteration.setNextNode(cond_node);
                }
                break;
            }
            case "goto": {
                String finalNodeVal = nodeVal;
                //System.out.println("goto " + nodeVal);
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
                //System.out.println("found " + lentry);

                if(lentry == null)
                    throw new Exception("Cannot find label '" + nodeVal + "' The label must be declared within current context and only once.");

                nextIteration.setNextNode(lentry.getV2());
                nextIteration.setOpts(15); // 10 -> 15 (do not return back treat next node as leaf.)
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
                else if(nodeVal.equals("vector")
                )
                {
                    nextIteration.setOpts(4); //skip all siblings. and do not perform action
                    matrices.push(new SequencesInterpreter(
                            ast, ast.parent(cur), ast.rightSibling(ast.parent(cur)), context.get()
                            ,exp, lists, indices, functions, args, matrices, this, vector_i, vnames_idxs
                    ));
                    this.curSequence = matrices.top();
                }

                // start/accumulators node of reduce operator.
                else if(nodeVal.equals("accumulators")){
                    this.accumulators.push(new ArrayList<>());
                    this.isAccumulatorItems.push(true);
                }
                break;
            }
            case "initacc": { //finish init of accumulators
                this.isAccumulatorItems.pop();
                this.isAccumulatorItems.push(false);
                break;
            }
            case "matrix": case "reduce": {
                nextIteration.setOpts(0);
                this.curSequence.generateItems(lists.top(), this.accumulators, this.isAccumulatorItems); //parent of matrix is always list/items.
                matrices.pop();
                this.curSequence = matrices.top();
                break;
            }
            case "list": {
                if(ast.parent(cur).getValue().getType().equals("list")){ //inner list
                    //inner list
                    List<Elem<Object>> inner = lists.top();
                    lists.pop(); //remove inner list from stack.
                    lists.top().add(new Elem<>(inner)); //add list as single item instead of addAll of its items.
                }
                else if(ast.parent(cur).getValue().getType().equals("access")){ //index.
                    ArrayList<Elem<Object>> idx_items = new ArrayList<>(lists.top()); //move list from lists to indices
                    lists.pop();
                    indices.add(idx_items); //lists > indices.
                }
                else if(ast.parent(cur).getValue().getType().equals("args")){ //list as argument
                    List<Elem<Object>> list_arg = lists.top();
                    lists.pop();
                    args.top().add(list_arg); //add as single argument with type list.
                }
                else if(ast.parent(cur).getValue().getType().equals("initacc")){ //as start value of acc
                    checkReduce(lists.top(), exp, lists, -1); //if inner then lists will be added to accumulators.
                }

                //if list is a PART of expression (not a part of sequence generator!)
                else if(   ast.parent(cur).getValue().getType().equals("operator") //operator expression.
                        || ast.parent(cur).getValue().getType().equals("boolop")
                        || ast.parent(cur).getValue().getType().equals("relop")
                        || ast.parent(cur).getValue().getType().equals("ternaryop")
                        || ast.parent(cur).getValue().getType().equals("vector")
                        || (ast.parent(cur).getValue().getType().equals("assign") && ast.parent(cur).getValue().getValue().length() > 1) //combine assign
                        ||  ast.parent(cur).getValue().getType().equals("lambda")
                        ||  ast.parent(ast.parent(cur)).getValue().getType().equals("lambda") //last function expression
                        ||  ast.parent(cur).getValue().getType().equals("else")
                )
                {
                    exp.push(lists.top()); //add operand to expression stack.
                    lists.pop(); //remove it from list stack as it will be added to expression stack.
                }
                break;
            }
            case "variable": {
                if(ast.parent(cur).getValue().getType().equals("params")){ //add parameter to params list.
                    params.add(new Variable(nodeVal, 1)); //category = 1 means parameter.
                    return;
                } //end  lambda variable_parameters.
                else if(ast.parent(cur).getValue().getType().equals("typeof")){ //instanceof operator.

                    String typeName = ast.rightSibling(cur).getValue().getValue();
                    Variable v = context.get().get(nodeVal);
                    if(v == null)
                        throw new Exception("Variable '" + nodeVal + "' is not defined!");



                    if(v.isList() && typeName.equals("list"))
                        exp.push(1);
                    else if(v.getFunction() != null && typeName.equals("function"))
                        exp.push(1);
                    else if(v.getSubModule() != null && typeName.equals("dict"))
                        exp.push(1);
                    else if(v.getStrVal().charAt(0) == '"' && (typeName.equals("string") || typeName.equals("str")))
                        exp.push(1);
                    else if(ProcessNumber.parseOnlyNumber(v.getStrVal()) != null && typeName.equals("number"))
                        exp.push(1);
                    else
                        exp.push(0);

                    nextIteration.setOpts(2); //ignore first sibling (get second that is null).
                    return;
                }
                checkAssign(ast, cur, context.get(), opType, nodeVal, exp, lists, indices, functions, args, vector_i, vnames_idxs, vector_len, isReduce);
                break;
            }

            //After parsing typeof expression check parent node.
            case "typeof": {
                checkList(ast, ast.parent(cur), cur, context.get(), opType, exp.top(), exp, lists, args, nextIteration, vector_i);
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


                    Variable fvar = context.get().get(ast.parent(cur).getValue().getValue(), v -> v.getFunction() != null);
                    if(fvar == null)
                        fvar = context.get().get(ast.parent(cur).getValue().getValue(), v -> v.getCategory() == 3);
                    if(fvar == null)
                        throw new Exception("Function '" + ast.parent(cur).getValue().getValue() + "' is not defined!");

                    FunctionInterpreter f = fvar.getFunction();
                    f.bindArguments(args_i); //throw Exception if cannot bind.
                    args_i.clear();// flush processed args.
                    args.pop();


                    if(f instanceof ExternalFunctionInterpreter) {
                       ((ExternalFunctionInterpreter) f).callExternal(exp);
                    }
                    else {
                        execFunction(f, ast, exp, functions, nextIteration, vector_i, vnames_idxs, vector_len, matrices, isReduce);
                        f.setContext(f.getContext().getPrev()); //remove inner context
                    }

                    //get next arguments caller. (args + args) => exec next function
                    if(ast.rightSibling(cur) != null && ast.rightSibling(cur).getValue().getType().equals("args")){
                        //System.out.println("Function '" + f.getFunctionName() + "' returns new lambda function");

                        Variable f_2 = new Variable("0$_" + f.getFunctionName());
                        context.get().add(f_2); //add generated variable (name is illegal for input) of N + 1 function
                        f_2.setFunction(functions.top());
                        functions.pop();

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
                    //i.e. f(f(x))
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

            //operators
            case "unaryop":
            case "relop":
            case "boolop":
            case "operator":
            case "ternaryop": {
                Object val = TypeChecker.CheckExpressionType(exp, opType, nodeVal);
                if(ast.parent(cur) == null) //TODO: check USAGE
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

                if(Double.isNaN(d1) || Double.isNaN(d2)){
                    throw new NumberFormatException("Range boundaries must be Numbers but returned NaN.");
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
                    throw new Exception("Cannot define the end of the range! This is not the access expression where items are defined!");
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
                int offsetTop = vector_len - 1; //extract from stack. len = 2 => [1, 0]; len = 1 => [0].

                for(int i = 0; i < vector_len; i++){
                    vector_items.add(new Elem<>(exp.topFrom(offsetTop)));
                    offsetTop--;
                }
                for(int i = 0; i < vector_len; i++) //items moved from exp into vector_items.
                    exp.pop();

                //computed expressions are in vector_items.
                //If operator is reduce => update accumulators and add them if vector is last item.
                if(isReduce){
                    //update accumulators.
                    for(int i = 0; i < vector_items.size(); i++){
                        //variable and value are meaningful
                        checkTypeAndGetValue(ast, cur, accumulators.top().get(i), context.get(), vector_items.get(i));
                    }
                    break;
                }

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
                             int vector_len, boolean isReduce) throws Exception {
        Variable v = null;
        //System.out.println("expr = " + exp.top() + " / " + nVal);
        //System.out.println("Parent: " + ast.parent(cur).getValue());

        if(ast.parent(cur).getValue().getType().equals("assign") && ast.rightSibling(cur) == null){ //variable parent is assign and it is lvalue (var = exp)
            String aop = ast.parent(cur).getValue().getValue(); //assign op: [=, +=, -= ...]
            TypeChecker.ResolveAssignOperation(ast, cur, context, exp, lists, indices, functions, aop, nVal);
            return;
        } //end assing type.

        v = context.get(nVal); //get variable from context if present.

        if(v == null && vector_i == null) //if not in context AND no vector.
            throw new Exception("Cannot find variable with name \'" + nVal + "\'. Define variable before use it!");

        //if vector => then get it from vector.
        else if(vector_i != null && vnames_idxs != null && vector_len > 0){ //try get from vector.
            //System.out.println(vnames_idxs);
            int vector_idx = vnames_idxs.getOrDefault(nVal, -1);
            if(vector_idx == -1 && v == null && !isReduce)
                throw new Exception("Cannot find variable with name \'" + nVal + "\' at vector. Define variable before use it!");

            else if(nVal.equals("acc") && isReduce) { //context name acc: ignore sequence item or outer variable name
                //1. compute position of expression.
                int acc_i = PositionalTreeUtils.indexOfChildAt(ast, cur, (x) -> x.getType().equals("vector"));
                v = accumulators.top().get(acc_i); //extract accumulator variable and add it to expr.
            }

            //if variable is not exists but found at vector => extract it from vector.
            //else if it exists and matches the vector sequence_item => use from vector.
            if(v == null || vector_idx != -1) {
                v = new Variable(nVal); //new sequence variable.
                checkTypeAndGetValue(ast, cur, v, context, vector_i.get(vector_idx));
            }
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
            List<Elem<Object>> content = scanAccess(v, indices, offset); //v, [a], 1.


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
                functions.push((FunctionInterpreter) content.get(0).getV1()); //TODO: check init reduce accumulators
            }
            else if(content.size() == 1) { //primitive element
                checkReduce(TypeChecker.CheckValue(content.get(0).getV1(), null), exp, lists, 0); //exp.push
            }
            else if(content.size() > 1) { //list expression. (list element)
                checkReduce(content, exp, lists, -1); //lists.push
            }
        }
        else if (parent.getValue().getType().equals("list")) // variable > list.
            lists.top().add(new Elem<>(TypeChecker.CheckValue(v, null)));
        else if(parent.getValue().getType().equals("args")) // variable > args.
            args.top().add(TypeChecker.CheckValue(v, null));
        else
            checkReduce(TypeChecker.CheckValue(v, null), exp, lists, 0); //exp.push
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

        else if(parent.getValue().getType().equals("if") && ast.leftMostChild(parent).equals(cur) && ExpressionsUtils.IsTrue(nVal)){
            //if true -> goto if.
            LinkedNode<AstSymbol> node_pass = new LinkedNode<>();
            node_pass.setValue(new AstNode("pass", "pass")); //add pass before else_node.
            node_pass.setIdx(ast.getCount() + 3);
            node_pass.setParent((LinkedNode<AstSymbol>) parent); //ERROR: SubType is fixed! Make it flexible!!!
            ast.getRealChildren(parent).add(2, node_pass); //add before else.
        }
        else if(parent.getValue().getType().equals("if") && ast.leftMostChild(parent).equals(cur) && !ExpressionsUtils.IsTrue(nVal)){
            //if false -> goto else.
            nextItr.setOpts(2); //0 => default, 1 => skip all siblings, 2 => skip one sibling.
            // => got else node (second right sibling of cond) :: ( ->condition, if, else)
        }
        else if(parent.getValue().getType().equals("loop") && ast.leftMostChild(parent).equals(cur)
                && (
                        (ExpressionsUtils.IsTrue(nVal) && parent.getValue().getValue().equals("while"))
                    ||  (!ExpressionsUtils.IsTrue(nVal) && parent.getValue().getValue().equals("until"))
                )
        )
        {
            //while true
            //this.loop = true
            parent.getValue().setCond(true); //loop is true
            return;
        }
        else if(parent.getValue().getType().equals("loop") && ast.leftMostChild(parent).equals(cur)
                &&
                (
                        (!ExpressionsUtils.IsTrue(nVal) && parent.getValue().getValue().equals("while"))
                    ||  (ExpressionsUtils.IsTrue(nVal) && parent.getValue().getValue().equals("until"))
                )
        )
        {
            //while false
            nextItr.setOpts(2);  //0 => default, 1 => skip all siblings, 2 => skip one sibling.

            //this.loop = false;
            parent.getValue().setCond(false); //loop is false
        }
        else
            checkReduce(nVal, exp, lists, 0); //part of expr or expr itself -> add to expression stack.
    }

    //check if value is in init accumulators.
    protected void checkReduce(Object value, LinkedStack<Object> exp, LinkedStack<List<Elem<Object>>> lists, int addToExp){
        while(value instanceof Elem<?>)
            value = ((Elem<?>) value).getV1();

        if(!isAccumulatorItems.isEmpty() && isAccumulatorItems.top()){
            Variable v = new Variable("$_accs");
            if(value instanceof String)
                v.setStrVal((String) value);
            else if(value instanceof Integer)
                v.setStrVal(((Integer) value).toString());
            else if(value instanceof Double)
                v.setStrVal(((Double) value).toString());
            else if(value instanceof List){
                v.setItems((List<Elem<Object>>) value);
                v.setStrVal(v.getItems().toString());
            }
            else if(value instanceof FunctionInterpreter){
                v.setFunction((FunctionInterpreter) value);
            }
            accumulators.top().add(v);
        }
        else if(value instanceof List && addToExp == -1)
            lists.push((List<Elem<Object>>) value);
        else if(addToExp == 0)
            exp.push(value);
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
        indices.subList(delStart, indices.size()).clear(); //remove indices from indices list.
        return prev_list;
    }


    private void checkTypeAndGetValue(
            PositionalTree<AstSymbol> ast,
            Node<AstSymbol> cur,
            Variable v,
            Env context,
            Elem<?> value)
    {
        while(value.getV1() instanceof Elem<?>)
            value = (Elem<?>) value.getV1();

        //if(value.getV1() instanceof Elem<?>)
        //    value = (Elem<?>) value.getV1();

        if(value.getV1() instanceof String)
            v.setStrVal((String) value.getV1());
        else if(value.getV1() instanceof Integer)
            v.setStrVal(((Integer) value.getV1()).toString());
        else if(value.getV1() instanceof Double)
            v.setStrVal(((Double) value.getV1()).toString());
        else if(value.getV1() instanceof List){
            v.setItems((List<Elem<Object>>) value.getV1());
            v.setStrVal(v.getItems().toString());
        }
        else if(value.getV1() instanceof FunctionInterpreter){
            v.setFunction((FunctionInterpreter) value.getV1());
        }
    }


} //end class