package bmstu.iu7m.osipov.structures.trees;

import java.util.List;

public interface Tree<T> {
    Node<T> parent(Node<T> node);
    List<Node<T>> getChildren(Node<T> n);
    Node<T> leftMostChild(Node<T> node);
    Node<T> rightSibling(Node<T> node);
    Node<T> root();
    T value(Node<T> node);
    void setVisitor(SubVisitor<T> visitor);

    void visit(VisitorMode order, Action<Node<T>> act);
    <R extends Node<T>> void visitFrom(VisitorMode order, Action<Node<T>> act, R subTree);

    void visit(VisitorMode order, Action2<Node<T>, VisitorsNextIteration<T>> act, VisitorsNextIteration<T> arg2);
    <R extends Node<T>> void visitFrom(VisitorMode order, Action2<Node<T>, VisitorsNextIteration<T>> act, R subTree, VisitorsNextIteration<T> arg2);

    int getCount();
    void clear();//FROM ICollection<T>
}
