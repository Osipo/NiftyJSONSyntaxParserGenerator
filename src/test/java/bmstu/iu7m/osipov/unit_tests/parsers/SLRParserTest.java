package bmstu.iu7m.osipov.unit_tests.parsers;

import bmstu.iu7m.osipov.configurations.PathStrings;
import bmstu.iu7m.osipov.services.grammars.Grammar;
import bmstu.iu7m.osipov.services.lexers.DFALexer;
import bmstu.iu7m.osipov.services.lexers.FALexerGenerator;
import bmstu.iu7m.osipov.services.lexers.LanguageSymbol;
import bmstu.iu7m.osipov.services.lexers.Token;
import bmstu.iu7m.osipov.services.parsers.LRAlgorithm;
import bmstu.iu7m.osipov.services.parsers.LRParser;
import bmstu.iu7m.osipov.services.parsers.ParserMode;
import bmstu.iu7m.osipov.structures.automats.CNFA;
import bmstu.iu7m.osipov.structures.automats.DFA;
import bmstu.iu7m.osipov.structures.trees.LinkedTree;
import bmstu.iu7m.osipov.structures.trees.SequentialNRVisitor;
import bmstu.iu7m.osipov.structures.trees.VisitorMode;
import bmstu.iu7m.osipov.structures.trees.reducers.BreakChainNode;
import bmstu.iu7m.osipov.structures.trees.reducers.DeleteUselessSyntaxNode;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        loader = AnnotationConfigContextLoader.class,
        classes = {}
)
public class SLRParserTest {

    @Test
    public void parse_G_2_27() throws IOException {
        Grammar G = new Grammar(
                SimpleJsonParserTest.parser.parse(PathStrings.GRAMMARS + "G_2_27.json")
        );
        System.out.println("Source G: ");
        System.out.println(G);
        FALexerGenerator lg = new FALexerGenerator();
        CNFA nfa = lg.buildNFA(G);
        DFALexer lexer = new DFALexer(new DFA(nfa));
        lexer.getImagefromStr(PathStrings.LEXERS,"I_G_2_27");
        LRParser sa = new LRParser(G, lexer, LRAlgorithm.SLR);
        sa.setParserMode(ParserMode.DEBUG);
        LinkedTree<LanguageSymbol> t = sa.parse(PathStrings.PARSER_INPUT + "I_G_2_27.txt");
        assert t != null;
        Graphviz.fromString(t.toDot("I_G_2_27")).render(Format.PNG).toFile(new File(PathStrings.PARSERS + "I_G_2_27"));
    }

    //LR(1) Grammar of L = c*dc*d [cdcd, ccdccd, ccccccccdd, etc.]
    @Test
    public void parse_G_416() throws IOException {
        Grammar G = new Grammar(
                SimpleJsonParserTest.parser.parse(PathStrings.GRAMMARS + "G_416.json")
        );
        System.out.println("Source G: ");
        System.out.println(G);
        FALexerGenerator lg = new FALexerGenerator();
        CNFA nfa = lg.buildNFA(G);
        DFALexer lexer = new DFALexer(new DFA(nfa));
        lexer.getImagefromStr(PathStrings.LEXERS, "I_G_416");
        LRParser sa = new LRParser(G, lexer, LRAlgorithm.SLR);
        sa.setParserMode(ParserMode.DEBUG);
        LinkedTree<LanguageSymbol> t = sa.parse(PathStrings.PARSER_INPUT + "I_G_416.txt");
        assert t != null;
        Graphviz.fromString(t.toDot("I_G_416")).render(Format.PNG).toFile(new File(PathStrings.PARSERS + "I_G_416"));
    }

    @Test
    public void test_xml4_grammar() throws IOException {
        Grammar G = new Grammar(
                SimpleJsonParserTest.parser.parse(PathStrings.GRAMMARS + "Xml_4th.json")
        );
        System.out.println("Source G: ");
        System.out.println(G);
        FALexerGenerator lg = new FALexerGenerator();
        CNFA nfa = lg.buildNFA(G);
        DFALexer lexer = new DFALexer(new DFA(nfa));
        lexer.getImagefromStr(PathStrings.LEXERS,"I_XML_4th");
        LRParser sa = new LRParser(G, lexer, LRAlgorithm.SLR);
        sa.setParserMode(ParserMode.DEBUG);
        LinkedTree<LanguageSymbol> t = sa.parse(PathStrings.PARSER_INPUT + "pseudo_xml4.xml");
        assert t != null;
        Graphviz.fromString(t.toDot("I_XML_4th")).render(Format.PNG).toFile(new File(PathStrings.PARSERS + "I_XML_4th"));

        t.setVisitor(new SequentialNRVisitor<>());

        DeleteUselessSyntaxNode a1 = new DeleteUselessSyntaxNode(G);
        BreakChainNode a2 = new BreakChainNode();

        t.visit(VisitorMode.PRE, a1);
        t.visit(VisitorMode.PRE, a2);
        Graphviz.fromString(t.toDot("I_XML_4th_reduced")).render(Format.PNG).toFile(new File(PathStrings.PARSERS + "I_XML_4th_reduced"));

    }
}
