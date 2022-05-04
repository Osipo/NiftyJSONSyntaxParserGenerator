package bmstu.iu7m.osipov.services.interpret;

import bmstu.iu7m.osipov.services.grammars.AstSymbol;
import bmstu.iu7m.osipov.structures.lists.LinkedStack;
import bmstu.iu7m.osipov.structures.trees.Node;
import bmstu.iu7m.osipov.structures.trees.PositionalTree;
import bmstu.iu7m.osipov.structures.trees.VisitorMode;
import bmstu.iu7m.osipov.utils.ProcessNumber;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class BaseInterpreter {

    public void interpret(PositionalTree<AstSymbol> ast) {
        AtomicReference<Env> env = new AtomicReference<>();
        env.set(new Env(null));
        LinkedStack<String> exp = new LinkedStack<>();
        LinkedStack<List<Object>> lists = new LinkedStack<>();
        ArrayList<List<Object>> indices = new ArrayList<>();

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

    private void applyOperation(PositionalTree<AstSymbol> ast, Env context, Node<AstSymbol> cur, LinkedStack<String> exp, LinkedStack<List<Object>> lists, ArrayList<List<Object>> indices) throws Exception {
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
                ArrayList<Object> items = new ArrayList<>();
                lists.push(items);
                break;
            }
            case "list": {
                if(ast.parent(cur).getValue().getType().equals("list")){
                    //inner list
                    List<Object> inner = lists.top();
                    lists.pop(); //remove inner list from stack.
                    lists.top().add(inner); //add list as single item instead of addAll of its items.
                }
                else if(ast.parent(cur).getValue().getType().equals("access")){
                    ArrayList<Object> idx_items = new ArrayList<>(lists.top()); //move list from lists to indices
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

                String val = Double.toString(d1);
                checkList(ast.parent(cur), context, opType, val, exp, lists);
                break;
            } // end operator
        } //end switch of nodeType
    } //end method

    private void checkAssign(PositionalTree<AstSymbol> ast, Node<AstSymbol> cur, Env context, String nType, String nVal, LinkedStack<String> exp, LinkedStack<List<Object>> lists, ArrayList<List<Object>> indices) throws Exception {
        Variable v = null;
        if(ast.parent(cur).getValue().getType().equals("assign")){ //variable parent is assign
            v = new Variable(nVal); //ast.value (variable name)
            context.add(v);
            if(lists.top() != null){
                v.setItems(lists.top());
                v.setStrVal(v.getItems().toString());
                System.out.println(nVal + " = " + v.getStrVal());
                lists.pop();
                return;
            } //end list expression.
            else if(indices.size() != 0){
                List<Object> idxs = null;
                ArrayList<Object> id_lists = null;
                //l = [12, [34, 35]]; => l[0, 1][0, 1] => [12, [34, 35]] => [12, [34, 35].
                //l = [1, [2, [3]]] => l[1][0] => [3].

                //[1, [2, 3, [4]], 5]  l[0, 1][1, 2][0] = 99 => [99, [2, 99, [99]], 5]


                List<List<Object>> ls = new ArrayList<>();

                for(int i = 0; i < indices.size(); i++) {
                    idxs = indices.get(i);
                    for (int p = 0; p < idxs.size(); p++) {
                        Object idx_i = idxs.get(p);
                        Integer j = null;
                        if (idx_i instanceof String)
                            j = Integer.parseInt((String) idx_i);
                        else if (idx_i instanceof Integer)
                            j = (Integer) idx_i;

                        if(i == 0){
                            Object item = v.getItems().get(j);
                            if(item instanceof List)
                                ls.add((List) item);
                            else
                                v.getItems().set(j, exp.top());
                        }
                        else{
                            List<Object> ls_i = ls.get(p);
                            Object item = ls.get(j);
                            if(item instanceof List)
                                ls.set(p, (List) item);
                            else
                                ls_i.set(j, exp.top());
                        }
                    } //end current index set.
                    idxs.clear();
                } //end indices sets
                indices.clear();
                exp.pop();
                return;
            } //end indices.
            else {
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

    private void checkAccess(PositionalTree<AstSymbol> ast, Node<AstSymbol> cur, Variable v, Env context,  LinkedStack<String> exp, LinkedStack<List<Object>> lists, ArrayList<List<Object>> indices) throws Exception {
        Node<AstSymbol> parent = ast.parent(cur);
        if(parent.getValue().getType().equals("access")){ //variable > access.
            ArrayList<Object> content = new ArrayList<>();
            for(Object ob_i : indices.get(indices.size() - 1)){ //get current index.
                Integer i = null;
                if(ob_i instanceof String)
                    i = Integer.parseInt((String) ob_i);
                else if(ob_i instanceof Integer)
                    i = (Integer) ob_i;
                content.add(v.getItems().get(i));
            }

            indices.remove(indices.size() - 1); //delete current index.

            if(ast.parent(parent).getValue().getType().equals("list")){ //variable > access > list
                lists.top().addAll(content);
            }
            else
                exp.push(content.get(0).toString()); //get first elem
            return;
        }
        exp.push(v.getStrVal());
    }

    private void checkList(Node<AstSymbol> parent, Env context, String nType, String nVal, LinkedStack<String> exp, LinkedStack<List<Object>> lists) throws Exception {
        if(parent.getValue().getType().equals("list"))
            lists.top().add(nVal);
        else
            exp.push(nVal);
    }

} //end class