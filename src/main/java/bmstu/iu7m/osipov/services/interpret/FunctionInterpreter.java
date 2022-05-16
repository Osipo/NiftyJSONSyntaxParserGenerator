package bmstu.iu7m.osipov.services.interpret;

import bmstu.iu7m.osipov.services.grammars.AstSymbol;
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


    public void setFunName(String name){
        this.funName = name;
    }

    public void bindArguments(List<Object> args) throws Exception {
        if(args.size() != this.params.size())
            throw new Exception("Arguments length mismatch with parameters of function '" + funName + "'");
        for(int i = 0; i < args.size(); i++){
            Object a = args.get(i);
            Variable p_i = this.params.get(i);
            if(a instanceof String)
                p_i.setStrVal((String) a);
            this.context.add(p_i);
        }
    }

    public Node<AstSymbol> getRoot() {
        return root;
    }

    public Env getContext() {
        return context;
    }

    public String getFunName() {
        return funName;
    }
}