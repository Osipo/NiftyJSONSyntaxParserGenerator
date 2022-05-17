package bmstu.iu7m.osipov.unit_tests.parsers;

import bmstu.iu7m.osipov.configurations.PathStrings;
import bmstu.iu7m.osipov.services.grammars.AstSymbol;
import bmstu.iu7m.osipov.services.grammars.Grammar;
import bmstu.iu7m.osipov.services.interpret.BaseInterpreter;
import bmstu.iu7m.osipov.services.lexers.*;
import bmstu.iu7m.osipov.services.parsers.*;
import bmstu.iu7m.osipov.services.parsers.json.elements.JsonObject;
import bmstu.iu7m.osipov.structures.automats.CNFA;
import bmstu.iu7m.osipov.structures.automats.DFA;
import bmstu.iu7m.osipov.structures.graphs.Elem;
import bmstu.iu7m.osipov.structures.trees.LinkedTree;
import bmstu.iu7m.osipov.unit_tests.json_parser.SimpleJsonParserTest;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        loader = AnnotationConfigContextLoader.class,
        classes = {}
)
public class AstParserTest {

    @Test
    public void test_csharp_cut() throws IOException{
        JsonObject G_OBJ = SimpleJsonParserTest.JSON_PARSER.parse(PathStrings.GRAMMARS + "C#_Cut.json");
        Grammar G = new Grammar(
                G_OBJ
        );
        System.out.println("Source G: ");
        System.out.println(G);

        //make lexer.
        FALexerGenerator lg = new FALexerGenerator();
        CNFA nfa = lg.buildNFA(G);
        DFALexer lexer = new DFALexer(new DFA(nfa));

        lexer.getImagefromStr(PathStrings.LEXERS,"csharp_cut_lexer");
    }

    @Test
    public void test_ast1_valid_lr_parser() throws IOException {
        JsonObject G_OBJ = SimpleJsonParserTest.JSON_PARSER.parse(PathStrings.GRAMMARS + "G_Ast_1.json");
        Grammar G = new Grammar(
                G_OBJ
        );
        System.out.println("Source G: ");
        System.out.println(G);

        //make lexer.
        FALexerGenerator lg = new FALexerGenerator();
        CNFA nfa = lg.buildNFA(G);
        DFALexer lexer = new DFALexer(new DFA(nfa));

        //make parser
        LRAstTranslator sa = new LRAstTranslator(G, lexer, LRAlgorithm.SLR);
        sa.setParserMode(ParserMode.DEBUG);

        lexer.getImagefromStr(PathStrings.LEXERS,"ast1_right_lexer");

        //Can make parser from Grammar?
        assert sa.isValidTable();

        LinkedTree<LanguageSymbol> t = sa.parse(PathStrings.PARSER_INPUT + "ast\\ast_input1.txt");
        assert t != null;
        //System.out.println(t);

        Graphviz.fromString(t.toDot("astbefore")).render(Format.PNG).toFile(new File(PathStrings.PARSERS + "syntax_before_ast1"));

        LinkedTree<AstSymbol> ast = sa.translate(new File(PathStrings.PARSER_INPUT + "ast\\ast_input1.txt"));
        assert ast != null;
        Graphviz.fromString(ast.toDot("astafter")).render(Format.PNG).toFile(new File(PathStrings.PARSERS + "semantics_after_ast1"));

        System.out.println("AST nodes: " + ast.getCount());
        System.out.println("Parsing tree nodes: " + t.getCount());

        //Phase 4. Interpret ast nodes.
        BaseInterpreter inter = new BaseInterpreter();
        inter.interpret(ast);
    }

    @Test
    public void test_ast2_valid_lr_parser() throws IOException {
        JsonObject G_OBJ = SimpleJsonParserTest.JSON_PARSER.parse(PathStrings.GRAMMARS + "G_Ast_2.json");
        Grammar G = new Grammar(
                G_OBJ
        );
        System.out.println("Source G: ");
        System.out.println(G);

        //make lexer.
        FALexerGenerator lg = new FALexerGenerator();
        CNFA nfa = lg.buildNFA(G);
        DFALexer lexer = new DFALexer(new DFA(nfa));

        //make parser
        LRAstTranslator sa = new LRAstTranslator(G, lexer, LRAlgorithm.SLR);
        sa.setParserMode(ParserMode.DEBUG);

        lexer.getImagefromStr(PathStrings.LEXERS,"ast2_right_lexer");

        //Can make parser from Grammar?
        assert sa.isValidTable();

        LinkedTree<LanguageSymbol> t = sa.parse(PathStrings.PARSER_INPUT + "ast\\ast_input2.txt");
        assert t != null;
        //System.out.println(t);

        Graphviz.fromString(t.toDot("astbefore")).render(Format.PNG).toFile(new File(PathStrings.PARSERS + "syntax_before_ast2"));

        LinkedTree<AstSymbol> ast = sa.translate(new File(PathStrings.PARSER_INPUT + "ast\\ast_input2.txt"));
        assert ast != null;
        Graphviz.fromString(ast.toDot("astafter")).render(Format.PNG).toFile(new File(PathStrings.PARSERS + "semantics_after_ast2"));

        System.out.println("AST nodes: " + ast.getCount());
        System.out.println("Parsing tree nodes: " + t.getCount());

        //ast.visit(VisitorMode.POST, null);

        //Phase 4. Interpret ast nodes.
        BaseInterpreter inter = new BaseInterpreter();
        inter.interpret(ast);
    }

    @Test
    public void test_ast3_valid_lr_parser() throws IOException {
        JsonObject G_OBJ = SimpleJsonParserTest.JSON_PARSER.parse(PathStrings.GRAMMARS + "G_Ast_3.json");
        Grammar G = new Grammar(
                G_OBJ
        );
        System.out.println("Source G: ");
        System.out.println(G);

        //make lexer.
        FALexerGenerator lg = new FALexerGenerator();
        CNFA nfa = lg.buildNFA(G);
        DFALexer lexer = new DFALexer(new DFA(nfa));

        //make parser
        LRAstTranslator sa = new LRAstTranslator(G, lexer, LRAlgorithm.SLR);
        sa.setParserMode(ParserMode.DEBUG);

        lexer.getImagefromStr(PathStrings.LEXERS,"ast3_right_lexer");

        //Can make parser from Grammar?
        assert sa.isValidTable();

        LinkedTree<LanguageSymbol> t = sa.parse(PathStrings.PARSER_INPUT + "ast\\ast_input3.txt");
        assert t != null;
        //System.out.println(t);

        Graphviz.fromString(t.toDot("astbefore")).render(Format.PNG).toFile(new File(PathStrings.PARSERS + "syntax_before_ast3"));

        LinkedTree<AstSymbol> ast = sa.translate(new File(PathStrings.PARSER_INPUT + "ast\\ast_input3.txt"));
        assert ast != null;
        Graphviz.fromString(ast.toDot("astafter")).render(Format.PNG).toFile(new File(PathStrings.PARSERS + "semantics_after_ast3"));

        System.out.println("AST nodes: " + ast.getCount());
        System.out.println("Parsing tree nodes: " + t.getCount());

        //ast.visit(VisitorMode.POST, null);

        //Phase 4. Interpret ast nodes.
        BaseInterpreter inter = new BaseInterpreter();
        inter.interpret(ast);
    }

    @Test
    public void test_ast4_valid_lr_parser() throws IOException {
        JsonObject G_OBJ = SimpleJsonParserTest.JSON_PARSER.parse(PathStrings.GRAMMARS + "G_Ast_4.json");
        Grammar G = new Grammar(
                G_OBJ
        );
        System.out.println("Source G: ");
        System.out.println(G);

        //make lexer.
        FALexerGenerator lg = new FALexerGenerator();
        CNFA nfa = lg.buildNFA(G);
        DFALexer lexer = new DFALexer(new DFA(nfa));

        //LLParserGenerator.firstTable_1(G);

        //lexer.showTranTable();

        //make parser
        LRAstTranslator sa = new LRAstTranslator(G, lexer, LRAlgorithm.SLR);
        sa.setParserMode(ParserMode.DEBUG);

        lexer.getImagefromStr(PathStrings.LEXERS,"ast4_right_lexer");

        //Can make parser from Grammar?
        assert sa.isValidTable();

        LinkedTree<LanguageSymbol> t = sa.parse(PathStrings.PARSER_INPUT + "ast\\ast_input4.txt");
        assert t != null;

        Graphviz.fromString(t.toDot("astbefore")).render(Format.PNG).toFile(new File(PathStrings.PARSERS + "syntax_before_ast4"));

        //phase 1 - 3 completed. (AST tree has been built.)
        LinkedTree<AstSymbol> ast = sa.translate(new File(PathStrings.PARSER_INPUT + "ast\\ast_input4.txt"));
        assert ast != null;

        Graphviz.fromString(ast.toDot("astafter")).render(Format.PNG).toFile(new File(PathStrings.PARSERS + "semantics_after_ast4"));

        System.out.println("AST nodes: " + ast.getCount());
        System.out.println("Parsing tree nodes: " + t.getCount());

        //TODO: phase 4. (interpretation).

    }

    @Test
    public void testList(){
        List<Elem<String>> l = new ArrayList<>();
        l.add(new Elem<>("1"));
        l.add(new Elem<>("2"));

        System.out.println("l = " + l); //original list.
        List<Elem<String>> cl = new ArrayList<>(l); //wrapper Elem  allow change content between lists.

        cl.get(0).setV1("1000");

        System.out.println("cl = " + cl);
        assert cl.get(0).equals(l.get(0));
        cl.clear();
        System.out.println("l = " + l);
    }
}