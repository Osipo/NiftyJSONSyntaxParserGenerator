package bmstu.iu7m.osipov.unit_tests.parsers;

import bmstu.iu7m.osipov.configurations.PathStrings;
import bmstu.iu7m.osipov.services.grammars.AstSymbol;
import bmstu.iu7m.osipov.services.grammars.Grammar;
import bmstu.iu7m.osipov.services.interpret.BottomUpInterpreter;
import bmstu.iu7m.osipov.services.interpret.optimizers.FunctionOptimizer;
import bmstu.iu7m.osipov.services.lexers.*;
import bmstu.iu7m.osipov.services.parsers.*;
import bmstu.iu7m.osipov.services.parsers.json.elements.JsonObject;
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
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        loader = AnnotationConfigContextLoader.class,
        classes = {}
)
public class AstParserTest {

    @Test
    public void test_langs_interpretation() {
        //assert test_lang_interpret("G_Ast_1.json", "ast\\ast_input1.txt", "ast11");
        //assert test_lang_interpret("G_Ast_2.json", "ast\\ast_input2.txt", "ast21");
        //assert test_lang_interpret("G_Ast_31.json", "ast\\ast_input3.txt", "ast31");
        //assert test_lang_interpret("G_Ast_41.json", "ast\\ast_input41.txt", "ast411");
        //assert test_lang_interpret("G_Ast_5.json", "ast\\ast_input41.txt", "ast411");
        //assert test_lang_interpret("G_Ast_6.json", "ast\\ast_input6.txt", "ast6");
        assert test_lang_interpret("G_Ast_6.json", "ast\\ast_input_61_matrix.txt", "ast61");
    }


    private boolean test_lang_interpret(String g, String input, String suffix){
        g = PathStrings.GRAMMARS + g;
        input = PathStrings.PARSER_INPUT + input;
        try {
            test_interpreter(g, input, suffix);
        } catch (IOException e){
            return false;
        }
        return true;
    }

    private void test_interpreter(String g, String input, String suffix) throws IOException {
        JsonObject G_OBJ = SimpleJsonParserTest.JSON_PARSER.parse(g);
        Grammar G = new Grammar(G_OBJ);
        System.out.println("Source G: ");
        System.out.println(G);

        FALexerGenerator lg = new FALexerGenerator();
        DFALexer lexer = new DFALexer(new DFA(lg.buildNFA(G))); //NFA -> DFA -> min_DFA_lexer.

        //show lexer
        //lexer.getImagefromStr(PathStrings.LEXERS, suffix + "_right_lexer");

        LRAstTranslator parser = new LRAstTranslator(G, lexer, LRAlgorithm.SLR);
        parser.setParserMode(ParserMode.HIDE);
        System.out.println("Determined Grammar: " + parser.isValidGrammar());


        LinkedTree<LanguageSymbol> t = parser.parse(input);
        assert t != null;

        //show syntax parsing tree
        //Graphviz.fromString(t.toDot("astbefore")).render(Format.PNG).toFile(new File(PathStrings.PARSERS + "syntax_before_" + suffix));

        LinkedTree<AstSymbol> ast = parser.translate(new File(input));
        assert ast != null;

        //show semantic AST
        //Graphviz.fromString(ast.toDot("astafter")).render(Format.PNG).toFile(new File(PathStrings.PARSERS + "semantics_after_" + suffix));

        //remove tail_recursion.
        FunctionOptimizer remove_tail_recusrive_call = new FunctionOptimizer();
        remove_tail_recusrive_call.optimize(ast);


        //show semantic after tail_recursion eliminating AST
        Graphviz.fromString(ast.toDot("astafter")).render(Format.PNG).toFile(new File(PathStrings.PARSERS + "semantics_after_tail_" + suffix));

        //Phase 4. Interpret ast nodes.
        BottomUpInterpreter inter = new BottomUpInterpreter();

        //LocalTime t_start = LocalTime.now();
        long start = System.currentTimeMillis();
        inter.interpret(ast);
        long end = System.currentTimeMillis();
        //LocalTime t_end = LocalTime.from(t_start);


        System.out.println("AST nodes: " + ast.getCount());
        System.out.println("Parsing tree nodes: " + t.getCount());
        System.out.println("Finished mills: " + (end - start) / 1000.0);
    }

    @Test
    public void testList(){
        List<String> l = new ArrayList<>();
        l.add("item1");
        l.add("item2");
        l.add("item3");

        l.add(2, "itempass");
        System.out.println(Math.floor(1.0) == 1.0);
        System.out.println(l);
    }
}