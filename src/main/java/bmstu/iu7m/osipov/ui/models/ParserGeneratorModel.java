package bmstu.iu7m.osipov.ui.models;

import bmstu.iu7m.osipov.services.grammars.Grammar;
import bmstu.iu7m.osipov.services.lexers.ILexer;
import bmstu.iu7m.osipov.services.parsers.Parser;

public class ParserGeneratorModel {
    private ILexer curLexer;
    private Parser curParser;
    private Grammar G;

    public ParserGeneratorModel(){

    }

    public Grammar getGrammar(){
        return this.G;
    }

    public void setGrammar(Grammar g) {
        this.G = g;
    }

    public ILexer getCurLexer() {
        return curLexer;
    }

    public Parser getCurParser() {
        return curParser;
    }

    public void setCurLexer(ILexer curLexer) {
        this.curLexer = curLexer;
    }

    public void setCurParser(Parser curParser) {
        this.curParser = curParser;
    }
}
