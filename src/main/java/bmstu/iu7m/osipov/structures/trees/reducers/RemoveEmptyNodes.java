package bmstu.iu7m.osipov.structures.trees.reducers;

import bmstu.iu7m.osipov.services.grammars.Grammar;
import bmstu.iu7m.osipov.services.lexers.LanguageSymbol;
import bmstu.iu7m.osipov.services.lexers.Token;
import bmstu.iu7m.osipov.structures.trees.Action;
import bmstu.iu7m.osipov.structures.trees.LinkedNode;
import bmstu.iu7m.osipov.structures.trees.Node;

public class RemoveEmptyNodes  implements Action<Node<LanguageSymbol>> {

    private String empty;
    public RemoveEmptyNodes(Grammar G){
        this.empty = G.getEmpty();
    }

    @Override
    public void perform(Node<LanguageSymbol> arg) {
        LinkedNode<LanguageSymbol> t = (LinkedNode<LanguageSymbol>)arg;
        if(t.getChildren().size() == 1 && t.getChildren().get(0).getValue().getName().equals(empty)){
            LinkedNode<LanguageSymbol> p = t.getParent();
            t.getChildren().get(0).setParent(null);
            t.setChildren(null);
            t.setParent(null);
            p.getChildren().remove(t);
        }
    }
}
