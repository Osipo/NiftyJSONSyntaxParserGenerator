package bmstu.iu7m.osipov.structures.trees.reducers;

import bmstu.iu7m.osipov.services.grammars.Grammar;
import bmstu.iu7m.osipov.services.lexers.Token;
import bmstu.iu7m.osipov.structures.trees.Action;
import bmstu.iu7m.osipov.structures.trees.LinkedNode;
import bmstu.iu7m.osipov.structures.trees.Node;

public class RemoveEmptyNodes  implements Action<Node<Token>> {

    private String empty;
    public RemoveEmptyNodes(Grammar G){
        this.empty = G.getEmpty();
    }

    @Override
    public void perform(Node<Token> arg) {
        LinkedNode<Token> t = (LinkedNode<Token>)arg;
        if(t.getChildren().size() == 1 && t.getChildren().get(0).getValue().getName().equals(empty)){
            LinkedNode<Token> p = t.getParent();
            t.getChildren().get(0).setParent(null);
            t.setChildren(null);
            t.setParent(null);
            p.getChildren().remove(t);
        }
    }
}
