package bmstu.iu7m.osipov.services.interpret;

import bmstu.iu7m.osipov.structures.graphs.Elem;
import bmstu.iu7m.osipov.utils.Value;

import java.util.ArrayList;
import java.util.List;

public class Variable implements Value<String> {
    private final String name;
    private String strVal; //expression (primitive)
    private List<Elem<Object>> items; //list expression (non-primitive -> list)
    private FunctionInterpreter function; //function expression.

    private int category = 0; // 0 = simple, 1 = parameter.

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

    public void setItems(List<Elem<Object>> items){
        this.items = new ArrayList<>(items);
    }

    public List<Elem<Object>> getItems() {
        return items;
    }

    public String getStrVal() {
        return strVal;
    }

    public boolean isList(){
        return items != null;
    }

    public int getCategory(){
        return this.category;
    }

    public void setFunction(FunctionInterpreter function) {
        this.function = function;
        this.function.setFunName(this.name);
    }

    public FunctionInterpreter getFunction() {
        return function;
    }
}
