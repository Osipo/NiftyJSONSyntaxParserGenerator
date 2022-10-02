package bmstu.iu7m.osipov.services.grammars;

import bmstu.iu7m.osipov.services.grammars.AstSymbol;

public class AstNode implements AstSymbol {
    protected String value;
    protected String type;

    protected boolean cond;

    public AstNode(String type, String value){
        this.type = type;
        this.value = value;
        this.cond = false;
    }

    @Override
    public boolean getCond() {
        return cond;
    }

    @Override
    public void setCond(boolean cond) {
        this.cond = cond;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return type + "/" + value + "\n";
    }
}
