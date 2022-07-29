package bmstu.iu7m.osipov.services.interpret;

import bmstu.iu7m.osipov.services.grammars.AstSymbol;
import bmstu.iu7m.osipov.structures.graphs.Elem;
import bmstu.iu7m.osipov.structures.trees.Node;


import java.util.ArrayList;
import java.util.List;

public class FunctionInterpreter {
    protected Node<AstSymbol> root;
    protected Env context;
    protected List<Variable> params;

    protected String funName;

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


    //TODO: Parse possible arg types: (variable, elem, list, function, str/number)
    public void bindArguments(List<Object> args) throws Exception {
       // System.out.println("expected: " + this.params.size());
       // System.out.println("actual: " + args.size());
        if(args.size() != this.params.size())
            throw new Exception("Arguments length mismatches with count of parameters of function '" + funName + "'");

        this.context = new Env(this.context); //init new execution context.

        for(int i = 0; i < args.size(); i++){
            Object a = args.get(i);
            Variable p_i = new Variable(this.params.get(i).getValue()); //variable_name

            //if elem get elem value
            if(a instanceof Elem) { //extract list item
                while(a instanceof Elem)
                    a = ((Elem<?>) a).getV1();
            }

            //Check raw types.
            if(a instanceof String)
                p_i.setStrVal((String) a);
            else if(a instanceof Number)
                p_i.setStrVal(a.toString());
            else if(a instanceof List){
                p_i.setItems((List<Elem<Object>>) a);
                p_i.setStrVal(p_i.getItems().toString());
            }
            else if(a instanceof FunctionInterpreter){
                p_i.setFunction((FunctionInterpreter) a);
            }

            //passed some variable name a as argument.
            //Check variable types.
            else if(a instanceof Variable){
                p_i.setStrVal(((Variable) a).getStrVal());

                if(((Variable) a).getFunction() != null) { //function name.
                    FunctionInterpreter of = ((Variable) a).getFunction();
                    FunctionInterpreter nf = new FunctionInterpreter(of.getRoot(), of.getContext(), of.params);
                    p_i.setFunction(nf); //do local self reference.
                }
                else if(((Variable) a).getItems() != null)
                    p_i.setItems(new ArrayList<>(((Variable) a).getItems()));
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

    public List<Variable> getParams(){
        return this.params;
    }
}