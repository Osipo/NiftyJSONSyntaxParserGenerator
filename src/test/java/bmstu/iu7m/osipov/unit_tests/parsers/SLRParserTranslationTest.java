package bmstu.iu7m.osipov.unit_tests.parsers;

import bmstu.iu7m.osipov.configurations.PathStrings;
import bmstu.iu7m.osipov.services.grammars.Grammar;
import bmstu.iu7m.osipov.services.grammars.directives.AttributeProcessorSDT;
import bmstu.iu7m.osipov.services.grammars.directives.ElementProcessorSDT;
import bmstu.iu7m.osipov.services.grammars.directives.JavaFXExtraXMLProcessorSDT;
import bmstu.iu7m.osipov.services.grammars.directives.TypeProcessorSDT;
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
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import javax.swing.*;
import java.io.File;
import java.lang.reflect.*;
import java.util.Arrays;
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
        //test("javafx_xml.json","stage_example1.xml");
        //test("javafx_xml.json", "fx_constructors_schema_nested.xml");
        //test("java_meta_objects.json", "java_objects_constructors.txt");

        /* execute at JavaFX-Thread */
        final CountDownLatch finish = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                testScheme(finish,"javafx_xml.json", "fx_constructors_schema_nested.xml", "stage_example1.xml");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        finish.await();
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

        //Graphviz.fromString(t.toDot(norm_input)).render(Format.PNG).toFile(new File(PathStrings.PARSERS + norm_input + "Reversed"));

        System.out.println("tree nodes before "+t.getCount());
        t.visit(VisitorMode.PRE, new TranslationsAttacher(G, t.getCount()));
        System.out.println("tree nodes after attaching acts "+t.getCount());

        //Graphviz.fromString(t.toDot(norm_input)).render(Format.PNG).toFile(new File(PathStrings.PARSERS + norm_input + "Attached"));


        ExecuteTranslationNode act_executor = new ExecuteTranslationNode();

        if(input.equals("stage_example1.xml")) {
            AttributeProcessorSDT actor = new AttributeProcessorSDT();
            TypeProcessorSDT type_actor = new TypeProcessorSDT();
            ElementProcessorSDT elem_actor = new ElementProcessorSDT(actor, type_actor);

            act_executor.putActionParser("createObject", elem_actor);
            act_executor.putActionParser("putAttr", elem_actor);
            act_executor.putActionParser("putAttr", actor);
            act_executor.putActionParser("showAttrs", actor);
            act_executor.putActionParser("addPrefix", actor);
            act_executor.putActionParser("removePrefix", actor);
        }
        else if(input.equals("fx_constructors_schema_nested.xml")){
            TypeProcessorSDT type_actor = new TypeProcessorSDT();
            act_executor.putActionParser("putAttr", type_actor);
            act_executor.putActionParser("addPrefix", type_actor);
            act_executor.putActionParser("removePrefix", type_actor);
            act_executor.putActionParser("createObject", type_actor);
            act_executor.putActionParser("showAttrs", type_actor);
        }

        System.out.println("Perform translation... :");
        t.visit(VisitorMode.PRE, act_executor);// find and execute.
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

        lexer.getImagefromStr(PathStrings.LEXERS,"I_G_"+norm_grammar);

        LRParser sa = new LRParser(G, lexer, LRAlgorithm.SLR);
        sa.setParserMode(ParserMode.DEBUG);

        LinkedTree<LanguageSymbol> stree = sa.parse(PathStrings.PARSER_INPUT + scheme);
        assert stree != null;
        Graphviz.fromString(stree.toDot(norm_scheme)).render(Format.PNG).toFile(new File(PathStrings.PARSERS + norm_scheme));

        stree.visit(VisitorMode.PRE, new ReverseChildren());
        stree.visit(VisitorMode.PRE, new TranslationsAttacher(G, stree.getCount()));
        ExecuteTranslationNode act_executor = new ExecuteTranslationNode();
        TypeProcessorSDT type_actor = new TypeProcessorSDT();
        act_executor.putActionParser("putAttr", type_actor);
        act_executor.putActionParser("addPrefix", type_actor);
        act_executor.putActionParser("removePrefix", type_actor);
        act_executor.putActionParser("createObject", type_actor);
        act_executor.putActionParser("showAttrs", type_actor);
        System.out.println("Translate to schema :");
        stree.visit(VisitorMode.PRE, act_executor);// find and execute.

        System.out.println("Schema processed.");
        LinkedTree<LanguageSymbol> otree = sa.parse(PathStrings.PARSER_INPUT + input);
        assert otree != null;
        Graphviz.fromString(otree.toDot(norm_input)).render(Format.PNG).toFile(new File(PathStrings.PARSERS + norm_input));
        otree.visit(VisitorMode.PRE, new ReverseChildren());
        otree.visit(VisitorMode.PRE, new TranslationsAttacher(G, otree.getCount()));

        ExecuteTranslationNode exec2 = new ExecuteTranslationNode();

//        AttributeProcessorSDT actor = new AttributeProcessorSDT();
//        ElementProcessorSDT elem_actor = new ElementProcessorSDT(actor, type_actor);
//
//        exec2.putActionParser("createObject", elem_actor);
//        exec2.putActionParser("putAttr", elem_actor);
//        exec2.putActionParser("putAttr", actor);
//        exec2.putActionParser("showAttrs", actor);
//        exec2.putActionParser("addPrefix", actor);
//        exec2.putActionParser("removePrefix", elem_actor);
//        exec2.putActionParser("removePrefix", actor);



        JavaFXExtraXMLProcessorSDT elem_actor = new JavaFXExtraXMLProcessorSDT(type_actor);
        exec2.putActionParser("putAttr", elem_actor);
        exec2.putActionParser("createObject", elem_actor);
        exec2.putActionParser("removePrefix", elem_actor);

        System.out.println("Translate document according to scheme");
        otree.visit(VisitorMode.PRE, exec2);
        if(elem_actor.getRoot() != null){
            Stage s = (Stage) elem_actor.getRoot();
            s.showAndWait();
        }
        finish_flag.countDown();
    }


    @Test
    public void check_type()  {
       /* blank code*/
        String s = "sss";
        System.out.println(s.toString());
        Constructor<?> stagectr = null;
        try{
            stagectr = Class.forName("javafx.stage.Stage").getDeclaredConstructor(null);

            Method methods = Arrays.stream(Stage.class.getDeclaredMethods()).filter(x -> x.getName().equalsIgnoreCase("initstyle")).findFirst().get();
            if(methods == null)
                System.out.println("searching 'initstyle' failed.");
        } catch (ClassNotFoundException | SecurityException | NoSuchMethodException e){System.out.println(e);}

        Constructor<?> finalStagectr = stagectr;

        /* JavaFX Thread */
        Platform.runLater(() -> {
            try {
                finalStagectr.newInstance(new Object[0]);
                System.out.println("Stage object created");
            } catch (InvocationTargetException e){
                System.out.println(e.getCause().getCause());
            } catch (InstantiationException | IllegalArgumentException | IllegalAccessException e){ }
        });
    }

    //Make sure that 'switch' returns value before 'break'
    //At break it returns -1.
    private int switcher(int x){
        switch(x){
            case 1:{
                return x * 2;
            }
            case 10:{
                break;
            }
        }
        return -1;
    }

    @Test
    public void switch_return_instead_of_break(){
        int x = 1, y = 1;
        y = switcher(x);
        assert y == x * 2;
        x = 10;
        y = switcher(x);
        assert y == -1;
    }
}