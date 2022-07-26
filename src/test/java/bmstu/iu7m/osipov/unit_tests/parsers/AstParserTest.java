package bmstu.iu7m.osipov.unit_tests.parsers;

import bmstu.iu7m.osipov.configurations.PathStrings;
import bmstu.iu7m.osipov.services.grammars.AstSymbol;
import bmstu.iu7m.osipov.services.grammars.Grammar;
import bmstu.iu7m.osipov.services.interpret.BottomUpInterpreter;
import bmstu.iu7m.osipov.services.interpret.ModuleProcessor;
import bmstu.iu7m.osipov.services.interpret.Variable;
import bmstu.iu7m.osipov.services.interpret.optimizers.FunctionOptimizer;
import bmstu.iu7m.osipov.services.lexers.*;
import bmstu.iu7m.osipov.services.parsers.*;
import bmstu.iu7m.osipov.services.parsers.json.elements.JsonObject;
import bmstu.iu7m.osipov.structures.automats.DFA;
import bmstu.iu7m.osipov.structures.trees.BinarySearchTree;
import bmstu.iu7m.osipov.structures.trees.LinkedTree;
import bmstu.iu7m.osipov.unit_tests.json_parser.SimpleJsonParserTest;
import bmstu.iu7m.osipov.utils.PathStringUtils;
import bmstu.iu7m.osipov.utils.StringContainerComparator;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.io.File;
import java.io.IOException;

import static java.util.Arrays.stream;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        loader = AnnotationConfigContextLoader.class,
        classes = {}
)
public class AstParserTest {



    @Test
    public void test_langs_interpretation() {
        //assert test_lang_interpret("G_Ast_1.json", "ast\\ast_input1.txt", "ast11", false);
        //assert test_lang_interpret("G_Ast_2.json", "ast\\ast_input2.txt", "ast21", false);
        //assert test_lang_interpret("G_Ast_31.json", "ast\\ast_input3.txt", "ast31", false);
        //assert test_lang_interpret("G_Ast_5.json", "ast\\ast_input41.txt", "ast411", false);
        //assert test_lang_interpret("G_Ast_6.json", "ast\\ast_input6.txt", "ast6", false);
        //assert test_lang_interpret("G_Ast_6.json", "ast\\ast_input_61_matrix.txt", "ast61", false);
        //assert test_lang_interpret("G_Ast_7.json", "ast\\ast_modules\\ast_input_73.txt", "ast73", true);
        assert test_lang_interpret("G_Ast_7.json", "ast\\ast_modules\\ast_input_75.txt", "ast75", false);
    }


    private boolean test_lang_interpret(String g, String input, String suffix, boolean parseModules){
        g = PathStrings.GRAMMARS + g;
        input = PathStrings.PARSER_INPUT + input;
        try {
            test_interpreter(g, input, suffix, parseModules);
        } catch (IOException e){
            return false;
        }
        return true;
    }

    private void test_interpreter(String g, String input, String suffix, boolean parseModules) throws IOException {
        JsonObject G_OBJ = SimpleJsonParserTest.JSON_PARSER.parse(g);
        Grammar G = new Grammar(G_OBJ);
        System.out.println("Source G: ");
        System.out.println(G);
        System.out.println("input File: " + input);

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


        String modDir = PathStringUtils.truncatePath(input, 1);
        if(parseModules && modDir != null) {
            BottomUpInterpreter inter = new BottomUpInterpreter();
            ModuleProcessor mproc = new ModuleProcessor(parser, inter, modDir, input);

            LinkedTree<AstSymbol> ast = parser.translate(new File(input));
            assert ast != null;

            long start = System.currentTimeMillis();
            inter.interpret(ast);
            long end = System.currentTimeMillis();
            System.out.println("AST of execModule nodes: " + ast.getCount());
            System.out.println("Parsing tree of execModule nodes: " + t.getCount());
            System.out.println("Finished secs: " + (end - start) / 1000.0);
            return;
        }

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
        System.out.println("Finished secs: " + (end - start) / 1000.0);
    }

    /*
    @Test
    public void testList(){
        List<String> l = new ArrayList<>();
        List<String> l2 = new ArrayList<>(new ArrayList<>());

        l2.add("item1");
        System.out.println(l2.size());
        l.add("item1");
        l.add("item2");
        l.add("item3");

        l.add(2, "itempass");
        System.out.println(Math.floor(1.0) == 1.0);
        System.out.println(l);
        System.out.println(l2);
        //assert Double.isNaN(Double.longBitsToDouble(0x7ff0000000000001L));
        System.out.println("ab".repeat(0));
        Double d = ProcessNumber.parseNumber("1");
        assert d == 1.0;
    }


    @Test
    public void testStr(){
        String p1 = "bety.it.up";
        String p2 = "module";
        System.out.println(Arrays.stream(p1.split("\\.", 0)).collect(Collectors.toList()));
        System.out.println(Arrays.stream(p2.split("\\.", 0)).collect(Collectors.toList()));

        System.out.println("cwd = " + Main.CWD);
    }
     */
    @Test
    public void stub_test(){
        BinarySearchTree<Variable> t = new BinarySearchTree<>(new StringContainerComparator<>());
        Variable v1 = new Variable("i");
        Variable v2 = new Variable("i");
        v1.setStrVal("1000");
        v2.setStrVal("2000");
        t.add(v1);
        System.out.println(t.contains(v1));
        t.add(v2);

        assert t.getCount() == 1;
    }
}