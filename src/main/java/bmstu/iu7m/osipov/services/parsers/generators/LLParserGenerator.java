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

        Map<String, Set<String>> firstTable = firstTable2(G);
        System.out.println("Non left-recursive Grammar");
        System.out.println(G);
        System.out.println("FIRST");
        System.out.println(firstTable);
        Map<String, Set<String>> followTable = followTable(G, firstTable);
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
                        table.put(rec, b);
                    }
            }
        }
        if(hasErr){
            table.put(new Pair<>("", ""), new GrammarString());
        }
        return table;
    }



    //FIRST function for each symbol X of grammar G.
    //TODO: Works only for Non-recursive, Canon Grammar (without Cycles, empty rules, recursion and useless symbols)
    public static Map<String, Set<String>> firstTable2(Grammar G){
        Set<String> N_e = G.getN_e(); // Non-terminals with rules N -> e.
        G = Grammar.deleteLeftRecursion(G);
        Set<String> T = G.getTerminals();
        Set<String> NT = G.getNonTerminals();
        HashMap<String, Set<String>> res = new HashMap<String,Set<String>>();
        LinkedStack<String> S = new LinkedStack<>();

        //System.out.println(G);

        /* For each terminal t add to Table FIRST(t) = { t }; */
        for(String t : T){
            HashSet<String> f = new HashSet<>();
            f.add(t);
            res.put(t, f);
        }

        //N_E
        Set<String> nonIndexed = NT.stream()
                .filter(x -> N_e.contains( x.substring(0, x.indexOf('_')) ) )
                .collect(Collectors.toSet());
        if(nonIndexed.size() == 0 && N_e.size() > 0){
            nonIndexed = N_e;
        }

        for(String n : NT){
            HashSet<String> f_i = new HashSet<>();
            res.put(n, f_i);
        }


        S.push(G.getStart());
        Set<String> processed = new TreeSet<>();
        int alts_size = 0;
        int proc_alts = 0;
        while(!S.isEmpty()){
            String N = S.top();
            S.pop();
            Set<String> first_n = res.getOrDefault(N, null);
            if(processed.contains(N) || first_n == null)
                continue;


            Set<GrammarString> prods = G.getProductions().get(N);
            alts_size = prods.size();
            proc_alts = 0;
            M1: for(GrammarString body : prods){
                // System.out.println(N + " -> "+ body);
                for(GrammarSymbol s_i : body.getSymbols()){
                    //System.out.println(N + ":: "+s_i.getVal());
                    //IF it is first empty

                    if(s_i.getType() == 't'){
                        first_n.add(s_i.getVal());// empty will be added as it is terminal.
                        proc_alts++;
                        break;
                    }
                    Set<String> X_i = res.get(s_i.getVal());
                    if(X_i == null || X_i.size() == 0){
                        S.push(N);
                        S.push(s_i.getVal());
                        break M1;//found new Non-Terminal s_i at body. Compute FIRST(s_i) then return to the body.
                    }
                    else if(X_i.contains(G.getEmpty())){ //X IS ALREADY FILLED AND LEFT SIDE MUST BE SCANNED TOO.
                        first_n.addAll(X_i);
                        first_n.remove(G.getEmpty()); //Add all except empty.
                    }
                    else {
                        first_n.addAll(X_i);
                        proc_alts++;
                        break;
                    }
                }
                if(proc_alts == alts_size) {
                    processed.add(N);
                    proc_alts = 0;
                }
            }
        }
        if(processed.size() != NT.size()){
            HashSet<String> rest_N = new HashSet<>(NT);
            rest_N.removeAll(processed);
            alts_size = 0;
            proc_alts = 0;
            for(String n : rest_N){
                S.push(n);
            }
            while(!S.isEmpty()){
                String N = S.top();
                S.pop();
                Set<String> first_n = res.getOrDefault(N, null);
                if(processed.contains(N) || first_n == null)
                    continue;

                Set<GrammarString> prods = G.getProductions().get(N);
                alts_size = prods.size();
                proc_alts = 0;
                M1: for(GrammarString body : prods){
                    // System.out.println(N + " -> "+ body);
                    for(GrammarSymbol s_i : body.getSymbols()){
                        //System.out.println(N + ":: "+s_i.getVal());
                        //IF it is first empty

                        if(s_i.getType() == 't'){
                            first_n.add(s_i.getVal());// empty will be added as it is terminal.
                            proc_alts++;
                            break;
                        }
                        Set<String> X_i = res.get(s_i.getVal());
                        if(X_i == null || X_i.size() == 0){
                            S.push(N);
                            S.push(s_i.getVal());
                            break M1;//found new Non-Terminal s_i at body. Compute FIRST(s_i) then return to the body.
                        }
                        else if(X_i.contains(G.getEmpty())){ //X IS ALREADY FILLED AND LEFT SIDE MUST BE SCANNED TOO.
                            first_n.addAll(X_i);
                            first_n.remove(G.getEmpty()); //Add all except empty.
                        }
                        else {
                            first_n.addAll(X_i);
                            proc_alts++;
                            break;
                        }
                    }
                    if(proc_alts == alts_size) {
                        processed.add(N);
                        proc_alts = 0;
                    }
                }
            }
        }

        //N from non-left recursive Grammar G which belongs to N_e of old G.
        //N_e for non-left recursive Grammar is empty as it does not contains rules A -> e.
        for(String n : nonIndexed){
            Set<String> ef = res.getOrDefault(n, null);
            if(ef != null)
                ef.add(G.getEmpty());
        }

        return res;
    }



    //Compute FIRST for GrammarString str. (list of GrammarSymbols)
    public static Set<String> first(GrammarString str, Map<String, Set<String>> firstTable, String eps){
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

        //debug
        /*
        for(String k : res.keySet()){
            System.out.println(k + " = " + res.get(k));
        }
        */
        return res;
    }
}