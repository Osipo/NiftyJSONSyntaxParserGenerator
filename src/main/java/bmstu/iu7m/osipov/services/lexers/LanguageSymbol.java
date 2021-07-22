package bmstu.iu7m.osipov.services.lexers;

import bmstu.iu7m.osipov.services.grammars.SyntaxSymbol;

public interface LanguageSymbol extends SyntaxSymbol {
    public String getName();
    public String getLexeme();
    public char getType();
    public int getLine();
    public int getColumn();

}
