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
    private List<Elem<Object>> items; //list expression (non-primitive -> list)
    private FunctionInterpreter function; //function expression.
    private Node<AstSymbol> next; //label -> ptr to next instruction.

    private int category = 0; // 0 = simple, 1 = parameter, 2 = label, 3 = external func.

    public Variable(String name, int category){
        this.name = name;
        this.category = category;
    }

    public Variable(String name){
        this.name = name;
        this.category = 0;
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

    public void setItems(List<Elem<Object>> items){
        this.items = new ArrayList<>(items);
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

    @Override
    public String toString(){
        return this.name + ": " + this.strVal;
    }
}
