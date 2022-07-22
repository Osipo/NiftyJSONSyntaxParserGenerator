package bmstu.iu7m.osipov.services.interpret;

import bmstu.iu7m.osipov.Main;
import bmstu.iu7m.osipov.services.grammars.AstSymbol;
import bmstu.iu7m.osipov.services.grammars.Grammar;
import bmstu.iu7m.osipov.services.interpret.optimizers.FunctionOptimizer;
import bmstu.iu7m.osipov.services.lexers.DFALexer;
import bmstu.iu7m.osipov.services.lexers.FALexerGenerator;
import bmstu.iu7m.osipov.services.parsers.LRAlgorithm;
import bmstu.iu7m.osipov.services.parsers.LRAstTranslator;
import bmstu.iu7m.osipov.services.parsers.ParserMode;
import bmstu.iu7m.osipov.services.parsers.json.SimpleJsonParser2;
import bmstu.iu7m.osipov.structures.automats.DFA;
import bmstu.iu7m.osipov.structures.trees.LinkedTree;
import bmstu.iu7m.osipov.structures.trees.PositionalTree;
import bmstu.iu7m.osipov.utils.PathStringUtils;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Scanner;

public class CmdLineInterpreter extends BottomUpInterpreter implements Interpreter {

    private LRAstTranslator parser;

    private boolean firstPassed = false;

    public CmdLineInterpreter(LRAstTranslator parser){
        super();
        this.parser = parser;
        this.firstPassed = false;
    }

    public static void main(String[] args){
        SimpleJsonParser2 jsonP = new SimpleJsonParser2(1024);

        System.out.println("args: " + Arrays.toString(args));
        if(args.length != 0 && args.length != 1){
            System.out.println("Usage: java -jar " + InterPathsConfiguration.PROJECT_NAME + " [fileName]");
        }
        else if(args.length == 0){
            System.out.println("Load interpreter...");
            Grammar G = new Grammar(jsonP.parseStream(CmdLineInterpreter.class.getClassLoader().getResourceAsStream("grammars/G_Ast_7_cmd.json")));
            //System.out.println(G);
            FALexerGenerator lg = new FALexerGenerator();
            DFALexer lexer = new DFALexer(new DFA(lg.buildNFA(G))); //NFA -> DFA -> min_DFA_lexer.
            LRAstTranslator parser = new LRAstTranslator(G, lexer, LRAlgorithm.SLR);
            parser.setParserMode(ParserMode.HIDE);
            CmdLineInterpreter I = new CmdLineInterpreter(parser);
            String str = null;
            Scanner inp = new Scanner(System.in);
            System.out.println("Interpreter initialized.");
            while(true){
                str = inp.nextLine();
                if(str.equals("q") || str.equals("q\n"))
                    break;
                str = "@\n" + str + "\n@";
                I.eval(str);
            }
        }
        else { //first argument passed to jar is fileName
            String fName = Main.CWD + (((args[0].charAt(0) == '\\' || args[0].charAt(0) == '/')) ? args[0] : Main.PATH_SEPARATOR + args[0]);
            fName = PathStringUtils.replaceSeparator(fName);
            String modDir = PathStringUtils.truncatePath(fName, 1);
            System.out.println("provided fileName: " + fName);
            System.out.println("execution module directory: " + modDir);

            Grammar G = new Grammar(jsonP.parseStream(CmdLineInterpreter.class.getClassLoader().getResourceAsStream("grammars/G_Ast_7.json")));
            //System.out.println(G);

            FALexerGenerator lg = new FALexerGenerator();
            DFALexer lexer = new DFALexer(new DFA(lg.buildNFA(G))); //NFA -> DFA -> min_DFA_lexer.
            LRAstTranslator parser = new LRAstTranslator(G, lexer, LRAlgorithm.SLR);
            parser.setParserMode(ParserMode.HIDE);

            BottomUpInterpreter I = new BottomUpInterpreter();
            FunctionOptimizer remove_tail_recursive_call = new FunctionOptimizer();
            ModuleProcessor M = new ModuleProcessor(parser, I, modDir, fName, remove_tail_recursive_call);

            System.out.println("Interpreter initialized.");
            LinkedTree<AstSymbol> ast = parser.translate(new File(fName));
            remove_tail_recursive_call.optimize(ast);
            I.interpret(ast);
        }
    }


    //for CLI.
    public void eval(String str) {
        try (InputStream src = IOUtils.toInputStream(IOUtils.toString(new StringReader(str)), Charsets.UTF_8)) {
            PositionalTree<AstSymbol> exp_tree = parser.translate(src);
            FunctionOptimizer remove_tail_recusrive_call = new FunctionOptimizer();
            remove_tail_recusrive_call.optimize(exp_tree);

            if(firstPassed)
                this.interpret_new(exp_tree);
            else
                this.interpret(exp_tree);

            this.firstPassed = true;

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
