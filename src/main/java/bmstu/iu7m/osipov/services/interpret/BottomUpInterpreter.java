package bmstu.iu7m.osipov.services.interpret;

import bmstu.iu7m.osipov.services.grammars.AstSymbol;
import bmstu.iu7m.osipov.structures.graphs.Elem;
import bmstu.iu7m.osipov.structures.graphs.Pair;
import bmstu.iu7m.osipov.structures.lists.LinkedList;
import bmstu.iu7m.osipov.structures.lists.LinkedStack;
import bmstu.iu7m.osipov.structures.lists.Triple;
import bmstu.iu7m.osipov.structures.trees.Node;
import bmstu.iu7m.osipov.structures.trees.PositionalTree;
import bmstu.iu7m.osipov.structures.trees.VisitorMode;
import bmstu.iu7m.osipov.structures.trees.VisitorsNextIteration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class BottomUpInterpreter extends BaseInterpreter {

    public BottomUpInterpreter(){
        this.labels = new ArrayList<>();
        this.blocks = 0;
    }

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

        /* Collect all labels with context.*/
        ast.visit(VisitorMode.POST, (n, next) ->{
            //skip start/prog at while/until/if/else parent nodes.
            if(n.getValue().getType().equals("start") && n.getValue().getValue().equals("prog")
                    && ast.parent(n) != null && ast.parent(ast.parent(n)) != null
                    && ast.parent(ast.parent(n)).getValue().getType().equals("loop")
            )
                return;

            if (n.getValue().getType().equals("start") && n.getValue().getValue().equals("prog")
                    && ast.parent(n) != null && ast.parent(ast.parent(n)) != null
                    && ast.parent(ast.parent(n)).getValue().getType().equals("if")
            )
                return;

            if (n.getValue().getType().equals("start") && n.getValue().getValue().equals("prog")
                    && ast.parent(n) != null && ast.parent(ast.parent(n)) != null
                    && ast.parent(ast.parent(n)).getValue().getType().equals("else")
            )
                return;


            //start new prog (scope)
            if(n.getValue().getType().equals("start") && n.getValue().getValue().equals("prog")) {
                this.blocks++;
            }

            else if(n.getValue().getType().equals("program") && ast.parent(n) != null && ast.parent(n).getValue().getType().equals("loop"))
                return;

            else if(n.getValue().getType().equals("program") && ast.parent(n) != null && ast.parent(n).getValue().getType().equals("if"))
                return;
            else if(n.getValue().getType().equals("program") && ast.parent(n) != null && ast.parent(n).getValue().getType().equals("else"))
                return;
                //end of program (remove current scope and get previous)
            else if(n.getValue().getType().equals("program")) {
                this.blocks--;
            }

            //add new label node.
            //Replace Pair with Triple<Parent, LabelNode, block>
            else if(n.getValue().getType().equals("label")) {
                Triple<Node<AstSymbol>, Node<AstSymbol>, Integer> label_entry = new Triple<>(ast.parent(n), n, this.blocks);
                if(labels.contains(label_entry)){
                    nextItr.setOpts(-1);
                }
                this.labels.add(label_entry);
            }
        }, nextItr);

        //System.out.println("found labels: " + labels);

        /* If dublicate labels then error */
        if(nextItr.getOpts() == -1){
            System.err.println("Dublicate labels at the same context!");
            return;
        }

        //Totally POST_ORDER. (start nodes -> leaf nodes that indicate start of the new scope).
        ast.visit(VisitorMode.POST, (n, next) -> {

            //skip start/prog at while/until/if/else parent nodes.
            if(n.getValue().getType().equals("start") && n.getValue().getValue().equals("prog")
                    && ast.parent(n) != null && ast.parent(ast.parent(n)) != null
                    && ast.parent(ast.parent(n)).getValue().getType().equals("loop")
            )
                return;

            if (n.getValue().getType().equals("start") && n.getValue().getValue().equals("prog")
                    && ast.parent(n) != null && ast.parent(ast.parent(n)) != null
                    && ast.parent(ast.parent(n)).getValue().getType().equals("if")
            )
                return;

            if (n.getValue().getType().equals("start") && n.getValue().getValue().equals("prog")
                    && ast.parent(n) != null && ast.parent(ast.parent(n)) != null
                    && ast.parent(ast.parent(n)).getValue().getType().equals("else")
            )
                return;


            //start new prog (scope)
            if(n.getValue().getType().equals("start") && n.getValue().getValue().equals("prog")) {
                env.set(new Env(env.get()));
                this.blocks++;
            }

            else if(n.getValue().getType().equals("program") && ast.parent(n) != null && ast.parent(n).getValue().getType().equals("loop"))
                return;

            else if(n.getValue().getType().equals("program") && ast.parent(n) != null && ast.parent(n).getValue().getType().equals("if"))
                return;
            else if(n.getValue().getType().equals("program") && ast.parent(n) != null && ast.parent(n).getValue().getType().equals("else"))
                return;
            //end of program (remove current scope and get previous)
            else if(n.getValue().getType().equals("program")) {
                env.set(env.get().getPrev());
                this.blocks--;
            }

            else
                try{
                    applyOperation(ast, env, n, exp, lists, indices, params, functions, args, nextItr, null, null, 0);
                }
                catch (Exception e){
                    System.err.println(e); //if error -> break execution.
                    nextItr.setNextNode(null);
                    nextItr.setOpts(-1);
                }
        }, nextItr);
    }

    @Override
    protected void execFunction(FunctionInterpreter f,
                                PositionalTree<AstSymbol> ast,
                                LinkedStack<String> exp,
                                LinkedStack<FunctionInterpreter> functions,
                                VisitorsNextIteration<AstSymbol> nextItr,
                                LinkedList<Elem<?>> vector_i,
                                Map<String, Integer> vnames_idxs,
                                int vector_len) {
        AtomicReference<Env> env2 = new AtomicReference<>();
        env2.set(f.getContext());
        Node<AstSymbol> root = f.getRoot();

        LinkedStack<List<Elem<Object>>> lists = new LinkedStack<>();
        ArrayList<List<Elem<Object>>> indices = new ArrayList<>();

        ArrayList<Variable> params = new ArrayList<>();
        LinkedStack<ArrayList<Object>> args = new LinkedStack<ArrayList<Object>>();

        ast.visitFrom(VisitorMode.POST, (c, next) -> {

            //skip start/prog at while/if/else parent nodes.
            if(c.getValue().getType().equals("start") && c.getValue().getValue().equals("prog")
                    && ast.parent(c) != null && ast.parent(ast.parent(c)) != null
                    && ast.parent(ast.parent(c)).getValue().getType().equals("loop")
            )
                return;

            if (c.getValue().getType().equals("start") && c.getValue().getValue().equals("prog")
                    && ast.parent(c) != null && ast.parent(ast.parent(c)) != null
                    && ast.parent(ast.parent(c)).getValue().getType().equals("if")
            )
                return;

            if (c.getValue().getType().equals("start") && c.getValue().getValue().equals("prog")
                    && ast.parent(c) != null && ast.parent(ast.parent(c)) != null
                    && ast.parent(ast.parent(c)).getValue().getType().equals("else")
            )
                return;

            //start new prog (scope)
            if(c.getValue().getType().equals("start") && c.getValue().getValue().equals("prog")) {
                env2.set(new Env(env2.get()));
                this.blocks++;
            }


            else if(c.getValue().getType().equals("program") && ast.parent(c) != null && ast.parent(c).getValue().getType().equals("loop"))
                return;

            else if(c.getValue().getType().equals("program") && ast.parent(c) != null && ast.parent(c).getValue().getType().equals("if"))
                return;
            else if(c.getValue().getType().equals("program") && ast.parent(c) != null && ast.parent(c).getValue().getType().equals("else"))
                return;

            //end of program (remove current scope and get previous)
            else if(c.getValue().getType().equals("program")) {
                env2.set(env2.get().getPrev());
                this.blocks--;
            }

            else
                try{
                    applyOperation(ast, env2, c, exp, lists, indices, params, functions, args, nextItr, vector_i, vnames_idxs, vector_len);
                }
                catch (Exception e){
                    System.err.println(e); //if error -> break execution.
                    nextItr.setNextNode(null);
                    nextItr.setOpts(-1);
                }
        }, root, nextItr);
    }
}