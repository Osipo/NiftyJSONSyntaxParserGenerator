package bmstu.iu7m.osipov.services.interpret;

import bmstu.iu7m.osipov.utils.Value;

public class Variable implements Value<String> {
    private final String name;
    private String strVal;
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

    public String getStrVal() {
        return strVal;
    }
}
