package bmstu.iu7m.osipov.unit_tests.parsers;

import bmstu.iu7m.osipov.configurations.PathStrings;
import bmstu.iu7m.osipov.services.grammars.Grammar;
import bmstu.iu7m.osipov.services.lexers.DFALexer;
import bmstu.iu7m.osipov.services.lexers.FALexerGenerator;
import bmstu.iu7m.osipov.services.lexers.LanguageSymbol;
import bmstu.iu7m.osipov.services.lexers.Token;
import bmstu.iu7m.osipov.services.parsers.LLParser;
import bmstu.iu7m.osipov.services.parsers.LRAlgorithm;
import bmstu.iu7m.osipov.services.parsers.LRParser;
import bmstu.iu7m.osipov.services.parsers.ParserMode;
import bmstu.iu7m.osipov.structures.automats.CNFA;
import bmstu.iu7m.osipov.structures.automats.DFA;
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        loader = AnnotationConfigContextLoader.class,
        classes = {}
)
public class LLParserTest {
    @Test
    public void parse_G_2_27() throws IOException {
        Grammar G = new Grammar(
                SimpleJsonParserTest.JSON_PARSER.parse(PathStrings.GRAMMARS + "G_2_27.json")
        );
        System.out.println("Source G: ");
        System.out.println(G);
        FALexerGenerator lg = new FALexerGenerator();
        CNFA nfa = lg.buildNFA(G);
        DFALexer lexer = new DFALexer(new DFA(nfa));
        lexer.getImagefromStr(PathStrings.LEXERS,"I_G_2_27");
        LLParser sa = new LLParser(G, lexer);
        sa.setParserMode(ParserMode.DEBUG);
        LinkedTree<LanguageSymbol> t = sa.parse(PathStrings.PARSER_INPUT + "I_G_2_27.txt");
        assert t != null;
        Graphviz.fromString(t.toDot("I_G_2_27")).render(Format.PNG).toFile(new File(PathStrings.LLPARSERS + "I_G_2_27"));
    }
}
