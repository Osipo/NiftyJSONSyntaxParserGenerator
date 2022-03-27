package bmstu.iu7m.osipov.unit_tests.parsers;

import bmstu.iu7m.osipov.configurations.PathStrings;
import bmstu.iu7m.osipov.services.grammars.Grammar;
import bmstu.iu7m.osipov.services.grammars.directives.*;
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
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.layout.BackgroundFill;
import javafx.stage.Stage;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import javax.swing.*;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        loader = AnnotationConfigContextLoader.class,
        classes = {}
)
public class SLRParserTranslationTest {

    @BeforeClass
    public static void initAWT() throws InterruptedException {
        System.setProperty("java.awt.headless", "false");
        System.out.println("Headless of AWT set to false");
        final CountDownLatch latch = new CountDownLatch(1);
        /* wait until Toolkit will be initialized */
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new JFXPanel(); // initializes JavaFX environment for tests.
                latch.countDown();
            }
        });
        latch.await();
        System.out.println("SwingGUI > Toolkit initialized.");
    }

    @Test
    public void test_translations() throws Exception{

        /* execute at JavaFX-Thread */
        final CountDownLatch finish = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                test_yml_type_scheme(finish);
                //testScheme(finish,"javafx_xml.json", "fx_constructors_schema_nested.xml", "stage_example1.xml");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        finish.await();
    }

    //Check new format of schema.
    public void test_yml_type_scheme(final CountDownLatch finish) throws Exception {

        //New Grammar for schema.
        Grammar G = new Grammar(
                SimpleJsonParserTest.JSON_PARSER.parse(PathStrings.GRAMMARS + "java_meta_objects.json")
        );
        System.out.println("Source G: ");
        System.out.println(G);

        String example = "java_objects_constructors.txt";
        String norm_example = example.substring(0, example.indexOf('.')); //remove part after '.' inclusive ['.'...]

        String doc = "stage_example1.xml";

        //build lexer.
        FALexerGenerator lg = new FALexerGenerator();
        CNFA nfa = lg.buildNFA(G);
        DFALexer lexer = new DFALexer(new DFA(nfa));

        //build parser.
        LRParser sa = new LRParser(G, lexer, LRAlgorithm.SLR);
        sa.setParserMode(ParserMode.DEBUG);

        LinkedTree<LanguageSymbol> stree = sa.parse(PathStrings.PARSER_INPUT + example);
        assert stree != null;

        //Graphviz.fromString(stree.toDot(norm_example)).render(Format.PNG).toFile(new File(PathStrings.PARSERS + norm_example));
        stree.visit(VisitorMode.PRE, new ReverseChildren());
        stree.visit(VisitorMode.PRE, new TranslationsAttacher(G, stree.getCount()));

        TypeProcessorYmlSDT type_actor = new TypeProcessorYmlSDT();
        ExecuteTranslationNode act_executor = new ExecuteTranslationNode();
        act_executor.putActionParser("addPkg", type_actor);
        act_executor.putActionParser("popPkg", type_actor);
        act_executor.putActionParser("addType", type_actor);
        act_executor.putActionParser("addCtrParam", type_actor);
        act_executor.putActionParser("addParamType", type_actor);
        act_executor.putActionParser("addGenericParamType", type_actor);
        act_executor.putActionParser("removeInnerGenType", type_actor);
        stree.visit(VisitorMode.PRE, act_executor);// parse scheme definition.

        System.out.println("scheme parsed.");

        System.out.println("types");
        System.out.println(type_actor.getTypes().toString());
        System.out.println("aliases");
        System.out.println(type_actor.getAliases().toString());

        //Create and parse XML document with provided schema.
        Grammar G2 = new Grammar(
                SimpleJsonParserTest.JSON_PARSER.parse(PathStrings.GRAMMARS + "javafx_xml.json")
        );
        FALexerGenerator lg2 = new FALexerGenerator();
        CNFA nfa2 = lg2.buildNFA(G2);
        DFALexer lexer2 = new DFALexer(new DFA(nfa2));
        LRParser sa2 = new LRParser(G2, lexer2, LRAlgorithm.SLR);
        sa2.setParserMode(ParserMode.DEBUG);

        LinkedTree<LanguageSymbol> dtree = sa2.parse(PathStrings.PARSER_INPUT + doc);
        assert dtree != null;
        dtree.visit(VisitorMode.PRE, new ReverseChildren());
        dtree.visit(VisitorMode.PRE, new TranslationsAttacher(G2, dtree.getCount()));


        ExecuteTranslationNode exec2 = new ExecuteTranslationNode();
        JavaFXExtraXMLProcessorSDT elem_actor = new JavaFXExtraXMLProcessorSDT(type_actor);
        exec2.putActionParser("putAttr", elem_actor);
        exec2.putActionParser("createObject", elem_actor);
        exec2.putActionParser("removePrefix", elem_actor);

        dtree.visit(VisitorMode.PRE, exec2);

        elem_actor.tryApplySizeRelations(); //apply relations.
        if(elem_actor.getRoot() != null){
            System.out.println("Translation finished. Launch Stage...");
            Stage s = (Stage) elem_actor.getRoot();
            s.showAndWait();
        }
        finish.countDown();
    }

    public void testScheme(CountDownLatch finish_flag, String grammar, String scheme, String input) throws Exception {
        Grammar G = new Grammar(
                SimpleJsonParserTest.JSON_PARSER.parse(PathStrings.GRAMMARS + grammar)
        );
        System.out.println("Source G: ");
        System.out.println(G);
        System.out.println("CommentLine: "+G.getCommentLine());

        String norm_input = input.substring(0, input.indexOf('.'));
        String norm_grammar = grammar.substring(0, grammar.indexOf('.'));
        String norm_scheme = scheme.substring(0, scheme.indexOf('.'));

        FALexerGenerator lg = new FALexerGenerator();
        CNFA nfa = lg.buildNFA(G);
        DFALexer lexer = new DFALexer(new DFA(nfa));

        // image file of lexer.
        //lexer.getImagefromStr(PathStrings.LEXERS,"I_G_"+norm_grammar);

        LRParser sa = new LRParser(G, lexer, LRAlgorithm.SLR);
        sa.setParserMode(ParserMode.DEBUG);

        LinkedTree<LanguageSymbol> stree = sa.parse(PathStrings.PARSER_INPUT + scheme);
        assert stree != null;

        // image file of syntax tree.
        //Graphviz.fromString(stree.toDot(norm_scheme)).render(Format.PNG).toFile(new File(PathStrings.PARSERS + norm_scheme));

        stree.visit(VisitorMode.PRE, new ReverseChildren());
        stree.visit(VisitorMode.PRE, new TranslationsAttacher(G, stree.getCount()));
        ExecuteTranslationNode act_executor = new ExecuteTranslationNode();
        DictionaryProcessorSDT act_translate = new DictionaryProcessorSDT();

        TypeProcessorSDT type_actor = new TypeProcessorSDT();
        act_executor.putActionParser("putAttr", type_actor);
        act_executor.putActionParser("addPrefix", type_actor);
        act_executor.putActionParser("removePrefix", type_actor);
        act_executor.putActionParser("createObject", type_actor);
        act_executor.putActionParser("showAttrs", type_actor);
        act_executor.putActionParser("translate", act_translate);
        System.out.println("Translate to schema :");
        stree.visit(VisitorMode.PRE, act_executor);// find and execute.

        System.out.println("Schema processed.");
        System.out.println(type_actor.getTypes().toString());
        System.out.println(type_actor.getAliases().toString());
        LinkedTree<LanguageSymbol> otree = sa.parse(PathStrings.PARSER_INPUT + input);
        assert otree != null;

        // image file of syntax tree.
        //Graphviz.fromString(otree.toDot(norm_input)).render(Format.PNG).toFile(new File(PathStrings.PARSERS + norm_input));

        otree.visit(VisitorMode.PRE, new ReverseChildren());
        otree.visit(VisitorMode.PRE, new TranslationsAttacher(G, otree.getCount()));

        ExecuteTranslationNode exec2 = new ExecuteTranslationNode();
        JavaFXExtraXMLProcessorSDT elem_actor = new JavaFXExtraXMLProcessorSDT(type_actor);
        exec2.putActionParser("putAttr", elem_actor);
        exec2.putActionParser("createObject", elem_actor);
        exec2.putActionParser("removePrefix", elem_actor);
        exec2.putActionParser("translate", act_translate);

        System.out.println("Translate document according to scheme");
        otree.visit(VisitorMode.PRE, exec2);

        elem_actor.tryApplySizeRelations(); //apply relations.
        if(elem_actor.getRoot() != null){
            System.out.println("Translation finished. Launch Stage...");
            Stage s = (Stage) elem_actor.getRoot();
            s.showAndWait();
        }
        finish_flag.countDown();
    }


    @Test
    public void getGenericClass(){

        Class<?> cls = null;
        try{
            cls = Class.forName("javafx.scene.layout.Background");
            Class<?> cl2 = ((List<String>) new ArrayList<String>()).getClass();

            ArrayList<String> ar111 = new ArrayList<>();
            System.out.println(cls.getName());
            System.out.println(cls.getSimpleName());
            System.out.println(cl2.getName());
            System.out.println(cl2.getSimpleName());

            for(TypeVariable t : cls.getTypeParameters()){
                System.out.println(t.getName());
                System.out.println(t.getTypeName());
            }

            Class<?> type2 = Class.forName("[Ljavafx.scene.layout.BackgroundFill;");
            System.out.println(type2.isArray());
            assert type2 != null;
            Constructor<?> ctr = cls.getConstructor(type2);
            System.out.println(ctr.getTypeParameters().length);
            assert ctr != null;
        }
        catch (ClassNotFoundException e){}
        catch (NoSuchMethodException e) { assert 1 == 0;}
        assert cls != null;
    }

}