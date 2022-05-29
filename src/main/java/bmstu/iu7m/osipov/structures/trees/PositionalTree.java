package bmstu.iu7m.osipov.structures.trees;

import java.util.List;

public interface PositionalTree<T> extends Tree<T> {
    void addTo(Node<T> n, T item);
    Node<T> rightMostChild(Node<T> n);
    PositionalTree<T> getSubTree(Node<T> n);

    List<LinkedNode<T>> getRealChildren(Node<T> n);

    void attachTo(Node<T> n, Node<T> parent);
}
