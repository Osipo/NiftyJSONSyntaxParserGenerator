package bmstu.iu7m.osipov.services.lexers;


import bmstu.iu7m.osipov.exceptions.grammar.InvalidRegexSyntaxException;
import bmstu.iu7m.osipov.structures.automats.*;
import bmstu.iu7m.osipov.structures.graphs.Vertex;
import bmstu.iu7m.osipov.structures.graphs.Elem;
import bmstu.iu7m.osipov.structures.graphs.Edge;
import bmstu.iu7m.osipov.structures.lists.LinkedStack;

import java.util.*;
import java.util.stream.Collectors;

public class AutomatonConstructor {

    //Algorithm: Mac Naughton-Yamada-Tompson (Мак-Нотона, Ямады, Томпсона)
    //DO NOT USE THIS METHOD FOR BUILDING A COMPLEMENT OF NFA. (Complement operation i.e. NOT(L)).
    public static NFA buildNFA(LinkedStack<Character> expr, RegexRPNParser parser){
        LinkedStack<NFA> result = new LinkedStack<>();
        HashSet<Character> alpha = new HashSet<>();
        for(Character tok : expr){
            if(parser.isUnaryOp(tok)){
                NFA g = result.top();
                result.pop();
                for(Vertex v: g.getNodes()){
                    v.setName("");
                    v.setFinish(false);
                }
                Vertex s = new Vertex();
                Vertex t = new Vertex();
                Edge iloop = new Edge(g.getFinish(), g.getStart(), (char) 1);
                g.getFinish().setFinish(false);
                g.getStart().setStart(false);
                Edge se = new Edge(s, g.getStart(), (char) 1);
                Edge fe = new Edge(g.getFinish(), t, (char) 1);
                if(tok == '*') {// '+' and '*' differ only with one edge.
                    Edge loop = new Edge(s, t, (char) 1);//for '*' add empty from start to finish
                }
                NFA R = new NFA();
                t.setFinish(true);
                R.setStart(s);
                result.push(R);
            }
            else if(parser.isOperator(tok)){
                NFA g2 = result.top();
                result.pop();
                NFA g1 = result.top();
                result.pop();
                for(Vertex v: g2.getNodes()){
                    v.setName("");v.setFinish(false);
                }
                for(Vertex v: g1.getNodes()){
                    v.setName("");v.setFinish(false);
                }
                if(tok == '^') {
                    Vertex inter = g1.getFinish();
                    inter.setFinish(false);
                    List<Edge> outE = g2.getStart().getEdges().stream().filter(edge -> edge.getSource().equals(g2.getStart())).collect(Collectors.toList());
                    for(Edge e: outE){
                        Edge ae = new Edge(inter,e.getTarget(),e.getTag());
                        g2.disconnectVertexByEdge(e,g2.getStart(),e.getTarget());
                    }
                    NFA FC = new NFA();
                    g2.getFinish().setFinish(true);
                    FC.setStart(g1.getStart());
                    result.push(FC);
                }
                else if(tok == '|'){
                    Vertex s = new Vertex();
                    Vertex t = new Vertex();
                    Vertex s1 = g1.getStart();
                    Vertex s2 = g2.getStart();
                    Vertex t1 = g1.getFinish();
                    Vertex t2 = g2.getFinish();
                    Edge s_s1 = new Edge(s,s1,(char)1);
                    Edge s_s2 = new Edge(s,s2,(char)1);
                    Edge t_t1 = new Edge(t1,t,(char)1);
                    Edge t_t2 = new Edge(t2,t,(char)1);
                    s.setStart(true);
                    s1.setStart(false);
                    s2.setStart(false);
                    t1.setFinish(false);
                    t2.setFinish(false);
                    t.setFinish(true);
                    NFA FU = new NFA();
                    FU.setStart(s);
                    result.push(FU);
                }
            }
            else{//token is not operator.
                Vertex v1 = new Vertex();
                Vertex v2 = new Vertex();
                v2.setFinish(true);
                Edge e = new Edge(v1,v2,tok);
                NFA F = new NFA();
                F.setStart(v1);
                alpha.add(tok);
                result.push(F);
            }
        }
        result.top().setAlpha(alpha);
        return result.top();
    }

    //Algorithm: Mac Naughton-Yamada-Tompson (Мак-Нотона, Ямады, Томпсона)
    public static CNFA buildNFA(LinkedStack<Character> expr, RegexRPNParser parser, Elem<Integer> el){
        LinkedStack<CNFA> result = new LinkedStack<>();
        HashSet<Character> alpha = new HashSet<>();
        int c = el.getV1();
        int pos = 0;
        Iterator<Character> itr = expr.iterator();
        while(itr.hasNext()){
            char tok = itr.next();
            if(parser.isUnaryOp(tok)){
                CNFA g = result.top();
                result.pop();
                if(tok == '!'){//COMPLEMENT
                    //Reverse F and F - N states.
                    for(Vertex v: g.getNodes()){
                        if(g.getFinished().contains(v))
                            v.setFinish(false);
                        else
                            v.setFinish(true);
                    }
                    for(Vertex v : g.getNodes()){
                        if(v.isFinish()) {//ADD Edge with label 'any character'
                            g.getFinished().add(v);
                            Edge e = new Edge(v,g.getStart(),(char)0);
                        }
                        else
                            g.getFinished().remove(v);
                    }
                    pos++;
                    if(g.getStart().isFinish()) {
                        Edge sl = new Edge(g.getStart(), g.getStart(), (char) 0);
                    }
                    result.push(g);
                    continue;
                }
                for(Vertex v: g.getNodes()){//nullify finish
                    v.setFinish(false);
                }
                Vertex s = new Vertex(c+"");
                c++;
                Vertex t = new Vertex(c+"");
                c++;
                Edge iloop = new Edge(g.getFinish(), g.getStart(), (char) 1);
                g.getFinish().setFinish(false);
                g.getStart().setStart(false);
                Edge se = new Edge(s, g.getStart(), (char) 1);
                Edge fe = new Edge(g.getFinish(), t, (char) 1);
                if(tok == '*') {// '+' and '*' differ only with one edge.
                    Edge loop = new Edge(s, t, (char) 1);//for '*' add empty from start to finish
                }
                CNFA R = new CNFA();
                t.setFinish(true);
                R.setStart(s);
                R.setFinish(t);
                result.push(R);
                pos++;
            }
            else if(parser.isOperator(tok)){
                CNFA g2 = result.top();
                result.pop();
                CNFA g1 = result.top();
                result.pop();
                for(Vertex v: g2.getNodes()){//nullify finish
                    v.setFinish(false);
                }
                for(Vertex v: g1.getNodes()){
                    v.setFinish(false);
                }
                if(tok == '^') {
                    Vertex inter = g1.getFinish();
                    List<Edge> outE = g2.getStart().getEdges().stream().filter(edge -> edge.getSource().equals(g2.getStart())).collect(Collectors.toList());
                    List<Edge> outEi = g2.getStart().getEdges().stream().filter(edge -> edge.getTarget().equals(g2.getStart())).collect(Collectors.toList());
                    inter.setFinish(false);
                    for(Edge e: outE){//union output edges.
                        Edge ae = new Edge(inter,e.getTarget(),e.getTag());
                        g2.disconnectVertexByEdge(e,g2.getStart(),e.getTarget());
                    }
                    for(Edge e : outEi){//union input edges.
                        Edge ea = new Edge(e.getSource(),inter,e.getTag());
                        g2.disconnectVertexByEdge(e,e.getSource(),g2.getStart());
                    }
                    CNFA FC = new CNFA();
                    g2.getFinish().setFinish(true);
                    FC.setStart(g1.getStart());
                    FC.setFinish(g2.getFinish());
                    result.push(FC);
                    pos++;
                }
                else if(tok == '|'){
                    Vertex s = new Vertex(c+"");
                    c++;
                    Vertex t = new Vertex(c+"");
                    c++;
                    Vertex s1 = g1.getStart();
                    Vertex s2 = g2.getStart();
                    Vertex t1 = g1.getFinish();
                    Vertex t2 = g2.getFinish();
                    Edge s_s1 = new Edge(s,s1,(char)1);
                    Edge s_s2 = new Edge(s,s2,(char)1);
                    Edge t_t1 = new Edge(t1,t,(char)1);
                    Edge t_t2 = new Edge(t2,t,(char)1);
                    s.setStart(true);
                    s1.setStart(false);
                    s2.setStart(false);
                    t1.setFinish(false);
                    t2.setFinish(false);
                    t.setFinish(true);
                    CNFA FU = new CNFA();
                    FU.setStart(s);
                    FU.setFinish(t);
                    result.push(FU);
                    pos++;
                }
            }
            else{//token is not operator.
                Vertex v1 = new Vertex(c+"");
                c++;
                Vertex v2 = new Vertex(c+"");
                c++;
                v2.setFinish(true);
                if(tok == '@') {
                    //System.out.println(tok);
                    tok = itr.next();//get operand after @ symbol.
                    //System.out.println(tok);
                }
                Edge e = new Edge(v1,v2,tok);
                CNFA F = new CNFA();
                F.setComboStart(v1);
                F.setFinish(v2);
                alpha.add(tok);
                result.push(F);
                pos++;
            }
        }
        result.top().setAlpha(alpha);
        el.setV1(c);
        return result.top();
    }

    public static String addConcat3(String s, RegexRPNParser parser){
        StringBuilder sb = new StringBuilder();

        int i = -1, l = s.length() - 1;
        char c;
        int paren = 0;
        int state = 0;
        while(i < l){
            i++;
            c = s.charAt(i);
            if(c == '@' && i < l) {
                i++;
                if(state == 1)
                    sb.append('^');
                sb.append('@').append(s.charAt(i));
                state = 1;
            }
            else if(c == '@'){
                if(state == 1)
                    sb.append('^');
                sb.append('@').append('@');
                i++;
                state = 1;
            }

            else if(c == '(') { // ( ) | + * ^
                paren++;
                if(state == 1)
                    sb.append('^');
                sb.append(c);
                state = 0;
            }
            else if(c == ')'){
                paren--;
                sb.append(')');
                if(paren == 0)
                    state = 1;
            }
            else if((state == 0 || state == 2) && !parser.isOperator(c)){
                sb.append(c);
                state = 1;
            }
            else if(state == 1 && !parser.isOperator(c))
                sb.append('^').append(c);
            else if(state == 1 && c != '|')
                sb.append(c);
            else if(state == 1) {
                sb.append(c);
                state = 2;
            }
        }
        return sb.toString();
    }

    public static String replaceBrackets(String s, RegexRPNParser parser){
        StringBuilder sb = new StringBuilder();
        int i = -1, l = s.length() - 1;
        char a = '\u0002', b = '\u0002', min = '\u0002', max = '\uffff';
        int state = 0;
        int brackets = 0;
        LinkedStack<Character> A = new LinkedStack<>();
        LinkedStack<Integer> S = new LinkedStack<>();
        while(i < l){
            i++;
            char c = s.charAt(i);
            if(c == '@' && i < l && state == 0) {
                sb.append('@').append(s.charAt(i + 1));
                i++;
                continue;
            }
            if(c == '@' && state == 0){
                sb.append('@').append('@');
                i++;
                continue;
            }

            if(c == '[' && (state == 0 || state == 1)) { //aaa [[[...  [[A]] => [(A)]
                state = 1;
                brackets++;
            }
            else if(state == 0)
                sb.append(c);
            else if(state == 1 && c == ']') { //aaa[]aaa
                brackets--;
                if(brackets == 0)
                    state = 0;
            }
            else if((state == 1 || state == 4) && c != ']') { // ( ) | ^ * +
                if(state == 1)
                    sb.append('(');
                if(state == 4)
                    sb.append('|');
                if(c == '@'){
                    sb.append('@');
                    i++;
                    c = s.charAt(i);
                }
                sb.append(c);
                A.push(c);
                state = 2;
            }
            else if(state == 2 && c == '-'){// aaa[A-
                state = 3;
                S.push(3);
            }
            else if((state == 2 || state == 3) && c == '['){// aaa[A[  or aaa[A-[
                brackets++;
                state = 4;
            }
            else if((state == 2 || state == 4) && c == ']'){ // aaa[A] or aaa[A[] or aaa[A[B] or aaa[A-[B] or aaa[A-[]
                brackets--;
                state = 4;
                if(S.top() != null) {
                    A.pop();
                    S.pop();
                    state = 3;
                }
                if(brackets == 0){
                    state = 0;
                    sb.append(')');
                }
            }
            else if(state == 2) { // aaa[AB
                sb.append('|');
                if(c == '@'){
                    sb.append(c);
                    i++;
                    c = s.charAt(i);
                }
                sb.append(c);
                A.pop();
                A.push(c);
            }
            else {// aaa[A-B
                boolean isVerbatim = false;
                if(c == '@'){ //skip verbatim
                    i++;
                    c = s.charAt(i);
                    isVerbatim = true;
                }
                b = c;
                a = A.top();
                A.pop();
                S.pop();
                char t = '\u0002';
                if(b < a) {
                    t = a;
                    a = b;
                    b = t;
                }
                while(a < b){
                    a++;
                    if(a == b && isVerbatim)
                        sb.append('|').append('@').append(a);
                    else
                        sb.append('|').append(a);
                }
                a = '\u0002'; b = '\uffff';
                state = 4;
            }
        }
        return sb.toString();
    }

    public static String addConcat2(String s, RegexRPNParser parser){
        StringBuilder result = new StringBuilder();
        char a = '\u0000', b = '\u0000', c, t; // 0 code == any symbol, 1 code == empty symbol.
        int i = 0, j = 0, state = 0;
        //states
        // 0 == the begining of line or expression
        // 1 == range detected '-'
        // 2 == range processed.
        // 3 == single symbol read (i.e. add '|')
        // 4 == end of range reached ']'

        while(i < s.length()){
            //RANGES BEGIN
            if(s.charAt(i) == '['){
                state = 0;
                //if previous symbol is not operator add concatenation.
                if(i > 0 && s.charAt(i - 1) != '(' && s.charAt(i - 1) != '|' && s.charAt(i - 1) != '^'){
                    result.append('^');
                }

                result.append('(');
                j = i + 1;// first symbol after '['.
                while(j < s.length()){
                    t = s.charAt(j);
                    //case [@$ where $ = end of string
                    if(t == '@' && j + 1 >= s.length() && state == 0){
                        result.append('@');
                        result.append('@');
                        j++; //that cause whole cycle to the end.
                        continue;
                    }
                    //case [@* where * = any sequence of chars
                    else if(t == '@' && state == 0){
                        result.append('@');
                        result.append(s.charAt(j + 1));
                        j += 2;
                        continue;
                    }

                    //case [*@$
                    else if(t == '@' && j + 1 >= s.length() && state > 1){
                        result.append('|');
                        result.append('@');
                        result.append('@');
                        j++; //that cause whole cycle to the end.
                        continue;
                    }
                    //case [*@*
                    else if(t == '@' && state > 1){
                        result.append('|');
                        result.append('@');
                        result.append(s.charAt(j + 1));
                        j += 2;
                        continue;
                    }

                    //case [a-* or [b-*
                    else if(t == '-' && s.charAt(j - 1) != '[' && state != 1 && state != 2){
                        a = s.charAt(j - 1);
                        state = 1;
                        j++;
                        continue;
                    }
                    //case [-*
                    else if(t == '-' && state != 1){
                        if(state != 0)
                            result.append('|').append((char)0);
                        else
                            result.append((char)0);
                        state = 1;
                        a = '\u0000';
                        j++;
                        continue;
                    }
                    //case [a-b* or [a--* or [a-]* or [a-@*  OR FROM PREV ELSE [-b* or [--* or [-]*
                    else if(state == 1){
                        b = t;
                        //case [a-@*
                        if(b == '@' && j + 1 < s.length()){
                            b = s.charAt(j + 1);
                            j++;
                        }
                        //case [a-]*
                        else if(b == ']'){
                            b = '\uffff';
                            state = 4;
                        }

                        if(a > b){
                            c = a;
                            a = b;
                            b = c;
                        }
                        while(a != b){
                            a++;
                            result.append('|').append(a);
                        }
                        j++;
                        if(state == 4)
                            break;
                        state = 2;
                        continue;
                    }
                    //case []* or [a]* or [a-b]* or [--]* or [a--]*
                    else if(t == ']') {
                        j++;
                        state = 4;
                        break;
                    }

                    //case [ab*
                    if(state == 2 || state == 3)
                        result.append('|');

                    //case [a*
                    result.append(t);
                    state = 3;
                    j++;
                }// end inner while at range '[' ']'
                result.append(')');
                i = j;
                continue;
            }
            else if(s.charAt(i) == '@' && i + 1 < s.length()){
                result.append('@');
                result.append(s.charAt(i + 1));
                i += 2;
                state = 1;
                continue;
            }
            else if(s.charAt(i) == '@'){
                result.append('@');
                result.append('@');
                i += 2;
                state = 1;
                continue;
            }
            // END OF RANGES

            if(state == 0) {
                result.append(s.charAt(i));
                if(s.charAt(i) != '(')
                     state = 1;
            }
            else if(s.charAt(i) == '*' || s.charAt(i) == '+' || s.charAt(i) == ')')
                result.append(s.charAt(i));
            else if(s.charAt(i) == '|'){
                result.append('|');
                state = 0;
            }
            else if(s.charAt(i) == '('){
                result.append('^');
                result.append('(');
                state = 0;
            }
            else {
                result.append('^');
                result.append(s.charAt(i));
            }
            i++;
        }
        return result.toString();
    }

    private static boolean isEmptyNFA(NFA nfa){
        return nfa.getCountOfStates() == 2 && nfa.getStart().getEdges().size() == 1 &&
                nfa.getStart().getEdges().get(0).getTag() == (char)1;
    }
}