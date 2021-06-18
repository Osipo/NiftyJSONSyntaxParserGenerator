package bmstu.iu7m.osipov.services.lexers;

import bmstu.iu7m.osipov.structures.automats.*;
import bmstu.iu7m.osipov.structures.graphs.Edge;
import bmstu.iu7m.osipov.structures.graphs.Elem;
import bmstu.iu7m.osipov.structures.graphs.Vertex;
import bmstu.iu7m.osipov.structures.lists.LinkedStack;
import bmstu.iu7m.osipov.services.grammars.Grammar;

import java.util.*;

public class FALexerGenerator {


    public CNFA buildNFA(Grammar G){
        Map<String, List<String>> rules = G.getLexicalRules();
        Set<String> terms = G.getTerminals();

        HashSet<Character> alpha = new HashSet<>();
        RegexRPNParser parser = new RegexRPNParser();
        Vertex vs = new Vertex("1");
        vs.setStart(true);
        int nfa_i = 2;
        Elem<Integer> idC = new Elem<>(1);
        List<Vertex> Fs = new ArrayList<>();
        for(String id : terms){
            List<String> patterns = rules.get(id);
            StringBuilder sbp = new StringBuilder();
            for(String pat : patterns) {//JUST CONCAT ALL PATTERNS INTO ONE.
                sbp.append(pat);
            }
            System.out.println(id+": "+sbp.toString());
            if(!id.equals(G.getEmpty())){
                /* BEGIN PATTERN PROCESSING */
                String pattern = sbp.toString();
                LinkedStack<Character> rpn = new LinkedStack<>();
                if(pattern.length() == 1) {
                    char c = pattern.charAt(0);
                    CNFA nfa = new CNFA();
                    Vertex v1 = new Vertex(nfa_i+"");
                    nfa_i++;
                    Vertex v2 = new Vertex(nfa_i+"");
                    nfa_i++;
                    Edge e = new Edge(v1, v2, c);
                    v1.setStart(true);
                    v2.setFinish(true);
                    nfa.setComboStart(v1);
                    nfa.setFinish(v2);
                    nfa.getFinish().setValue(id);
                    Fs.add(nfa.getFinish());
                    alpha.add(c);
                    idC.setV1(nfa_i);
                    Edge e2 = new Edge(vs,nfa.getStart(),(char)1);
                    continue;
                }
                else {
                    //replace empty and any with one-character symbols.
                    String p_i = new String(pattern.toCharArray());
                    if(G.getEmpty() != null)
                        p_i = p_i.replaceAll(G.getEmpty(),(char)1+"");//special symbol for empty-character.
                    p_i = p_i.replaceAll("(?<!@)_",(char)0+"");//another special symbol for any character.
                    p_i = p_i.replaceAll("@_","_");
                    parser.setTerminals(p_i.toCharArray());

                    //convert [A-Z] classes to (A|B|..|Z) expressions
                    p_i = AutomatonConstructor.addConcat2(p_i, parser);
                    parser.setTerminals(p_i.toCharArray());
                    rpn = parser.GetInput(p_i);//convert regex to postfix.
                }
                CNFA nfa = AutomatonConstructor.buildNFA(rpn, parser,idC);
                alpha.addAll(nfa.getAlpha());
                nfa.getFinish().setValue(id);
                Fs.addAll(nfa.getFinished());
                nfa_i = idC.getV1();
                nfa.getStart().setName(nfa_i+"");
                nfa_i++;
                idC.setV1(nfa_i);
                Edge e = new Edge(vs,nfa.getStart(),(char)1);
                /* END PATTERN PROCESSING */
            }
        }
        CNFA comboNFA = new CNFA();
        comboNFA.setComboStart(vs);
        comboNFA.setFinished(Fs);
        System.out.println(comboNFA.getFinished());
        alpha.remove((char)1);//empty-character is not part of the alpha.
        System.out.println("Alpha: "+alpha);
        comboNFA.setAlpha(alpha);//alpha will include zero-character code (char)0 for any-character symbol.
        System.out.println("States of NFA: "+comboNFA.getCountOfStates());
        return comboNFA;
    }
}
