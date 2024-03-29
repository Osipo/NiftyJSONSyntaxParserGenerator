package bmstu.iu7m.osipov.services.interpret;

import bmstu.iu7m.osipov.services.grammars.AstSymbol;
import bmstu.iu7m.osipov.structures.graphs.Elem;
import bmstu.iu7m.osipov.structures.trees.Node;
import bmstu.iu7m.osipov.utils.Value;

import java.util.ArrayList;
import java.util.List;

public class Variable implements Value<String> {
    private final String name;

    private String strVal; //expression (primitive)
    private Object val; // actual value.
    private List<Elem<Object>> items; //list expression (non-primitive -> list)
    private FunctionInterpreter function; //function expression.
    private Node<AstSymbol> next; //label -> ptr to next instruction.
    private Env subModule;

    private int category = 0; // 0 = simple, 1 = parameter, 2 = label, 3 = external func.

    public Variable(String name, int category){
        this.name = name;
        this.category = category;
        this.subModule = null;
    }

    public Variable(String name){
        this.name = name;
        this.category = 0;
        this.subModule = null;
    }

    @Override
    public String getValue(){
        return this.name;
    }

    public void setStrVal(String strVal) {
        this.strVal = strVal;
    }

    public String getStrVal() {
        return strVal;
    }

    public void setActualValue(Object v){
        this.val = v;
    }
    public Object getActualValue(){
        return this.val;
    }

    public void setItems(List<Elem<Object>> items){
        if(items != null)
            this.items = new ArrayList<>(items); //throw NullPointerException if argument is null.
        else
            this.items = null;
    }

    public List<Elem<Object>> getItems() {
        return items;
    }


    public boolean isList(){
        return items != null;
    }

    public int getCategory(){
        return this.category;
    }

    public void setFunction(FunctionInterpreter function) {
        this.function = function;
        if(function != null)
            this.function.setFunName(this.name, this);
    }

    public void setNextNode(Node<AstSymbol> n){
        if(this.category != 2)
            throw new UnsupportedOperationException("Variable is not a label and next command cannot be saved into it!");
        this.next = n;
    }

    public Node<AstSymbol> getNext() {
        if(this.category != 2)
            throw new UnsupportedOperationException("Variable is not a label and next command cannot be extracted!");
        return next;
    }

    public FunctionInterpreter getFunction() {
        return function;
    }

    public void setSubModule(Env subModule) {
        this.subModule = subModule;
    }

    public Env getSubModule() {
        return subModule;
    }

    @Override
    public String toString(){
        return this.name + ": " + this.strVal;
    }
}
