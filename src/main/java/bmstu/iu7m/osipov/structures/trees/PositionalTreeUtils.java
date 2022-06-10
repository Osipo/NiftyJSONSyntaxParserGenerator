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
}