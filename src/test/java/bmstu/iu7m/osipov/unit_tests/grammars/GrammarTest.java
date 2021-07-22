package bmstu.iu7m.osipov.unit_tests.grammars;

import bmstu.iu7m.osipov.configurations.PathStrings;
import bmstu.iu7m.osipov.services.grammars.Grammar;
import bmstu.iu7m.osipov.services.grammars.GrammarString;
import bmstu.iu7m.osipov.services.grammars.GrammarSymbol;
import bmstu.iu7m.osipov.unit_tests.json_parser.SimpleJsonParserTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        loader = AnnotationConfigContextLoader.class,
        classes = {}
)
public class GrammarTest {

    @Test
    public void test_G_2_27(){
        Grammar G = new Grammar(
                SimpleJsonParserTest.parser.parse(PathStrings.GRAMMARS + "G_2_27.json")
        );
        assert G.getTerminals().size() == 14;
        assert G.getKeywords().size() == 2;
        assert G.getKeywords().contains("true");
        assert G.getKeywords().contains("false");
        System.out.println(G);
    }

    @Test
    public void testLeftRecursion(){
        Grammar G = new Grammar(
                SimpleJsonParserTest.parser.parse(PathStrings.GRAMMARS + "G_2_27.json")
        );
        System.out.println("Original G");
        System.out.println(G);

        Grammar G1 = Grammar.deleteLeftRecursion(G);

        System.out.println("Non-left recursive G");
        System.out.println(G1);

        Grammar G2 = Grammar.deleteLeftRecursion(G1);
        System.out.println("The same");
        System.out.println(G2);
        assert G1.getNonTerminals().equals(G2.getNonTerminals());
    }

    @Test
    public void test_GrammarStrings_Equility(){
        GrammarString s1 = new GrammarString();
        GrammarString s2 = new GrammarString();
        s1.addSymbol(new GrammarSymbol('n', "N"));
        s1.addSymbol(new GrammarSymbol('t', "a"));
        s2.addSymbol(new GrammarSymbol('n', "N"));
        s2.addSymbol(new GrammarSymbol('t', "a"));

        assert s1.equals(s2);

        //now change one symbol at s1.
        s1.getSymbols().get(1).setType('n');

        assert !s1.equals(s2);
    }
}
