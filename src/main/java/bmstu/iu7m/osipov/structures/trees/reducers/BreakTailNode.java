package bmstu.iu7m.osipov.structures.trees.reducers;

import bmstu.iu7m.osipov.services.lexers.LanguageSymbol;
import bmstu.iu7m.osipov.services.lexers.Token;
import bmstu.iu7m.osipov.structures.trees.Action;
import bmstu.iu7m.osipov.structures.trees.LinkedNode;
import bmstu.iu7m.osipov.structures.trees.Node;

import java.util.function.Predicate;

public class BreakTailNode implements Action<Node<LanguageSymbol>> {
    private Predicate<LinkedNode<LanguageSymbol>> f;

    public BreakTailNode(){
        this.f = this::isB;
    }

    public BreakTailNode(Predicate<LinkedNode<LanguageSymbol>> f){
        this.f = f;
    }

    @Override
    public void perform(Node<LanguageSymbol> arg) {
        LinkedNode<LanguageSymbol> t = (LinkedNode<LanguageSymbol>) arg;
        if(f != null && f.test(t)){
            up(t);
        }
        else if(f == null && t.getValue().getName().contains("\'") && isB(t)){
            up(t);
        }
    }

    //move children of t to its parent (with removing t).
    // [A -> t -> b/c/d] => [A -> b/c/d].
    private void up(LinkedNode<LanguageSymbol> t){
        // System.out.println(t.getValue());
        for(LinkedNode<LanguageSymbol> c : t.getChildren()){
            c.setParent(t.getParent());
            t.getParent().getChildren().add(c);
        }
        t.getParent().getChildren().remove(t);
        t.setParent(null);
        t.setChildren(null);
    }

    // is node arg is RIGHTMOST_CHILD of its parent.
    private boolean isB(LinkedNode<LanguageSymbol> arg){
        return arg.getParent() != null && arg.getChildren() != null && arg.getChildren().size() > 0 &&
                (arg.getParent().getChildren().indexOf(arg) == arg.getParent().getChildren().size() - 1
                || arg.getParent().getChildren().indexOf(arg) == 0);
    }
}
