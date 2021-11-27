package bmstu.iu7m.osipov.unit_tests.parsers;

import bmstu.iu7m.osipov.configurations.PathStrings;
import bmstu.iu7m.osipov.services.grammars.Grammar;
import bmstu.iu7m.osipov.services.grammars.directives.AttributeProcessorSDT;
import bmstu.iu7m.osipov.services.grammars.directives.ElementProcessorSDT;
import bmstu.iu7m.osipov.services.grammars.directives.PrintSDT;
import bmstu.iu7m.osipov.services.lexers.DFALexer;
import bmstu.iu7m.osipov.services.lexers.FALexerGenerator;
import bmstu.iu7m.osipov.services.lexers.LanguageSymbol;
import bmstu.iu7m.osipov.services.parsers.LRAlgorithm;
import bmstu.iu7m.osipov.services.parsers.LRParser;
import bmstu.iu7m.osipov.services.parsers.ParserMode;
import bmstu.iu7m.osipov.structures.automats.CNFA;
import bmstu.iu7m.osipov.structures.automats.DFA;
import bmstu.iu7m.osipov.structures.trees.LinkedTree;
import bmstu.iu7m.osipov.structures.trees.ReverseChildren;
import bmstu.iu7m.osipov.structures.trees.VisitorMode;
import bmstu.iu7m.osipov.structures.trees.translators.ExecuteTranslationNode;
import bmstu.iu7m.osipov.structures.trees.translators.TranslationsAttacher;
import bmstu.iu7m.osipov.unit_tests.json_parser.SimpleJsonParserTest;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.io.File;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        loader = AnnotationConfigContextLoader.class,
        classes = {}
)
public class SLRParserTranslationTest {

    @Test
    public void test_translations() throws Exception{
        test("javafx_xml.json","stage_example1.xml");
    }

    public void test(String grammar, String input) throws Exception {
        Grammar G = new Grammar(
                SimpleJsonParserTest.JSON_PARSER.parse(PathStrings.GRAMMARS + grammar)
        );
        System.out.println("Source G: ");
        System.out.println(G);

        String norm_input = input.substring(0, input.indexOf('.'));
        String norm_grammar = grammar.substring(0, grammar.indexOf('.'));

        FALexerGenerator lg = new FALexerGenerator();
        CNFA nfa = lg.buildNFA(G);
        DFALexer lexer = new DFALexer(new DFA(nfa));

        lexer.getImagefromStr(PathStrings.LEXERS,"I_G_"+norm_grammar);

        LRParser sa = new LRParser(G, lexer, LRAlgorithm.SLR);
        sa.setParserMode(ParserMode.DEBUG);

        LinkedTree<LanguageSymbol> t = sa.parse(PathStrings.PARSER_INPUT + input);
        assert t != null;

        Graphviz.fromString(t.toDot(norm_input)).render(Format.PNG).toFile(new File(PathStrings.PARSERS + norm_input));

        //t.setVisitor(new RightToLeftNRVisitor<>()); // traverse children right to left
        t.visit(VisitorMode.PRE, new ReverseChildren()); // or just reverse them.

        Graphviz.fromString(t.toDot(norm_input)).render(Format.PNG).toFile(new File(PathStrings.PARSERS + norm_input + "Reversed"));

        System.out.println("tree nodes before "+t.getCount());
        t.visit(VisitorMode.PRE, new TranslationsAttacher(G, t.getCount()));
        System.out.println("tree nodes after attaching acts "+t.getCount());



        ExecuteTranslationNode act_executor = new ExecuteTranslationNode();
        AttributeProcessorSDT actor = new AttributeProcessorSDT();
        ElementProcessorSDT elem_actor = new ElementProcessorSDT(actor);

        act_executor.putActionParser("createObject", elem_actor);
        act_executor.putActionParser("putAttr", elem_actor);
        act_executor.putActionParser("putAttr", actor);
        act_executor.putActionParser("showAttrs", actor);
        act_executor.putActionParser("addPrefix", actor);
        act_executor.putActionParser("removePrefix", actor);


        System.out.println("Perform translation... :");
        t.visit(VisitorMode.PRE, act_executor);// find and execute.
    }

}
