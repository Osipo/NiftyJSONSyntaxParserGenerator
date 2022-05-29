package bmstu.iu7m.osipov.services.interpret;

import bmstu.iu7m.osipov.services.grammars.AstSymbol;
import bmstu.iu7m.osipov.structures.graphs.Elem;
import bmstu.iu7m.osipov.structures.trees.Node;


import java.util.ArrayList;
import java.util.List;

public class FunctionInterpreter {
    private Node<AstSymbol> root;
    private Env context;
    private List<Variable> params;

    private String funName;

    public FunctionInterpreter(Node<AstSymbol> root, Env context,
                               List<Variable> params)
    {
        this.root = root;
        this.context = new Env(context);
        this.params = new ArrayList<>(params);
    }


    public void setFunName(String name, Variable self){
        this.funName = name;
        this.context.add(self); //add reference to self in its context.
    }

    public void setContext(Env context){
        this.context = context;
    }

    public void bindArguments(List<Object> args) throws Exception {
        //System.out.println("expected: " + this.params.size());
        //System.out.println("actual: " + args.size());
        if(args.size() != this.params.size())
            throw new Exception("Arguments length mismatches with count of parameters of function '" + funName + "'");

        this.context = new Env(this.context); //init new execution context.

        for(int i = 0; i < args.size(); i++){
            Object a = args.get(i);
            Variable p_i = new Variable(this.params.get(i).getValue()); //this.params.get(i);

            if(a instanceof String)
                p_i.setStrVal((String) a);

            else if(a instanceof Elem){ //extract list item
                a = ((Elem<?>) a).getV1();
                if(a instanceof String)
                    p_i.setStrVal((String) a);
            }

            else if(a instanceof Variable){
                p_i.setStrVal(((Variable) a).getStrVal());
            }
            this.context.add(p_i);
        }
        //System.out.println(params);
    }

    public Node<AstSymbol> getRoot() {
        return root;
    }

    public Env getContext() {
        return context;
    }

    public String getFunctionName() {
        return funName;
    }
}