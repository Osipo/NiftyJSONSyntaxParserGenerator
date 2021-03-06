package bmstu.iu7m.osipov.structures.trees;

//Visit Tree n from its ROOT.
public interface Visitor<T> {
    void preOrder(Tree<T> n, Action<Node<T>> act);
    void inOrder(Tree<T> t, Action<Node<T>> act);
    void postOrder(Tree<T> n, Action<Node<T>> act);

    void preOrder(Tree<T> n, Action2<Node<T>, VisitorsNextIteration<T>> act, VisitorsNextIteration<T> next);
    void postOrder(Tree<T> n, Action2<Node<T>, VisitorsNextIteration<T>> act, VisitorsNextIteration<T> next);



    void setNoCount(boolean f);
    boolean isNoCount();
}