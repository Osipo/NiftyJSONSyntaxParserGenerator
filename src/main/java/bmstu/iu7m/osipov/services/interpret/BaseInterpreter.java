package bmstu.iu7m.osipov.services.interpret;

import bmstu.iu7m.osipov.services.grammars.AstSymbol;
import bmstu.iu7m.osipov.structures.graphs.Elem;
import bmstu.iu7m.osipov.structures.lists.LinkedStack;
import bmstu.iu7m.osipov.structures.trees.Node;
import bmstu.iu7m.osipov.structures.trees.PositionalTree;
import bmstu.iu7m.osipov.structures.trees.VisitorMode;
import bmstu.iu7m.osipov.utils.ProcessNumber;

import java.util.concurrent.atomic.AtomicReference;

public class BaseInterpreter {

    public void interpret(PositionalTree<AstSymbol> ast) {
        AtomicReference<Env> env = new AtomicReference<>();
        env.set(new Env(null));
        LinkedStack<String> exp = new LinkedStack<>();

        // anonymous function.
        ast.visit(VisitorMode.PRE, (n) -> {
            if(n.getValue().getType().equals("program"))
                env.set(new Env(env.get()));
            else if(n.getValue().getType().equals("end"))
                env.set(env.get().getPrev());
            else if(n.getValue().getType().equals("assign")) {
                ast.visitFrom(VisitorMode.POST, (c) -> {
                    try {
                        applyOperation(ast, env.get(), c, exp);
                    } catch (Exception e) {
                        System.err.println(e);
                    }
                }, n);
            }
        });
    }

    private void applyOperation(PositionalTree<AstSymbol> ast, Env context, Node<AstSymbol> cur, LinkedStack<String> exp) throws Exception {
        if (context == null || cur == null || cur.getValue() == null)
            return;

        String opType = cur.getValue().getType();
        String nodeVal = cur.getValue().getValue();
        switch (opType){
            case "number": {
                exp.push(nodeVal);
                break;
            }
            case "variable":{
                Variable v = null;
                if(ast.parent(cur).getValue().getType().equals("assign")){ //variable parent is assign
                    v = new Variable(nodeVal); //ast.value (variable name)
                    context.add(v);
                    v.setStrVal(exp.top());
                    System.out.println(nodeVal + " = " + v.getStrVal());
                    exp.pop();
                    break;
                }
                v = context.get(nodeVal);
                if(v == null)
                    throw new Exception("Cannot find variable with name \'" + nodeVal + "\'. Define variable before use it!");
                exp.push(v.getStrVal());
                break;
            }
            case "operator": {
                String t1 = exp.top();
                exp.pop();
                String t2 = exp.top();
                exp.pop();

                double d2 = ProcessNumber.parseNumber(t1);
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

                exp.push(Double.toString(d1));
                break;
            } // end operator
        } //end switch of nodeType
    } //end method

} //end class