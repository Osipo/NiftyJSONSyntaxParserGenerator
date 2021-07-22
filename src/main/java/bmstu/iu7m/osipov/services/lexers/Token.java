package bmstu.iu7m.osipov.services.lexers;

import bmstu.iu7m.osipov.services.grammars.GrammarSymbol;

/**
 * Represents lexer elements.
 */
public class Token extends GrammarSymbol implements LanguageSymbol {
    private String lexem;

    // l,c holds line and column from src file.
    private int l;
    private int c;

    public Token(String name, String lexem, char type, int l, int c){
        super(type, name);
        this.lexem = lexem;
        this.l = l;
        this.c = c;
    }

    public Token(Token t){
        super(t.getType(), t.getName());
        this.lexem = t.getLexeme();
        this.l = t.getLine();
        this.c = t.getColumn();
    }

    @Override
    public int getLine() {
        return l;
    }

    @Override
    public int getColumn() {
        return c;
    }

    public void setLexem(String lexem) {
        this.lexem = lexem;
    }

    public void setName(String name) {
        this.val = name;
    }

    @Override
    public String getLexeme() {
        return lexem;
    }

    public String getName() {
        return val;
    }


    @Override
    public String toString(){
        if(type == 't')
            return lexem != null ? lexem : val;
        else if(type == 'n')
            return val;
        else
            return val + "/" + lexem;
    }
}
