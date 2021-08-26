package bmstu.iu7m.osipov.structures.trees;

import bmstu.iu7m.osipov.services.lexers.LanguageSymbol;

import java.util.List;

public class ReverseChildren implements Action<Node<LanguageSymbol>>{

    @Override
    public void perform(Node<LanguageSymbol> arg) {
        LinkedNode<LanguageSymbol> lnode = (LinkedNode<LanguageSymbol>) arg;
        if(lnode.getChildren() == null || lnode.getChildren().size() < 2)
            return;
        List<LinkedNode<LanguageSymbol>> ch = lnode.getChildren();

        // reverse children C1...CN to CN...C1.
        for(int i = 0, j = ch.size() - 1; i < ch.size() / 2; i++, j--){
            LinkedNode<LanguageSymbol> temp = ch.get(i);
            ch.set(i, ch.get(j));
            ch.set(j, temp);
        }
    }
}
