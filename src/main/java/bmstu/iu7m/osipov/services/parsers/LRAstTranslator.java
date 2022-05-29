package bmstu.iu7m.osipov.services.parsers;

import bmstu.iu7m.osipov.services.grammars.*;
import bmstu.iu7m.osipov.services.grammars.directives.SyntaxDirectedTranslation;
import bmstu.iu7m.osipov.services.lexers.*;
import bmstu.iu7m.osipov.structures.graphs.Elem;
import bmstu.iu7m.osipov.structures.graphs.Pair;
import bmstu.iu7m.osipov.structures.lists.LinkedList;
import bmstu.iu7m.osipov.structures.lists.LinkedStack;
import bmstu.iu7m.osipov.structures.trees.LinkedNode;
import bmstu.iu7m.osipov.structures.trees.LinkedTree;
import bmstu.iu7m.osipov.utils.GrammarBuilderUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LRAstTranslator  extends LRParser {
    public LRAstTranslator(Grammar G, ILexer lexer) {
        super(G, lexer);
    }

    public LRAstTranslator(Grammar G, ILexer lexer, LRAlgorithm alg) {
        super(G, lexer, alg);
    }

    public LinkedTree<AstSymbol> translate(File file) {
        if (table == null || file == null)
            return null;
        int l, col = 0;
        try (FileInputStream f = new FileInputStream(file.getAbsolutePath())) {
            LinkedStack<LinkedNode<LanguageSymbol>> S = new LinkedStack<>();//symbols.
            LinkedList<LinkedNode<AstSymbol>> AST = new LinkedList<>(); //AST Nodes
            LinkedStack<Integer> states = new LinkedStack<>();//states.
            LinkedStack<Integer> scopes = new LinkedStack<>();//scopes.
            isParsed = true;

            //-------------READ FIRST TOKEN ----------------------//
            Token tok = lexer.recognize(f);

            //Skip comments (NULL tokens) and Unrecognized (INVALID lexems)
            while (tok == null || tok.getName().equals("Unrecognized")) {
                if (tok != null) { //NOT COMMENT -> INVALID TOKEN.
                    System.out.println(tok); //show all lexical errors.
                    isParsed = false;//TODO: HANDLE LEXICAL ERRORS
                }
                tok = lexer.recognize(f);
            }
            l = tok.getLine();
            col = tok.getColumn();
            String t = tok.getName();
            //-------- END READ FIRST TOKEN -----------------------//-

            int nidx = 1;//counter of elements (tree nodes)
            Elem<Integer> aidx = new Elem<>(0);//counter of ast nodes.
            int cstate = 0;
            states.push(0);//push start state 0 to the STATES_STACK.
            String command = null;

            boolean removeScope = false;

            while (true) {
                cstate = states.top();
                Pair<Integer, String> k = new Pair<>(cstate, t);
                showMessage(states + " " + S + " >>" + t);
                //command =  s_state (shift to the new state)
                //      | r_header:size (reduce to production with header and size of items)
                //      | acc
                //      | err
                command = table.getActionTable().get(k);
                //System.out.println(command);
                showMessage(command);
                if (command == null && empty == null) {//if where are no any command and no empty.
                    isParsed = false;
                    break;
                }
                if (command == null) { //if where is a empty symbol, try get command at [state, empty]
                    command = table.getActionTable().get(new Pair<Integer, String>(cstate, empty));
                    //System.out.println("Command for [" + cstate + ", empty] = " + command);
                    showMessage("Command for [" + cstate + ", empty] = " + command);
                    if (command != null) { // apply shift empty symbol if presence. (Rule like A -> e)
                        String j = command.substring(command.indexOf('_') + 1);
                        states.push(Integer.parseInt(j));
                        LinkedNode<LanguageSymbol> nc = new LinkedNode<>();
                        nc.setValue(new Token(empty, null, 't', l, col));
                        nidx++;
                        nc.setIdx(nidx);
                        S.push(nc); //nc - new child.
                        continue;
                    }
                    isParsed = false;
                    break;
                }
                if (command.equals("err")) {
                    isParsed = false;
                    break;
                }
                int argIdx = command.indexOf('_');
                argIdx = argIdx == -1 ? 1 : argIdx;//in case of ACC or ERR
                String act = command.substring(0, argIdx);
                if (act.charAt(0) == 's') {

                    //add new child (nc) (perform Shift)
                    String j = command.substring(argIdx + 1);
                    states.push(Integer.parseInt(j));
                    LinkedNode<LanguageSymbol> nc = new LinkedNode<>();
                    nc.setValue(tok);
                    nidx++;
                    nc.setIdx(nidx);
                    S.push(nc);

                    int scope_i = 0; //iteration through scopes
                    int scope_s = -1; //flag that scope start found
                    int scope_mi = 0; //max length of prefix (start of the scope)
                    //Check scopes.
                    for(Scope s : this.G.getMeta().getScopes()){
                        int scope_prefix_len = 0;
                        int scope_prefix_i = s.getStart().size() - 1;
                        if(s.getStart().size() <= S.size()) {
                            for (String scope_prefix : s.getStart()) {
                                if (S.topFrom(scope_prefix_i).getValue().getName().equals(scope_prefix))
                                    scope_prefix_len++;
                                scope_prefix_i--;
                            }
                            if (scope_prefix_len == s.getStart().size() && scope_mi <= scope_prefix_len) {
                                scope_mi = scope_prefix_len;
                                scopes.push(AST.size());
                                scope_s = scope_i;
                            }
                        }
                        if(scope_s != -1)
                            continue;

                        if(s.getEnd() == null
                                && S.topFrom(0) != null && S.topFrom(0).getValue() != null
                                && S.topFrom(0).getValue().getName().equals(s.getBody())
                        ){
                            removeScope = true;
                            break;
                        }
                        //body was end [{ BODY }] >>}, scope: { BODY } => flush current scope.]
                        else if(s.getEnd() != null && s.getEnd().equals(tok.getName())
                                && S.topFrom(1) != null && S.topFrom(1).getValue() != null
                                && S.topFrom(1).getValue().getName().equals(s.getBody())
                        ){
                            removeScope = true; //signal that next reduce removeScope.
                            break;
                        }
                        scope_i++;
                    }
                    //END Scan Scopes.

                    //get next token
                    tok = lexer.recognize(f);
                    while (tok == null || tok.getName().equals("Unrecognized")) {
                        if (tok != null) {
                            isParsed = false;//TODO: MAY BE OPTIONAL
                            System.out.println(tok);
                        }
                        tok = lexer.recognize(f);
                    }
                    t = tok.getName();
                    l = tok.getLine();
                    col = tok.getColumn();
                } else if (act.charAt(0) == 'r') {

                    //CHECK Scopes
                    for(Scope s : this.G.getMeta().getScopes()) {
                        if (s.getEnd() == null
                                && S.topFrom(0) != null && S.topFrom(0).getValue() != null
                                && S.topFrom(0).getValue().getName().equals(s.getBody())
                        ) {
                            removeScope = true;
                            break;
                        }
                        //body was end [{ BODY }], scope: { BODY } => flush current scope.
                        else if(s.getEnd() != null && S.top() != null && s.getEnd().equals(S.top().getValue().getName())
                                && S.topFrom(1) != null && S.topFrom(1).getValue() != null
                                && S.topFrom(1).getValue().getName().equals(s.getBody())
                        ){
                            removeScope = true; //signal that next reduce removeScope.
                            break;
                        }
                    }
                    //END CHECK Scopes

                    String args = command.substring(argIdx + 1); //args  of reduce command
                    String header = args.substring(0, args.indexOf(':'));
                    int sz = Integer.parseInt(args.substring(args.indexOf(':') + 1));
                    int sz2 = sz;
                    LinkedNode<LanguageSymbol> parent = new LinkedNode<>();

                    //Memoize sequence into g.
                    GrammarString g = new GrammarString();
                    while (sz > 0) {
                        LinkedNode<LanguageSymbol> c = S.topFrom(sz - 1); //preserve source order
                        states.pop();

                        if(c.getValue() instanceof Token)
                            g.getSymbols().add( (Token) c.getValue());

                        c.setParent(parent);
                        parent.getChildren().add(c);
                        sz--;
                    }
                    while(sz2 > 0){ //remove nodes from S.
                        S.pop();
                        sz2--;
                    }

                    //TODO: Make ast nodes.
                    List<SyntaxDirectedTranslation> astdefs = extractEachTranslation(extractSDT(g, header));
                    //System.out.println(astdefs);
                    if(astdefs != null) {
                        for(SyntaxDirectedTranslation astdef : astdefs) {
                            if (astdef != null && astdef.getActName().equals("astNode")) {
                                String arg1 = astdef.getArguments().getOrDefault("type", null);
                                String arg2 = astdef.getArguments().getOrDefault("value", null);
                                String arg3 = astdef.getArguments().getOrDefault("children", null);
                                String arg4 = astdef.getArguments().getOrDefault("child", null);
                                int astAllPos = scopes.top() != null ? scopes.top() : 0;
                                //System.out.println("scopes: "+scopes);
                                showMessage("scopes: " + scopes);
                                createAstNode(arg1, arg2, arg3, arg4, parent, AST, aidx, astAllPos);
                            }
                        }
                    }
                    if(removeScope){
                        scopes.pop();
                        removeScope = false;
                    }

                    parent.setValue(new Token(header, header, 'n', l, col));
                    nidx++;
                    parent.setIdx(nidx);
                    S.push(parent);
                    cstate = states.top();
                    states.push(table.getGotoTable().get(new Pair<Integer, String>(cstate, header)));
                } else if (act.charAt(0) == 'a') {
                    break;
                }
            } //end while loop.
            lexer.reset();
            if (isParsed) {
                return new LinkedTree<AstSymbol>(AST.tail());
            } else {
                System.err.println(tok.getName() + " " + tok.getVal() + " at (" + tok.getLine() + ", " + tok.getColumn() + ")");
                return null;
            }
        } catch (FileNotFoundException e) {
            lexer.reset();
            System.out.println("File not found. Specify file to read");
            return null;
        } catch (IOException e) {
            lexer.reset();
            System.out.println("File is not available now.");
            return null;
        } //end try.
    }

    private GrammarSDTString extractSDT(GrammarString p, String header){
        Set<GrammarString> rule = G.getProductions().get(header);
        for(GrammarString alt : rule){
            if(alt.equals(p) && alt instanceof GrammarSDTString) {
                return (GrammarSDTString)alt;
            }
        }
        return null;
    }

    private List<SyntaxDirectedTranslation> extractEachTranslation(GrammarSDTString ap){
        if(ap == null || ap.getActions() == null || ap.getActions().size() == 0)
            return null;
        return ap.getActions().stream().filter(x -> x instanceof SyntaxDirectedTranslation)
                .map(x -> (SyntaxDirectedTranslation) x).collect(Collectors.toList());
    }

    private SyntaxDirectedTranslation extractTranslation(GrammarSDTString ap){
        if(ap == null || ap.getActions() == null || ap.getActions().size() == 0)
            return null;
        if(ap.getActions().get(ap.getActions().size() - 1) instanceof SyntaxDirectedTranslation)
            return (SyntaxDirectedTranslation) ap.getActions().get(ap.getActions().size() - 1);
        else
            return null;
    }

    /* Choose right structure for collection of astNodes (Deck)*/
    private void createAstNode(String type, String value, String children, String child,
                               LinkedNode<LanguageSymbol> parent,
                               LinkedList<LinkedNode<AstSymbol>> AST,
                               Elem<Integer> idx,
                               int astAllPos
    )
    {
        int pos = 0;
        int valp = -1;
        if(value.charAt(0) == '$') {
            value = GrammarBuilderUtils.replaceSymRefsAtArgument(parent, value);
        }
        LinkedNode<AstSymbol> ast = new LinkedNode<>();
        ast.setValue(new AstNode(type, value));

        ast.setIdx(idx.getV1());
        idx.setV1(idx.getV1() + 1);

        showMessage("AST: " + AST + "\n" + "children: " + children);

        LinkedNode<AstSymbol> c_i = null;
        int l = AST.size();
        int dl  = l - (l - astAllPos) + 1; //[1,2,3,4, 5..] => dl = 5 - (5 - 4) + 1 => 5.

        //child is available only when children are not specified (null)!
        //if children are specified > child is ignored!
        if(children == null && child != null){
            int child_num = Integer.parseInt(child.substring(1)); //skip '$'
            AST.get(AST.size()).getChildren().add(child_num, ast); //children property is ArrayList.
            ast.setParent(AST.get(AST.size())); //attach to parent.
            return;
        }
        else if(children == null){} // just skip else-branches.
        else if(children.equals("all")){ //var children is not null
            while (l != astAllPos) {
                c_i = AST.get(dl);
                ast.getChildren().add(c_i);
                c_i.setParent(ast);
                AST.remove(dl - 1); //for remove index range starts from 0 [0..4] for size = 5.
                l--;
            }
        }
        else {
            //skip '$' at '$num' string.
            int ptr = l - Integer.parseInt(children.substring(1)); // (l - pos)
            int ptr_i = ptr;
            while(ptr_i <= l){
                ptr_i++;
                c_i = AST.get(ptr);
                AST.remove(ptr - 1); //l - pos - 1

                showMessage("after delete: "+AST);

                if(c_i == null)
                    continue;
                ast.getChildren().add(c_i);
                c_i.setParent(ast);
            }
        }

        //finally add node.
        showMessage("AST size before: "+AST.size());
        AST.append(ast);
        showMessage("AST size after: "+AST.size());
    }
}