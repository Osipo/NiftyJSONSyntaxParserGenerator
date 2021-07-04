package bmstu.iu7m.osipov.services.parsers.generators;

import bmstu.iu7m.osipov.structures.graphs.Pair;
import bmstu.iu7m.osipov.structures.lists.LinkedStack;
import bmstu.iu7m.osipov.structures.observable.*;
import bmstu.iu7m.osipov.services.grammars.*;

import java.util.*;
import java.util.stream.Collectors;

public class LLParserGenerator {
    public static Map<Pair<String,String>, GrammarString> getTable(Grammar G){
        HashMap<Pair<String,String>, GrammarString> table = new HashMap<>();

        Map<String,Set<String>> firstTable = firstTable(G);
        Map<String,Set<String>> followTable = followTable(G,firstTable);
        Set<String> ps = G.getProductions().keySet();
        boolean hasErr = false;
        String empty = G.getEmpty();
        for(String p : ps){
            Set<GrammarString> bodies = G.getProductions().get(p);
            for(GrammarString b: bodies){
                Set<String> first = first(b,firstTable,empty);
                if(first.contains(empty)) {
                    Set<String> follow = followTable.get(p);
                    for (String f : follow) {
                        Pair<String, String> rec = new Pair<>();
                        rec.setV1(p);
                        rec.setV2(f);
                        if(table.get(rec) != null) {
                            hasErr = true;
                            System.out.println("Error");
                            System.out.println("\tCell: ["+rec.getV1()+", "+rec.getV2()+"]");
                            System.out.println("\tAmbiguous: "+table.get(rec)+" / "+b);
                            System.out.println("\tConflict detected ! Grammar is not LL(1)!");
                        }
                        table.put(rec,b);// new GrammarString(new ArrayList<>(b.getSymbols())));
                    }
                }
                else
                    for(String a : first){
                        Pair<String, String> rec = new Pair<>();
                        rec.setV1(p);
                        rec.setV2(a);
                        if(table.get(rec) != null){
                            hasErr = true;
                            System.out.println("Error");
                            System.out.println("\tCell: ["+rec.getV1()+", "+rec.getV2()+"]");
                            System.out.println("\tAmbiguous: "+table.get(rec)+" / "+b);
                            System.out.println("\tConflict detected ! Grammar is not LL(1)!");
                        }
                        table.put(rec, b);//new GrammarString(new ArrayList<>(b.getSymbols())));
                    }
            }
        }
        return table;
    }

    public static Map<String, Set<String>> firstTable2(Grammar G){
        Set<String> N_e = G.getN_e(); // Non-terminals with rules N -> e.
        G = Grammar.deleteLeftRecursion(G);
        Set<String> T = G.getTerminals();
        Set<String> NT = G.getNonTerminals();
        HashMap<String, Set<String>> res = new HashMap<String,Set<String>>();
        LinkedStack<String> S = new LinkedStack<>();

        System.out.println("Non left-recursing Grammar: ");
        System.out.println(G);
        /* For each terminal t add to Table FIRST(t) = { t }; */
        for(String t : T){
            HashSet<String> f = new HashSet<>();
            f.add(t);
            res.put(t, f);
        }
        Set<String> nonIndexed = NT.stream()
                .filter(x -> N_e.contains( x.substring(0, x.indexOf('_')) ) )
                .collect(Collectors.toSet());

        for(String n : nonIndexed){
            HashSet<String> ef = new HashSet<>();// FIRST = { e } (e == empty)
            ef.add(G.getEmpty());
            res.put(n, ef);
        }
        for(String n : NT){
            S.push(n);
        }
        S.push(G.getStart());

        Map<GrammarString, String> cache = new HashMap<>();

        while(!S.isEmpty()){
            String N = S.top();
            System.out.println(S);
            Set<String> first_n = res.computeIfAbsent(N, k -> new HashSet<>());
            res.put(N, first_n);
            S.pop();
            Set<GrammarString> prods = G.getProductions().get(N);
            //Scan each alternative symbol by symbol
            M1: for(GrammarString body : prods){
                // IF previously computed.
                if(cache.containsKey(body)) {
                    first_n.addAll( res.get( cache.get(body) ) );
                    if(!first_n.contains(G.getEmpty()))
                        break;
                    else
                        continue;
                }
                System.out.println(N + " -> "+ body);
                for(GrammarSymbol s_i : body.getSymbols()){
                    System.out.println(N + ":: "+s_i.getVal());
                    //IF it is first empty
                    if(s_i.getType() == 't'){
                        first_n.add(s_i.getVal());// empty will be added as it is terminal.
                        cache.put(body, N);
                        break;
                    }
                    Set<String> X_i = res.get(s_i.getVal());
                    if(X_i == null || X_i.size() == 0){
                        S.push(N);// [ELEM STAG ETAG ELEM STAG CONTENT ETAG ELEM STAG ETAG ELEM STAG CONTENT ETAG
                        S.push(s_i.getVal());
                        break M1;
                    }
                    else if(X_i.contains(G.getEmpty())){ //X IS ALREADY FILLED AND LEFT SIDE MUST BE SCANNED TOO.
                        first_n.addAll(X_i);// CONTENT -> ELEMS, ELEMS -> e => FIRST(CONTENT) = { e, < }.
                    }
                    else {
                        first_n.addAll(X_i);
                        cache.put(body, N);
                        break;
                    }
                }
            }
        }
        return res;
    }

    //Compute FIRST for each terminal and non-terminal.
    //WARNING: Only for Grammars without Left-recursion!
    //You must eliminate left-recursion in Grammar _______.
    public static Map<String, Set<String>> firstTable(Grammar G){
        G = Grammar.deleteLeftRecursion(G);
        Set<String> T = G.getTerminals();
        Set<String> NT = G.getNonTerminals();
        HashMap<String, Set<String>> res = new HashMap<String,Set<String>>();
        LinkedStack<String> S = new LinkedStack<>();
        for(String t : T){ //for each terminal put first(t) = t.
            HashSet<String> f = new HashSet<>();
            f.add(t);
            res.put(t,f);
        }
        for(String n : NT){
            S.push(n);
        }
        S.push(G.getStart());
        M1: while(!S.isEmpty()){// compute first for each nonTerminal p.
            Set<String> first_i = new HashSet<>();
            String p = S.top();
            S.pop();
            if(res.containsKey(p))//computed.
                continue;
            Set<GrammarString> bodies = G.getProductions().get(p);
            for(GrammarString str : bodies){
                int l = str.getSymbols().size();
                int ec = 0;
                boolean addAll = false;
                if(str.getSymbols().get(0).equals(G.getEmpty()) && l == 1){
                    first_i.add(G.getEmpty());
                    continue;
                }
                for(GrammarSymbol s : str.getSymbols()){
                    if(s.getType() == 't'){
                        if(s.getVal().equals(G.getEmpty()))
                            ec++;// continue scanning string.
                        else {
                            first_i.add(s.getVal());
                            break;//end scanning after first terminal.
                        }
                    }
                    else{//non-terminal.
                        if(res.containsKey(s.getVal())){//if it's already computed.
                            if(res.get(s.getVal()).contains(G.getEmpty())) {//if FIRST(X) contains empty.
                                Set<String> without_empty =  new HashSet<>(res.get(s.getVal()));//add all except empty.
                                without_empty.remove(G.getEmpty());
                                first_i.addAll(without_empty);
                                ec++;//continue scanning string.
                                continue;
                            }
                            first_i.addAll(res.get(s.getVal()));//add all symbols from first(X_i) where X_i is grammar symbol of str.
                            break;//stop scanning body because X_i doesn't contains empty.
                        }
                        addAll = true;//it is a non-computed, new non-term.
                        break;//suspend scanning until FIRST(X_i) is not computed.
                    }
                }
                if(addAll){//non-computed, new non-term X was discovered while scanning body.
                    S.push(p);//save non-computed current non-term into stack (pause).
                    List<GrammarSymbol> ns = str.getSymbols();
                    for(int i = ns.size() - 1; i >= 0; i--){
                        if(ns.get(i).getType() == 'n')
                            S.push(ns.get(i).getVal());
                    }
                    continue M1;
                }
                else{
                    if(ec == l)//add empty if and only if all grammar symbols X in str contains eps in FIRST(X)
                        first_i.add(G.getEmpty());
                }
            }
            res.put(p, first_i);
        }
        return res;
    }

    //Compute FIRST for GrammarString str. (list of GrammarSymbols)
    public static Set<String> first(GrammarString str, Map<String,Set<String>> firstTable,String eps){
        Set<String> res = new HashSet<>();
        int ec = 0;
        for(GrammarSymbol s : str.getSymbols()){
            HashSet<String> ans = new HashSet<String>(firstTable.get(s.getVal()));
            if(!ans.contains(eps)) {
                res.addAll(ans);
                break;
            }
            else{
                ans.remove(eps);
                res.addAll(ans);//add all symbols except eps and continue.
                ec++;
            }
        }
        if(ec == str.getSymbols().size())
            res.add(eps);
        return res;
    }


    //WARNING: Only for Grammars without Left-recursion!
    //You must eliminate left-recursion in Grammar _______.
    public static Map<String,Set<String>> followTable(Grammar G, Map<String,Set<String>> firstTable){
        List<String> NT = new ArrayList<>(G.getNonTerminals());
        String empty = G.getEmpty();
        HashMap<String,Set<String>> res = new HashMap<String,Set<String>>();
        LinkedStack<String> S = new LinkedStack<>();
        for(String N : NT) {
            S.push(N);
            res.put(N,new ObservableHashSet<String>());
        }
        res.get(G.getStart()).add("$");//add $ to FOLLOW(S) where S = start symbol of _______.
        while(!S.isEmpty()){
            String p = S.top();
            S.pop();
            Set<GrammarString> bodies = G.getProductions().get(p);
            for(GrammarString str : bodies){
                List<GrammarSymbol> l = str.getSymbols();
                for(int i = 0; i < l.size()-1;i++){
                    GrammarSymbol sym = l.get(i);
                    if(sym.getType() != 't'){
                        GrammarString subStr = new GrammarString(new ArrayList<>(l.subList(i+1,l.size())));
                        Set<String> first =  first(subStr,firstTable,empty);
                        if(first.contains(empty)) {
                            ((ObservableHashSet<String>) res.get(p)).attach((ObservableHashSet<String>) res.get(l.get(i).getVal()));
                            first.addAll(res.get(p));
                        }
                        first.remove(empty);
//                        if(sym.getVal().equals(_______.getStart()))
//                            first.add("$");
                        res.get(l.get(i).getVal()).addAll(first);
                        //res.put(l.get(i).getVal(),first);
                    }
                }
                GrammarSymbol last = l.get(l.size() - 1);
                if(last.getType() != 't'){
                    Set<String> first = res.get(p);
                    ((ObservableHashSet<String>) first).attach((ObservableHashSet<String>) res.get(last.getVal()));
                    //res.put(last.getVal(),first);
                }
            }
        }
        return res;
    }
}