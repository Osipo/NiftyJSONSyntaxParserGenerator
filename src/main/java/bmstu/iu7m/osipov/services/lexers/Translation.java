package bmstu.iu7m.osipov.services.lexers;

import java.util.Map;

public interface Translation extends LanguageSymbol {
    public String getActName();
    public int getPos();
    public Map<String, String> getArguments();

    public default String getName(){
        return getActName();
    }
    public default char getType(){
        return 't';
    }

    public default String getLexeme(){
        return getActName();
    }

    public default int getLine(){
        throw new UnsupportedOperationException("Syntax Action is out of source stream (input).");
    }

    public default int getColumn(){
        throw new UnsupportedOperationException("Syntax Action is out of source stream (input).");
    }
}
