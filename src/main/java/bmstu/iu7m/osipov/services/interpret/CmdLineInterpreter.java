package bmstu.iu7m.osipov.services.interpret;

import bmstu.iu7m.osipov.services.grammars.AstSymbol;
import bmstu.iu7m.osipov.services.grammars.Grammar;
import bmstu.iu7m.osipov.services.lexers.DFALexer;
import bmstu.iu7m.osipov.services.lexers.FALexerGenerator;
import bmstu.iu7m.osipov.services.parsers.LRAlgorithm;
import bmstu.iu7m.osipov.services.parsers.LRAstTranslator;
import bmstu.iu7m.osipov.services.parsers.ParserMode;
import bmstu.iu7m.osipov.services.parsers.json.SimpleJsonParser2;
import bmstu.iu7m.osipov.structures.automats.DFA;
import bmstu.iu7m.osipov.structures.trees.PositionalTree;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Scanner;

public class CmdLineInterpreter extends BottomUpInterpreter implements Interpreter {

    private LRAstTranslator parser;

    public CmdLineInterpreter(LRAstTranslator parser){
        super();
        this.parser = parser;
    }

    public static void main(String[] args){
        SimpleJsonParser2 jsonP = new SimpleJsonParser2(1024);

        System.out.println("args: " + Arrays.toString(args));
        if(args.length != 1 && args.length != 2){
            System.out.println("Usage: <programName> [fileName]");
        }
        else if(args.length == 1){
            String gr = InterPathsConfiguration.GRAMMARS + "G_Ast_7_cmd.json";
            Grammar G = new Grammar(jsonP.parse(gr));
            FALexerGenerator lg = new FALexerGenerator();
            DFALexer lexer = new DFALexer(new DFA(lg.buildNFA(G))); //NFA -> DFA -> min_DFA_lexer.
            LRAstTranslator parser = new LRAstTranslator(G, lexer, LRAlgorithm.SLR);
            parser.setParserMode(ParserMode.HIDE);
            CmdLineInterpreter I = new CmdLineInterpreter(parser);
            String str = null;
            Scanner inp = new Scanner(System.in);
            while(true){
                str = inp.nextLine();
                if(str.equals("q") || str.equals("q\n"))
                    break;
                I.eval(str);
            }

        }
        else {
            System.out.println("required fileName");
            /*
            String gr = InterPathsConfiguration.GRAMMARS + "G_Ast_7.json";
            Grammar G = new Grammar(jsonP.parse(gr));
            FALexerGenerator lg = new FALexerGenerator();
            DFALexer lexer = new DFALexer(new DFA(lg.buildNFA(G))); //NFA -> DFA -> min_DFA_lexer.
            LRAstTranslator parser = new LRAstTranslator(G, lexer, LRAlgorithm.SLR);
            parser.setParserMode(ParserMode.HIDE);
            BottomUpInterpreter I = new BottomUpInterpreter();
            ModuleProcessor M = new ModuleProcessor(parser, I, "", "");
             */
        }
    }


    public void eval(String str) {
        try (InputStream src = IOUtils.toInputStream(IOUtils.toString(new StringReader(str)), Charsets.UTF_8)) {
            PositionalTree<AstSymbol> exp_tree = parser.translate(src);
            this.interpret(exp_tree);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
