package bmstu.iu7m.osipov.structures.trees.translators;

import bmstu.iu7m.osipov.services.grammars.*;
import bmstu.iu7m.osipov.services.grammars.directives.SyntaxDirectedTranslation;
import bmstu.iu7m.osipov.services.lexers.LanguageSymbol;
import bmstu.iu7m.osipov.services.lexers.Token;
import bmstu.iu7m.osipov.structures.trees.Action;
import bmstu.iu7m.osipov.structures.trees.LinkedNode;
import bmstu.iu7m.osipov.structures.trees.Node;

import java.util.Set;

public class TranslationsAttacher implements Action<Node<LanguageSymbol>> {
    private Grammar G;
    private int last_id;
    public TranslationsAttacher(Grammar G, int last_id){
        this.G = G;
        this.last_id = last_id + 2;
    }

    @Override
    public void perform(Node<LanguageSymbol> arg) {
        LinkedNode<LanguageSymbol> t = (LinkedNode<LanguageSymbol>) arg;
        if(t.getChildren().size() == 0)
            return;

        Set<GrammarString> rule = G.getProductions().get(t.getValue().getName());
        GrammarString yield = mapToProduction(t);

        //System.out.println("Rule: "+t.getValue().getName()+" -> "+yield);

        //System.out.println("search alts for "+t.getValue().getName());
        for(GrammarString alt : rule){
            //System.out.println(alt);
            if(alt.equals(yield)) {
                //System.out.println("matched alt is "+alt);
                yield = alt;
                break;
            }
        }

        // Augment list of children of t.
        if(yield instanceof GrammarSDTString){
            //System.out.println("SDT matched: "+yield);
            GrammarSDTString replacement = (GrammarSDTString) yield;

            // scan augmented string (with actions).
            for(SyntaxSymbol sym : replacement.getActions()){
                //System.out.println(sym.getClass());

                if(sym instanceof SyntaxDirectedTranslation){
                    SyntaxDirectedTranslation act = (SyntaxDirectedTranslation) sym;
                    //System.out.println("Found action: "+act);
                    //System.out.println("for rule: "+yield);
                    LinkedNode<LanguageSymbol> nc = new LinkedNode<>();
                    nc.setIdx(last_id);
                    last_id++;
                    nc.setValue(act);//act implements Translation -> LanguageSymbol

                    if(act.getPos() >= t.getChildren().size()){
                        t.getChildren().add(nc);
                    }
                    else {
                        t.getChildren().add(act.getPos(), nc);
                    }
                    nc.setParent(t);
                }
            }
        }
    }

    private GrammarString mapToProduction(LinkedNode<LanguageSymbol> node){
        GrammarString g = new GrammarString();
        for(int i = 0; i < node.getChildren().size(); i++){ //because of STACK of Parser.
            LinkedNode<LanguageSymbol> ch = node.getChildren().get(i);
            if(ch.getValue() instanceof Token)
                g.getSymbols().add( (Token) ch.getValue()); //Token extends GrammarSymbol and implements LanguageSymbol.

        }
        return g;
    }
}