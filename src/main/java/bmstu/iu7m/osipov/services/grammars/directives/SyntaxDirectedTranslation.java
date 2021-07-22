package bmstu.iu7m.osipov.services.grammars.directives;

import bmstu.iu7m.osipov.services.grammars.SyntaxSymbol;
import bmstu.iu7m.osipov.services.lexers.Translation;

import java.util.Map;

public class SyntaxDirectedTranslation implements Translation {
    private String actName;
    private int pos;
    private Map<String, String> arguments;



    public SyntaxDirectedTranslation(String actName, int pos, Map<String, String> args){
        if(actName == null || actName.length() == 0)
            throw new IllegalArgumentException("Argument action name must be non-empty string!");
        else if(pos < 0)
            throw new IllegalArgumentException("Argument position must be positive ( >= 0)");
        else if(args == null)
            throw new IllegalArgumentException("Map of arguments (args) of action must not be null");

        this.actName = actName;
        this.pos = pos;
        this.arguments = args;
    }

    @Override
    public String getActName() {
        return actName;
    }

    @Override
    public int getPos() {
        return pos;
    }

    @Override
    public Map<String, String> getArguments() {
        return arguments;
    }

    @Override
    public String toString() {
        return "{" +
                "act = '" + actName + '\'' +
                ", \n\targuments = " + arguments +
                "\n}";
    }
}
