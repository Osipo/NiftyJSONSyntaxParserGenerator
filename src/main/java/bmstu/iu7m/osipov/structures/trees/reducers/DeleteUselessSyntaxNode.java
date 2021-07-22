package bmstu.iu7m.osipov.structures.trees.reducers;

import bmstu.iu7m.osipov.services.lexers.LanguageSymbol;
import bmstu.iu7m.osipov.services.lexers.Token;
import bmstu.iu7m.osipov.services.grammars.Grammar;
import bmstu.iu7m.osipov.structures.trees.Node;
import bmstu.iu7m.osipov.structures.trees.LinkedNode;
import bmstu.iu7m.osipov.structures.trees.Action;

public class DeleteUselessSyntaxNode implements Action<Node<LanguageSymbol>> {

    private Grammar G;
    public DeleteUselessSyntaxNode(Grammar G){
        this.G = G;
    }

    @Override
    public void perform(Node<LanguageSymbol> arg) {
        LinkedNode<LanguageSymbol> t = (LinkedNode<LanguageSymbol>) arg;
        if(t.getValue().getType() == 't'){
            String term = t.getValue().getName();
            if(G.getOperands().contains(term) || G.getOperators().contains(term) || G.getKeywords().contains(term) || G.getScopeBegin().equals(term) || G.getScopeEnd().equals(term))
                return;
            LinkedNode<LanguageSymbol> p = t.getParent();
            p.getChildren().remove(t);
            t.setParent(null);
            t.setValue(null);
        }
    }
}
