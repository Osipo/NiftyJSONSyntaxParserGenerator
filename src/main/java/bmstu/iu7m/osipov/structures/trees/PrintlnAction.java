package bmstu.iu7m.osipov.structures.trees;

public class PrintlnAction<T> implements Action<Node<T>> {
    @Override
    public void perform(Node<T> arg) {
        System.out.println(arg.getValue().toString());
    }
}
