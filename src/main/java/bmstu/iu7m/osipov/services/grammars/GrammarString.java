package bmstu.iu7m.osipov.services.grammars;

import java.util.ArrayList;
import java.util.List;

public class GrammarString {
    protected List<GrammarSymbol> symbols;

    public GrammarString(){
        this.symbols = new ArrayList<>(40);
    }

    public GrammarString(List<GrammarSymbol> symbols){
        this.symbols = symbols;
    }

    public void setSymbols(List<GrammarSymbol> symbols) {
        this.symbols = symbols;
    }

    public List<GrammarSymbol> getSymbols() {
        return symbols;
    }

    public void addSymbol(GrammarSymbol s){
        symbols.add(s);
    }

    public String getTypedStr(){
        StringBuilder b = new StringBuilder();
        for(GrammarSymbol s : symbols){
            b.append(s.getType());
        }
        return b.toString();
    }

    @Override
    public boolean equals(Object o){
        try{
            if(o == null)
                return false;
            GrammarString gs = (GrammarString) o;
            return gs.getSymbols().equals(symbols);
        }
        catch (ClassCastException e){
            return false;
        }
    }

    @Override
    public int hashCode(){
        StringBuilder b = new StringBuilder();
        for(GrammarSymbol s : symbols){
            b.append(s.getVal());
        }
        return b.toString().hashCode();
    }

    @Override
    public String toString(){
        StringBuilder b = new StringBuilder();
        for(GrammarSymbol s : symbols){
            b.append(s.getVal()).append(" ");
        }
        return b.toString();
    }

    public static GrammarString getEmpty(){
        GrammarSymbol empty = new GrammarSymbol('t',"");
        ArrayList<GrammarSymbol> symbols = new ArrayList<>(1);
        symbols.add(empty);
        return new GrammarString(symbols);
    }
}
