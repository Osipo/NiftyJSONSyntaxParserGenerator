package bmstu.iu7m.osipov.services.interpret;

import bmstu.iu7m.osipov.services.grammars.AstSymbol;
import bmstu.iu7m.osipov.structures.graphs.Elem;
import bmstu.iu7m.osipov.structures.lists.LinkedStack;
import bmstu.iu7m.osipov.structures.trees.Node;
import bmstu.iu7m.osipov.structures.trees.PositionalTree;
import bmstu.iu7m.osipov.structures.trees.VisitorMode;
import bmstu.iu7m.osipov.structures.trees.VisitorsNextIteration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class BottomUpInterpreter extends BaseInterpreter {

    @Override
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
        nextItr.setOpts(0);

        //Totally POST_ORDER. (start nodes -> leaf nodes that indicate start of the new scope).
        ast.visit(VisitorMode.POST, (n, next) -> {

            //start new prog (scope)
            if(n.getValue().getType().equals("start") && n.getValue().getValue().equals("prog"))
                env.set(new Env(env.get()));

            //end of program (remove current scope and get previous)
            else if(n.getValue().getType().equals("program"))
                env.set(env.get().getPrev());

            else
                try{
                    applyOperation(ast, env, n, exp, lists, indices, params, functions, args, nextItr);
                }
                catch (Exception e){
                    System.err.println(e);
                }
        }, nextItr);
    }

    @Override
    protected void execFunction(FunctionInterpreter f, PositionalTree<AstSymbol> ast, LinkedStack<String> exp, LinkedStack<FunctionInterpreter> functions, VisitorsNextIteration<AstSymbol> nextItr) {
        AtomicReference<Env> env2 = new AtomicReference<>();
        env2.set(f.getContext());
        Node<AstSymbol> root = f.getRoot();

        LinkedStack<List<Elem<Object>>> lists = new LinkedStack<>();
        ArrayList<List<Elem<Object>>> indices = new ArrayList<>();

        ArrayList<Variable> params = new ArrayList<>();
        LinkedStack<ArrayList<Object>> args = new LinkedStack<ArrayList<Object>>();

        ast.visitFrom(VisitorMode.POST, (c, next) -> {

            //start new prog (scope)
            if(c.getValue().getType().equals("start") && c.getValue().getValue().equals("prog"))
                env2.set(new Env(env2.get()));

                //end of program (remove current scope and get previous)
            else if(c.getValue().getType().equals("program"))
                env2.set(env2.get().getPrev());

            else
                try{
                    applyOperation(ast, env2, c, exp, lists, indices, params, functions, args, nextItr);
                }
                catch (Exception e){
                    System.err.println(e);
                }
        }, root, nextItr);
    }
}