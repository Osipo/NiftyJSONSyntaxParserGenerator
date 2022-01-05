package bmstu.iu7m.osipov.structures.trees.translators;

import bmstu.iu7m.osipov.services.grammars.Grammar;
import bmstu.iu7m.osipov.services.lexers.LanguageSymbol;
import bmstu.iu7m.osipov.services.lexers.Translation;
import bmstu.iu7m.osipov.structures.trees.Action;
import bmstu.iu7m.osipov.structures.trees.LinkedNode;
import bmstu.iu7m.osipov.structures.trees.Node;

public class TranslationsDetacher implements Action<Node<LanguageSymbol>> {


    public TranslationsDetacher(){
    }

    @Override
    public void perform(Node<LanguageSymbol> arg) {
        LinkedNode<LanguageSymbol> t = (LinkedNode<LanguageSymbol>) arg;
        if(t.getValue() instanceof Translation){
            LinkedNode<LanguageSymbol> p = t.getParent();
            t.setParent(null);
            p.getChildren().remove(t);
        }
    }
}
