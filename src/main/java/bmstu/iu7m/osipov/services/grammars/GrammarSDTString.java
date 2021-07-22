package bmstu.iu7m.osipov.services.grammars;

import bmstu.iu7m.osipov.services.grammars.directives.SyntaxDirectedTranslation;

import java.util.ArrayList;
import java.util.List;

public class GrammarSDTString extends GrammarString {
    private List<SyntaxSymbol> s_with_acts;

    public GrammarSDTString(){
        super();
        s_with_acts = new ArrayList<>(40);
    }

    public List<SyntaxSymbol> getActions(){
        return s_with_acts;
    }

    public void addToAugmentedBody(SyntaxSymbol s){
        this.s_with_acts.add(s);
    }

}
