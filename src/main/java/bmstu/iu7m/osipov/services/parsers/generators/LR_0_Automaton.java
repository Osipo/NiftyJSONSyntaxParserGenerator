package bmstu.iu7m.osipov.services.parsers.generators;

import bmstu.iu7m.osipov.Main;
import bmstu.iu7m.osipov.structures.graphs.Pair;
import bmstu.iu7m.osipov.services.grammars.*;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

//Contains LR(0) items without symbol.
public class LR_0_Automaton {
    private Map<Integer, Set<GrammarItem>> C;// Canonical items. [LR(0)-items]
    protected Map<Pair<Integer,String>,Integer> gotoTable;// Function GOTO(state, Symbol) = newState.

    // Function ACTION(state, Symbol) = s\r N |a|\err\acc
    // where acc = accept, err = error, s = shift => add to stack new state.
    // Contains suffix with number of the new state '_newState'
    // r = reduce => command with two parameters: production header and count of symbols (which will be deleted
    // from stack)
    protected Map<Pair<Integer, String>, String> actionTable;

    //FOLLOW function for each non-term A. returns a set of terms.
    protected Map<String, Set<String>> follow;

    //new Start header for Grammar.
    protected String S;
    //old Start symbol of Grammar.
    protected String S0;

    protected boolean hasErr;

    //ONLY FOR LR_1_Automaton and other subclasses.
    protected LR_0_Automaton(Grammar G, String oldS, String nS, Map<Pair<Integer,String>, Integer> g, Map<String, Set<String>> firstTable){
        this.gotoTable = g;
        this.actionTable = new HashMap<>();
        this.follow = new HashMap<>();
        this.S0 = oldS;
        this.S = nS;
        this.follow = LLParserGenerator.followTable(G, firstTable); //firstTable is already Transformed at CLRParserGenerator.
        this.hasErr = true;
    }

    //For LR_0_Automaton. C is Canonical Set of items for LR(0) Grammar G.
    public LR_0_Automaton(Grammar G, String oldS, String ns, Map<Integer, Set<GrammarItem>> C, Map<Pair<Integer, String>, Integer> g, Map<String, Set<String>> firstTable) {
        this.C = C;
        this.gotoTable = g;
        this.actionTable = new HashMap<>();
        this.follow = new HashMap<>();
        this.S0 = oldS;
        this.S = ns;
        System.out.println("HERE");
        System.out.println(firstTable);

        //Transform FIRST (from non-recursive indexed Grammar to non-indexed G)
        Map<String, Set<String>> oF = new HashMap<>();
        for(String k : firstTable.keySet()){
            int s1 = k.lastIndexOf('\'');
            int s2 = k.lastIndexOf('_');
            if(s1 < s2 && s1 != -1) // ' before _ like A'_ (when ' is part of the original N of G)
                oF.put(k.substring(0, s1 + 1), firstTable.get(k));
            else if(s1 < s2) // s1 == -1.
                oF.put(k.substring(0, s2), firstTable.get(k));
            else if(s1 == s2)// s1 == s2 <=> s1 == -1 and s2 == -1.
                oF.put(k, firstTable.get(k));

        }
        this.follow = LLParserGenerator.followTable(G, oF); //G may be left-recursive
        System.out.println("FIRST");
        System.out.println(oF);
        //System.out.println(follow);
        this.hasErr = true;
        initActions();
        System.out.println("ACTION and GOTO are built.");
    }

    private void initActions(){
        Set<Integer> keys = C.keySet();
        for(Integer i : keys){//for each set of items C_i in C
            Set<GrammarItem> C_i = C.get(i);
            for(GrammarItem item : C_i){//for each item in C_i
                GrammarSymbol s = item.getAt();
                if(s != null && s.getType() == 't'){ //item [A -> a .bc]
                    Pair<Integer, String> k = new Pair<Integer,String>(i, s.getVal());
                    Integer j = gotoTable.getOrDefault(k,-1);

                    if(j != -1 && !actionTable.containsKey(k)) // GOTO IS CORRECT AND RECORD IS NOT FILLED?
                        actionTable.put(new Pair<Integer, String>(i, s.getVal()), "s_"+j);// s_[state]

                    else if(j != -1){
                        String c = actionTable.get(k);
                        if(c.charAt(0) != 's') {
                            hasErr = true;
                            System.out.println("Error.");
                            System.out.println("\tErr item from I_"+i+": " + item+" term: "+item.getAt());
                            System.out.println("\tAmbiguous: "+actionTable.get(k)+" / s_"+j);
                            System.out.println("\tConflict detected! (Reduce-Shift) Grammar is not SLR(1)!");
                            System.out.println("\tTry to resolve as shift.");
                            actionTable.put(k,"s_"+j);
                        }
                        else if(Integer.parseInt(c.substring(2)) != j){
                            hasErr = true;
                            System.out.println("Error.");
                            System.out.println("\tErr item from I"+i+": " + item+"term: "+item.getAt());
                            System.out.println("\tAmbiguous: "+actionTable.get(k)+" / s_"+j);
                            System.out.println("\tConflict detected! (Shift-Shift) Grammar is not SLR(1)!");
                        }
                    }
                    else{
                        actionTable.put(new Pair<Integer, String>(i,s.getVal()),"err");
                    }
                }
                else if (s != null && item.getPosition() < item.getSymbols().size()){ // item [A -> a .Bc]
                    actionTable.put(new Pair<Integer, String>(i, s.getVal()), "err");
                }
                else {// item like [A -> y.]
                    //item [S' -> S.]
                    if(item.getHeader().equals(S) && item.getSymbols().get(0).getVal().equals(S0)){
                        actionTable.put(new Pair<Integer, String>(i, "$"),"acc");
                        continue;
                    }
                    //item [S' -> y.]
                    else if(item.getHeader().equals(S)){
                        actionTable.put(new Pair<Integer,String>(i,"$"),"err");
                        continue;
                    }
                    Set<String> t = follow.get(item.getHeader());
                    //System.out.println(item.getHeader() + ": "+t);
                    //System.out.println(i+": "+item);
                    for(String term : t){
                        Pair<Integer, String> k = new Pair<Integer, String>(i, term);
                        if(!actionTable.containsKey(k))//RECORD IS NOT FILLED?
                            actionTable.put(k, "r_" + item.getHeader()+":"+item.getSymbols().size());
                        else{
                            hasErr = true;
                            String com = actionTable.get(k);
                            char a = com.charAt(0);
                            String conftype = (a == 's') ? "(Shift-Reduce)" : "(Reduce-Reduce)";
                            System.out.println("Error.");
                            System.out.println("\tErr item from I"+i+": " + item+" term: "+term);
                            System.out.println("\tAmbiguous: "+com+" / r_"+item.getHeader()+":"+item.getSymbols().size());
                            System.out.println("\tConflict detected! "+conftype+" Grammar is not SLR(1)!");
                        }
                    }
                }
            }
        }
    }

    public Map<Integer, Set<GrammarItem>> getItems() {
        return C;
    }

    public Map<Pair<Integer, String>, Integer> getGotoTable() {
        return gotoTable;
    }

    public Map<Pair<Integer, String>, String> getActionTable() {
        return actionTable;
    }

    public String getStart() {
        return S;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Canonical Grammar LR(0) items : {");
        for(Integer i : C.keySet()){
            sb.append("\n\tI").append(i).append(": {");
            for(GrammarItem item : C.get(i)){
                sb.append("\n\t\t").append(item);
            }
            sb.append("\n\t}");
        }
        sb.append("\n}");
        sb.append("\n GOTO: {");
        for(Pair<Integer,String> k : gotoTable.keySet()){
            sb.append("\n\t"+k.getV1()+", "+k.getV2()+" = "+gotoTable.get(k));
        }
        sb.append("\n}");
        return sb.toString();
    }

    public File toDotFile() throws IOException {
        File f = File.createTempFile("LR_automaton", ".dot", new File(Main.CWD));
        f.setWritable(true);
        f.setReadable(true);
        BufferedWriter writer = new BufferedWriter(new FileWriter(f));
        writer.write("digraph LRA {\n");
        for(Integer state : C.keySet()){
            writer.write(state + " [shape=\"rect\", label=\"");
            Set<GrammarItem> I = C.get(state);
            for(GrammarItem it : I){
                writer.write('\t');
                writer.write(it.toString());
                writer.newLine();
            }
            writer.write("\"];\n");
        }

        for(Pair<Integer, String> tran : gotoTable.keySet()){
            int to = gotoTable.get(tran);
            writer.write("\t "+tran.getV1() + " -> "+to);
            writer.write(" [label=\"" + tran.getV2() + "\"];");
            writer.newLine();
        }
        writer.write("}\n");
        writer.close();
        return f;
    }

    public File getImageFromDot() throws IOException {
        File dotF = toDotFile();
        File f = File.createTempFile("LR_automaton", ".png", new File(Main.CWD));
        f.setWritable(true);
        f.setReadable(true);
        Graphviz.fromFile(dotF).render(Format.PNG).toFile(f);
        dotF.delete();
        f.deleteOnExit();
        return f;
    }

    public boolean noConflicts(){
        return hasErr;
    }
}
