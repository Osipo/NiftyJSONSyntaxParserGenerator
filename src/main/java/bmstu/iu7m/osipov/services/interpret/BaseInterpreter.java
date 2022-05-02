package bmstu.iu7m.osipov.services.interpret;

import bmstu.iu7m.osipov.services.grammars.AstSymbol;
import bmstu.iu7m.osipov.structures.graphs.Elem;
import bmstu.iu7m.osipov.structures.hashtables.STable;
import bmstu.iu7m.osipov.structures.lists.LinkedStack;
import bmstu.iu7m.osipov.structures.trees.Node;
import bmstu.iu7m.osipov.structures.trees.PositionalTree;
import bmstu.iu7m.osipov.structures.trees.VisitorMode;
import com.kitfox.svg.A;

import java.util.concurrent.atomic.AtomicReference;

public class BaseInterpreter {

    public void interpret(PositionalTree<AstSymbol> ast){
        AtomicReference<Env> env = new AtomicReference<>();
        env.set(new Env(null));
        LinkedStack<Variable> exp = new LinkedStack<>();
        Elem<Long> ids = new Elem<>(0l);

        // anonymous function.
        ast.visit(VisitorMode.PRE, (n) -> {
            if(n.getValue().getType().equals("program"))
                env.set(new Env(env.get()));
            else if(n.getValue().getType().equals("end"))
                env.set(env.get().getPrev());
            else if(n.getValue().getType().equals("assign"))
                ast.visitFrom(VisitorMode.POST, (c) -> {
                    applyOperation(ast, env.get(), c, exp);
                }, n);
        });
    }

    private void applyOperation(PositionalTree<AstSymbol> ast, Env context, Node<AstSymbol> cur, LinkedStack<Variable> exp)
    {
        if (context == null || cur == null || cur.getValue() == null)
            return;

        String opType = cur.getValue().getType();
        String nodeVal = cur.getValue().getValue();
        switch (opType){
            case "number": {
                Variable temp = new Variable("temp");
                temp.setStrVal(nodeVal); //node.value.value -> ast.value
                exp.push(temp);
                break;
            }
            case "variable":{
                Variable v = null;
                if(ast.parent(cur).getValue().getType().equals("assign")){ //variable parent is assign
                    v = new Variable(nodeVal); //ast.value (variable name)
                    context.add(v);
                    break;
                }
                v = context.get(nodeVal);
                exp.push(v);
            }
            case "operator": {
                String t1 = exp.top().getStrVal();
                exp.pop();
                String t2 = exp.top().getStrVal();
                exp.pop();
            }
        }
    }
}