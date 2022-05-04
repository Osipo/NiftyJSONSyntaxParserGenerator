package bmstu.iu7m.osipov.services.interpret;

import bmstu.iu7m.osipov.utils.Value;

import java.util.ArrayList;
import java.util.List;

public class Variable implements Value<String> {
    private final String name;
    private String strVal;
    private List<String> items;

    public Variable(String name){
        this.name = name;
    }

    @Override
    public String getValue(){
        return this.name;
    }

    public void setStrVal(String strVal) {
        this.strVal = strVal;
    }

    public void setItems(List<String> items){
        this.items = new ArrayList<>(items);
    }

    public List<String> getItems() {
        return items;
    }

    public String getStrVal() {
        return strVal;
    }

    public boolean isList(){
        return items != null;
    }
}
