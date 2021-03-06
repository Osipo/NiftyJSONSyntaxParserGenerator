package bmstu.iu7m.osipov.services.grammars;

import java.util.List;

//LR(0) item
public class GrammarItem extends GrammarString {

    protected int pos;//Current position
    protected String h;//header of the production

    public GrammarItem(GrammarString str, String header){
        super(str.getSymbols());
        this.pos = 0;
        this.h = header;
    }

    public GrammarItem(List<GrammarSymbol> symbols, int pos, String header){
        super(symbols);
        this.pos = pos;
        this.h = header;
    }

    public void setPosition(int p ){
        this.pos = p;
    }

    public int getPosition(){
        return pos;
    }

    public GrammarSymbol getAt(){
        if(pos <= 0)
            return symbols.get(0);
        else if(pos >= symbols.size())
            return null;
        return symbols.get(pos);
    }

    public GrammarSymbol getPrev(){
        if(pos <= 0)
            return symbols.get(0);
        return symbols.get(pos - 1);
    }

    public String getHeader(){
        return h;
    }

    @Override
    public String toString(){
        StringBuilder b = new StringBuilder();
        b.append('[').append(h).append(" -> ");
        for(int i = 0; i < symbols.size(); i++){
            if(i == pos)
                b.append('.');
            b.append(symbols.get(i).getVal()).append(" ");
        }
        if(pos == symbols.size())
            b.append('.');
        b.append(']');
        return b.toString();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o) && this.pos == ((GrammarItem)o).getPosition() && this.h.equals( ((GrammarItem) o).h);
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}
