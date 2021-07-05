package bmstu.iu7m.osipov.unit_tests.grammars;

import bmstu.iu7m.osipov.configurations.PathStrings;
import bmstu.iu7m.osipov.services.grammars.Grammar;
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
        System.out.println(G);
        Grammar G1 = Grammar.deleteLeftRecursion(G);
        System.out.println(G1);
        Grammar G2 = Grammar.deleteLeftRecursion(G1);
        System.out.println(G2);
    }
}
