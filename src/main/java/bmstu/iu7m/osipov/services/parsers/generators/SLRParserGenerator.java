package bmstu.iu7m.osipov.services.parsers.generators;

import bmstu.iu7m.osipov.structures.graphs.Pair;
import bmstu.iu7m.osipov.structures.lists.LinkedStack;
import bmstu.iu7m.osipov.services.grammars.*;
import com.codepoetics.protonpack.maps.MapStream;

import java.util.*;

public class SLRParserGenerator {
    public static LR_0_Automaton buildLRAutomaton(Grammar G) throws Exception {
        String S0 = G.getStart();
        String S1 = G.getStart()+"\'";
        Map<Integer, Set<GrammarItem>> C = new TreeMap<>();//canonical items.
        Map< Pair<Integer, String>, Integer> gotoTable = new HashMap<>();//goto function.

        try {
            G.extendStart(S1);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            throw new Exception("Cannot extend Grammar");
        }

        Map<String, Set<String>> firstTable = LLParserGenerator.firstTable2(G);

        GrammarString start = new GrammarString();
        start.addSymbol(new GrammarSymbol('n', S0));
        GrammarItem S = new GrammarItem(start, S1);//point [S' -> .S]
        Set<String> symbols = new HashSet<>(G.getNonTerminals());
        symbols.addAll(G.getTerminals());

        LinkedStack<Set<GrammarItem>> ST = new LinkedStack<>();
        LinkedStack<Integer> states = new LinkedStack<>();//number of states.

        //Compute closure of [S' -> .S]
        C.put(0, closure(G, S));
        ST.push(C.get(0));
        states.push(0);
        int count = 0;
        int cstate = 0;// contains top of the states

        while(!ST.isEmpty() && !states.isEmpty()){
            Set<GrammarItem> old = ST.top();
            ST.pop();
            cstate = states.top();
            states.pop();
            Set<GrammarItem> ns = null;
            //System.out.println("Now scanning I"+cstate);
            for(String s : symbols){
                ns = gotoSet(G, old, s);
                if(ns.size() > 0 && !C.containsValue(ns)) {//IF NOT NULL AND NOT EMPTY AND NEW => ADD NEW SET.
                    count++;//new set was discovered.
                    ST.push(ns);
                    states.push(count);
                    gotoTable.put(new Pair<Integer,String>(cstate, s), count);
                    C.put(count, ns);
                }
                else if(ns.size() > 0) {
                    for(Map.Entry e: C.entrySet()){
                        if(e.getValue().equals(ns))
                            gotoTable.put(new Pair<Integer, String>(cstate, s), (Integer) e.getKey());
                    }
                }
            }
            //System.out.println("Item I"+cstate+" was fully processed within all symbols of G.");
        }
        System.out.println("Canonical Set of Items was built.");


        return new LR_0_Automaton(G, S0, S1, C, gotoTable, firstTable);
    }



    private static Set<GrammarItem> closure(Grammar G, GrammarItem item){
        Set<GrammarItem> C = new HashSet<>();
        C.add(item);//initially C = { item };

        LinkedStack<String> ST = new LinkedStack<>();//STACK of closure.

        //Items like [A -> y.] and [A -> a .b C] do not generate new items.
        if(item.getAt() == null || item.getAt().getType() != 'n')
            return C;

        ST.push(item.getAt().getVal());//scan production for .X symbol. [A -> a .X b]
        ArrayList<String> backtrack = new ArrayList<>();//backtracking for uniqueness of headers of productions.
        String current = null;//current .X symbol.

        while(!ST.isEmpty()) {
            current = ST.top();
            backtrack.add(current);
            Set<GrammarString> rules = G.getProductions().get(current);
            ST.pop();
            if(rules != null) {
                for(GrammarString r : rules) {//check its productions.
                    C.add(new GrammarItem(r, current));//init new item [B -> .y] with pos = 0.
                    GrammarSymbol next = r.getSymbols().get(0);//get .y and check if it is a non-term
                    if (next != null && next.getType() == 'n' && !backtrack.contains(next.getVal())) {//if it is a new non-term.
                        ST.add(next.getVal());//scan its production too.
                        backtrack.add(next.getVal());
                    }
                }
            }
        }
        return C;
    }

    private static Set<GrammarItem> gotoSet(Grammar G, Set<GrammarItem> I, String symbol){
        Set<GrammarItem> R = new HashSet<>();
        for(GrammarItem point : I){
            if(point.getAt() != null && point.getAt().getVal().equals(symbol)){// Item: [A -> a .symbol b]
                GrammarItem ni = new GrammarItem(point.getSymbols(),point.getPosition() + 1, point.getHeader());// Item: [A -> a symbol .b]
                R.addAll(closure(G, ni));
            }
        }
        return R;
    }
}
