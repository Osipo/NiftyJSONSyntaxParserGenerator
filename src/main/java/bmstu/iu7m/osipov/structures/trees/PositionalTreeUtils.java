package bmstu.iu7m.osipov.structures.trees;

import java.util.List;
import java.util.function.Predicate;

public class PositionalTreeUtils {

    public static <T> Node<T> rightMostLeafOf(PositionalTree<T> tree, Node<T> subParent, Predicate<T> predicate){
        Node<T> m = subParent;
        while(subParent != null){
            m = subParent;
            if(predicate.test(m.getValue()))
                break;
            subParent = tree.rightMostChild(subParent);
        }
        return m;
    }

    public static <T> Node<T> rightMostLeafOf(PositionalTree<T> tree, Node<T> subParent){
        return rightMostLeafOf(tree, subParent, (val) -> false); //false predicate cause search until leaf.
    }

    //Returns index of child node in parent children if presented else -1.
    //The nodes must be in specified tree.
    public static <T> int indexOfChild(PositionalTree<T> tree, Node<T> parent, Node<T> child){
        int i = -1;
        List<LinkedNode<T>> children = tree.getRealChildren(parent);
        int l = children.size();
        for(int j = 0; j < l; j++){
            if(children.get(j).equals(child)){
                i = j;
                break;
            }
        }
        return i;
    }

    //Is node ancestor for node n at tree.
    public static <T> boolean isAncestorOf(PositionalTree<T> tree, Node<T> ancestor, Node<T> n){
        if(tree == null || ancestor == null || n == null)
            return false;

        Node<T> cparent = tree.parent(n); //ancestor is not self n.
        while(cparent != null){
            if(ancestor.equals(cparent)) //parent_i of n is equal to ancestor.
                return true;
            cparent = tree.parent(cparent);
        }
        return false;
    }

    //has node n Parent that satisfy cond
    public static <T> boolean hasParentThat(PositionalTree<T> tree, Node<T> n, Predicate<T> cond){
        if(tree == null || n == null || cond == null)
            return false;
        Node<T> cparent = tree.parent(n); //ancestor is not self n.
        while(cparent != null){
            if(cond.test(cparent.getValue()))
                return true;
            cparent = tree.parent(cparent);
        }
        return false;
    }

    public static <T> Node<T> getParentThat(PositionalTree<T> tree, Node<T> n, Predicate<T> cond){
        if(tree == null || n == null || cond == null)
            return null;
        Node<T> cparent = tree.parent(n); //ancestor is not self n.
        while(cparent != null){
            if(cond.test(cparent.getValue()))
                return cparent;
            cparent = tree.parent(cparent);
        }
        return null;
    }

    public static <T> int indexOfChildAt(PositionalTree<T> tree, Node<T> n, Predicate<T> cond){
        int i = -1;
        if(tree == null || n == null || cond == null)
            return i;
        Node<T> cparent = tree.parent(n); //parent is not self n.
        while(cparent != null){
            if(cond.test(cparent.getValue()))
                break;
            n = cparent;
            cparent = tree.parent(cparent);
        }
        return indexOfChild(tree, cparent, n);
    }

    //has node n sibling that satisfy cond
    public static <T> boolean hasSiblingThat(PositionalTree<T> tree, Node<T> n, Predicate<T> cond){
        if(tree == null || n == null || cond == null)
            return false;
        Node<T> parent = tree.parent(n); //ancestor is not self n.
        if(parent != null){
            List<Node<T>> children = tree.getChildren(parent);
            children.remove(n); //sibling is not self n.
            for(int i = 0; i < children.size(); i++){
                if(cond.test(children.get(i).getValue()))
                    return true;
            }
        }
        return false;
    }
}