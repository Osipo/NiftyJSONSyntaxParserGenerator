package bmstu.iu7m.osipov.services.parsers;

import bmstu.iu7m.osipov.structures.graphs.Pair;
import bmstu.iu7m.osipov.structures.lists.LinkedStack;
import bmstu.iu7m.osipov.services.grammars.*;
import bmstu.iu7m.osipov.services.lexers.*;
import bmstu.iu7m.osipov.structures.trees.LinkedTree;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class Parser {
    protected ILexer lexer;
    protected String empty;//empty symbol of grammar.
    protected boolean isParsed;

    protected ParserMode mode;

    public void setParserMode(ParserMode mode){
        this.mode = mode;
    }

    public ParserMode getParserMode(){
        return this.mode;
    }

    public abstract File getImage() throws IOException;

    public abstract LinkedTree<Token> parse(String fname);

    public Parser(Grammar G, ILexer lexer) {
        this.lexer = lexer;
        this.isParsed = true;
        this.empty = G.getEmpty();
        lexer.setKeywords(G.getKeywords());
        lexer.setOperands(G.getOperands());
        lexer.setAliases(G.getAliases());
        lexer.setCommentLine(G.getCommentLine());
        lexer.setMlCommentStart(G.getMlCommentStart());
        lexer.setMlCommentEnd(G.getMlCommentEnd());
        lexer.setIdName(G.getIdName());
        lexer.setSeparators(G.getSeparators());
        this.mode = ParserMode.HIDE;
    }
}
