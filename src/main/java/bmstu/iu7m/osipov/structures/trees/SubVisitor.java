package bmstu.iu7m.osipov.structures.trees;

//VISIT SUB_TREES WHERE ROOT(SUB_TREE) = node.
public interface SubVisitor<T> extends Visitor<T> {
    void preOrder(Tree<T> n, Action<Node<T>> act, Node<T> node);
    void inOrder(Tree<T> n, Action<Node<T>> act, Node<T> node);
    void postOrder(Tree<T> n, Action<Node<T>> act, Node<T> node);

    void preOrder(Tree<T> n, Action2<Node<T>, VisitorsNextIteration<T>> act, Node<T> node, VisitorsNextIteration<T> next);
    void postOrder(Tree<T> n, Action2<Node<T>, VisitorsNextIteration<T>> act, Node<T> node, VisitorsNextIteration<T> next);

}
