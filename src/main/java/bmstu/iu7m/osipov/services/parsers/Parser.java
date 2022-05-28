package bmstu.iu7m.osipov.services.parsers;

import bmstu.iu7m.osipov.services.grammars.*;
import bmstu.iu7m.osipov.services.lexers.*;
import bmstu.iu7m.osipov.structures.trees.LinkedTree;

import java.io.File;
import java.io.IOException;

public abstract class Parser {
    protected ILexer lexer;
    protected String empty;//empty symbol of grammar.
    protected boolean isParsed;

    protected Grammar G; //add Grammar field.

    protected ParserMode mode;

    public void setParserMode(ParserMode mode){
        this.mode = mode;
    }

    public ParserMode getParserMode(){
        return this.mode;
    }

    public abstract File getImage() throws IOException;

    public LinkedTree<LanguageSymbol> parse(String fname){
        return parse(new File(fname));
    }

    public abstract LinkedTree<LanguageSymbol> parse(File f);

    public abstract void showMessage(String body);

    public Parser(Grammar G, ILexer lexer) {
        this.lexer = lexer;
        this.isParsed = true;
        this.empty = G.getEmpty();
        lexer.setKeywords(G.getKeywords());
        lexer.setOperands(G.getOperands());
        lexer.setOperators(G.getOperators());
        lexer.setAliases(G.getAliases());
        lexer.setCommentLine(G.getCommentLine());
        lexer.setMlCommentStart(G.getMlCommentStart());
        lexer.setMlCommentEnd(G.getMlCommentEnd());
        lexer.setIdName(G.getIdName());
        lexer.setSeparators(G.getSeparators());
        this.mode = ParserMode.HIDE;
        this.G = G;
    }
}
