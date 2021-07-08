package bmstu.iu7m.osipov.structures.trees.reducers;

import bmstu.iu7m.osipov.services.lexers.Token;
import bmstu.iu7m.osipov.structures.trees.Action;
import bmstu.iu7m.osipov.structures.trees.LinkedNode;
import bmstu.iu7m.osipov.structures.trees.Node;

import java.util.function.Predicate;

public class BreakTailNode implements Action<Node<Token>> {
    private Predicate<LinkedNode<Token>> f;

    public BreakTailNode(){
        this.f = this::isB;
    }

    public BreakTailNode(Predicate<LinkedNode<Token>> f){
        this.f = f;
    }

    @Override
    public void perform(Node<Token> arg) {
        LinkedNode<Token> t = (LinkedNode<Token>) arg;
        if(f != null && f.test(t)){
            up(t);
        }
        else if(f == null && t.getValue().getName().contains("\'") && isB(t)){
            up(t);
        }
    }

    //move children of t to its parent (with removing t).
    // [A -> t -> b/c/d] => [A -> b/c/d].
    private void up(LinkedNode<Token> t){
        // System.out.println(t.getValue());
        for(LinkedNode<Token> c : t.getChildren()){
            c.setParent(t.getParent());
            t.getParent().getChildren().add(c);
        }
        t.getParent().getChildren().remove(t);
        t.setParent(null);
        t.setChildren(null);
    }

    // is node arg is RIGHTMOST_CHILD of its parent.
    private boolean isB(LinkedNode<Token> arg){
        return arg.getParent() != null && arg.getChildren() != null && arg.getChildren().size() > 0 &&
                (arg.getParent().getChildren().indexOf(arg) == arg.getParent().getChildren().size() - 1
                || arg.getParent().getChildren().indexOf(arg) == 0);
    }
}
