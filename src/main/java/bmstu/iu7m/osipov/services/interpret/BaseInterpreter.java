package bmstu.iu7m.osipov.services.interpret;

import bmstu.iu7m.osipov.services.grammars.AstSymbol;
import bmstu.iu7m.osipov.structures.graphs.Elem;
import bmstu.iu7m.osipov.structures.lists.LinkedStack;
import bmstu.iu7m.osipov.structures.trees.Node;
import bmstu.iu7m.osipov.structures.trees.PositionalTree;
import bmstu.iu7m.osipov.structures.trees.VisitorMode;
import bmstu.iu7m.osipov.utils.ProcessNumber;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class BaseInterpreter {

    public void interpret(PositionalTree<AstSymbol> ast) {
        AtomicReference<Env> env = new AtomicReference<>();
        env.set(new Env(null));
        LinkedStack<String> exp = new LinkedStack<>();
        LinkedStack<List<Elem<Object>>> lists = new LinkedStack<>();
        ArrayList<List<Elem<Object>>> indices = new ArrayList<>();

        // anonymous function.
        ast.visit(VisitorMode.PRE, (n) -> {
            if(n.getValue().getType().equals("program"))
                env.set(new Env(env.get()));
            else if(n.getValue().getType().equals("end"))
                env.set(env.get().getPrev());
            else if(n.getValue().getType().equals("assign")) {
                ast.visitFrom(VisitorMode.POST, (c) -> {
                    try {
                        applyOperation(ast, env.get(), c, exp, lists, indices);
                    } catch (Exception e) {
                        System.err.println(e);
                    }
                }, n);
            }
        });
    }

    private void applyOperation(PositionalTree<AstSymbol> ast, Env context, Node<AstSymbol> cur, LinkedStack<String> exp, LinkedStack<List<Elem<Object>>> lists, ArrayList<List<Elem<Object>>> indices) throws Exception {
        if (context == null || cur == null || cur.getValue() == null)
            return;

        String opType = cur.getValue().getType();
        String nodeVal = cur.getValue().getValue();
        switch (opType){
            case "number": case "char": case "string": {
                checkList(ast.parent(cur), context, opType, nodeVal, exp, lists);
                break;
            }
            case "start":{
                ArrayList<Elem<Object>> items = new ArrayList<>();
                lists.push(items);
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
                checkAssign(ast, cur, context, opType, nodeVal, exp, lists, indices);
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
                checkList(ast.parent(cur), context, opType, val, exp, lists);
                break;
            } // end operator
        } //end switch of nodeType
    } //end method

    private void checkAssign(PositionalTree<AstSymbol> ast, Node<AstSymbol> cur, Env context, String nType, String nVal, LinkedStack<String> exp, LinkedStack<List<Elem<Object>>> lists, ArrayList<List<Elem<Object>>> indices) throws Exception {
        Variable v = null;
        //System.out.println("expr = " + exp.top() + " / " + nVal);
        //System.out.println("Parent: " + ast.parent(cur).getValue());
        if(ast.parent(cur).getValue().getType().equals("assign")){ //variable parent is assign
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

                for (int i = 0; i < indices.size(); i++) {

                    //extract each ptr (number in brackets [])
                    for(Elem<Object> ptr : indices.get(i)){
                        Integer j = null;
                        if(ptr.getV1() instanceof String)
                            j = Integer.parseInt((String) ptr.getV1());
                        else if(ptr.getV1() instanceof Integer)
                            j = (Integer) ptr.getV1();


                        if(i > 0 && prev_list.size() == 1 && prev_list.get(0).getV1() instanceof List){
                            Object j_item = ((List) prev_list.get(0).getV1()).get(j); //extract j_item of list.
                            if(j_item instanceof Elem)
                                content.add((Elem) j_item);
                        }
                        else
                            content.add(prev_list.get(j));

                        if(!(content.get(content.size() - 1).getV1() instanceof List)) {
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
                exp.pop(); //remove expression to be assigned for each list item.

                v.setStrVal(v.getItems().toString());
                System.out.println("access: " + nVal + " = " + v.getStrVal());
                return;
            } //end indices.
            else { //simple expression. (nor access nor list [a = exp])
                v = new Variable(nVal); //ast.value (variable name)
                context.add(v);
                v.setStrVal(exp.top());
                System.out.println(nVal + " = " + v.getStrVal());
                exp.pop();
                return;
            }
        } //end assing type.
        v = context.get(nVal);
        if(v == null)
            throw new Exception("Cannot find variable with name \'" + nVal + "\'. Define variable before use it!");
        checkAccess(ast, cur, v, context, exp, lists, indices);
    }

    private void checkAccess(PositionalTree<AstSymbol> ast, Node<AstSymbol> cur, Variable v, Env context,  LinkedStack<String> exp, LinkedStack<List<Elem<Object>>> lists, ArrayList<List<Elem<Object>>> indices) throws Exception {
        Node<AstSymbol> parent = ast.parent(cur);
        if(parent.getValue().getType().equals("access")){ //variable > access

            int offset = ast.getChildren(ast.leftMostChild(parent)).size(); //access/list > access/indices > count(children)
            List<Elem<Object>> content = scanAccess(v, indices, offset);
            /*
            for(Elem<Object> ob_i : indices.get(0)){ //get current index.
                Integer i = null;
                if(ob_i.getV1() instanceof String)
                    i = Integer.parseInt((String) ob_i.getV1());
                else if(ob_i.getV1() instanceof Integer)
                    i = (Integer) ob_i.getV1();
                content.add(v.getItems().get(i));
            }

            indices.remove(0); //delete current index.
            */


            if(ast.parent(parent).getValue().getType().equals("list")){ //variable > access > list
                lists.top().addAll(content);
            }
            else if(content.size() == 1)
                exp.push(content.get(0).getV1().toString()); //get first elem
            else if(content.size() > 1) //list expression.
                lists.push(content);
            return;
        }


        exp.push(v.getStrVal());
    }

    private void checkList(Node<AstSymbol> parent, Env context, String nType, String nVal, LinkedStack<String> exp, LinkedStack<List<Elem<Object>>> lists) throws Exception {
        if(parent.getValue().getType().equals("list"))
            lists.top().add(new Elem<>(nVal));
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
                    j = Integer.parseInt((String) ptr.getV1());
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