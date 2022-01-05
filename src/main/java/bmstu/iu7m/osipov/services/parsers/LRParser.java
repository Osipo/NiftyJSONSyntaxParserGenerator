package bmstu.iu7m.osipov.services.parsers;

import bmstu.iu7m.osipov.structures.graphs.Pair;
import bmstu.iu7m.osipov.structures.lists.LinkedStack;
import bmstu.iu7m.osipov.services.grammars.*;
import bmstu.iu7m.osipov.services.lexers.*;
import bmstu.iu7m.osipov.services.parsers.generators.*;
import bmstu.iu7m.osipov.structures.trees.LinkedTree;
import bmstu.iu7m.osipov.structures.trees.LinkedNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

//Implements all LR parsers.
//Contains two LR parser generators.
//SLR parser or LR(0) parser in (SLRParserGenerator class)
//CLR parser or LR(1) parser in (CLRParserGenerator class)
public class LRParser extends Parser {

    private LR_0_Automaton table;

    public LRParser(Grammar G, ILexer lexer){
        this(G, lexer, LRAlgorithm.CLR);
    }

    public LRParser(Grammar G, ILexer lexer, LRAlgorithm alg) {
        super(G,lexer);
        try {
            if (alg == LRAlgorithm.SLR) {
                this.table = SLRParserGenerator.buildLRAutomaton(G);//LR(0) or SLR(1).
            } else {
                this.table = CLRParserGenerator.buildLRAutomaton(G);//LR(1)
            }
            System.out.println(table);
        }
        catch (Exception e){
            System.out.println("Cannot build");
            System.out.println(e.getMessage());
        }
    }

    @Override
    public String toString(){
        return table.toString();
    }

    @Override
    public File getImage() throws IOException {
        return table.getImageFromDot();
    }


    @Override
    public LinkedTree<LanguageSymbol> parse(File file){
        if(table == null || file == null)
            return null;
        int l,col = 0;
        try (FileInputStream f  = new FileInputStream(file.getAbsolutePath())){
            LinkedStack<LinkedNode<LanguageSymbol>> S = new LinkedStack<>();//symbols.
            LinkedStack<Integer> states = new LinkedStack<>();//states.
            isParsed = true;

            //READ first token
            Token tok = lexer.recognize(f);

            //Skip comments (NULL tokens) and Unrecognized (INVALID lexems)
            while(tok == null || tok.getName().equals("Unrecognized")){
                if(tok != null) { //NOT COMMENT -> INVALID TOKEN.
                    System.out.println(tok);
                    isParsed = false;//TODO: HANDLE LEXICAL ERRORS
                }
                tok = lexer.recognize(f);
            }
            l = tok.getLine();
            col = tok.getColumn();
            String t = tok.getName();

            int nidx = 1;//counter of elements (tree nodes)
            int cstate = 0;
            states.push(0);//push start state 0 to the STATES_STACK.
            String command = null;
            while(true) {
                cstate = states.top();
                Pair<Integer, String> k = new Pair<>(cstate, t);
                if(mode == ParserMode.DEBUG)
                    System.out.println(states+" "+S+" >>"+t);
                //command =  s_state (shift to the new state)
                //      | r_header:size (reduce to production with header and size of items)
                //      | acc
                //      | err
                command = table.getActionTable().get(k);
                System.out.println(command);
                if(command == null && empty == null){//if where are no any command and no empty.
                    isParsed = false;
                    break;
                }
                if(command == null){ //if where is a empty symbol, try get command at [state, empty]
                    command = table.getActionTable().get(new Pair<Integer, String>(cstate, empty));
                    System.out.println("Command for ["+ cstate +", empty] = " + command);
                    if(command != null){ // apply shift empty symbol if presence. (Rule like A -> e)
                        String j = command.substring(command.indexOf('_') + 1);
                        states.push(Integer.parseInt(j));
                        LinkedNode<LanguageSymbol> nc = new LinkedNode<>();
                        nc.setValue(new Token(empty,null,'t', l, col));
                        nidx++;
                        nc.setIdx(nidx);
                        S.push(nc);
                        continue;
                    }
                    isParsed = false;
                    break;
                }
                if(command.equals("err")){
                    isParsed = false;
                    break;
                }
                int argIdx = command.indexOf('_');
                argIdx = argIdx == -1 ? 1 : argIdx;//in case of ACC or ERR
                String act = command.substring(0, argIdx);
                if(act.charAt(0) == 's'){
                    String j = command.substring(argIdx + 1);
                    states.push(Integer.parseInt(j));
                    LinkedNode<LanguageSymbol> nc = new LinkedNode<>();
                    nc.setValue(tok);
                    nidx++;
                    nc.setIdx(nidx);
                    S.push(nc);
                    //get next token
                    tok = lexer.recognize(f);
                    while(tok == null || tok.getName().equals("Unrecognized")){
                        if(tok != null) {
                            isParsed = false;//TODO: MAY BE OPTIONAL
                            System.out.println(tok);
                        }
                        tok = lexer.recognize(f);
                    }
                    t = tok.getName();
                    l = tok.getLine();
                    col = tok.getColumn();
                }
                else if(act.charAt(0) == 'r'){
                    String args = command.substring(argIdx + 1); //args  of reduce command
                    String header = args.substring(0, args.indexOf(':'));
                    int sz = Integer.parseInt(args.substring(args.indexOf(':') + 1));
                    LinkedNode<LanguageSymbol> parent = new LinkedNode<>();
                    while(sz > 0){
                        LinkedNode<LanguageSymbol> c = S.top();
                        S.pop();
                        states.pop();
                        c.setParent(parent);
                        parent.getChildren().add(c);
                        sz--;
                    }
                    parent.setValue(new Token(header, header,'n', l, col));
                    nidx++;
                    parent.setIdx(nidx);
                    S.push(parent);
                    cstate = states.top();
                    states.push(table.getGotoTable().get(new Pair<Integer, String>(cstate, header)));
                }
                else if(act.charAt(0) == 'a'){
                    break;
                }
            }
            lexer.reset();
            if(isParsed){
                return new LinkedTree<LanguageSymbol>(S.top());
            }
            else
                return null;
        }
        catch (FileNotFoundException e){
            lexer.reset();
            System.out.println("File not found. Specify file to read");
            return null;
        } catch (IOException e) {
            lexer.reset();
            System.out.println("File is not available now.");
            return null;
        }
    }
}
