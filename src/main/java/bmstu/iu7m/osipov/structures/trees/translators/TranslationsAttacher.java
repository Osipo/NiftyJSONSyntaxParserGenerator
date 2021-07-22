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
    public TranslationsAttacher(Grammar G){
        this.G = G;
    }

    @Override
    public void perform(Node<LanguageSymbol> arg) {
        LinkedNode<LanguageSymbol> t = (LinkedNode<LanguageSymbol>) arg;
        if(t.getChildren().size() == 0)
            return;

        Set<GrammarString> rule = G.getProductions().get(t.getValue().getName());
        GrammarString yield = mapToProduction(t);

        for(GrammarString alt : rule){
            if(alt.equals(yield)) {
                yield = alt;
                break;
            }
        }

        // Augment list of children of t.
        if(yield instanceof GrammarSDTString){
            GrammarSDTString replacement = (GrammarSDTString) yield;

            // scan augmented string (with actions).
            for(SyntaxSymbol sym : replacement.getActions()){
                if(sym instanceof SyntaxDirectedTranslation){
                    SyntaxDirectedTranslation act = (SyntaxDirectedTranslation) sym;
                    LinkedNode<LanguageSymbol> nc = new LinkedNode<>();
                    nc.setValue(act);//act implements Translation -> LanguageSymbol

                    if(act.getPos() >= t.getChildren().size()){
                        t.getChildren().add(nc);
                    }
                    else {
                        t.getChildren().add(act.getPos(), nc);
                    }
                }
            }
        }
    }

    private GrammarString mapToProduction(LinkedNode<LanguageSymbol> node){
        GrammarString g = new GrammarString();
        for(LinkedNode<LanguageSymbol> ch : node.getChildren()){
            if(ch.getValue() instanceof Token)
                g.getSymbols().add((Token)ch.getValue()); //Token extends GrammarSymbol.
        }
        return g;
    }
}
