package bmstu.iu7m.osipov.structures.lists;

/* Represents List Node */
public class ElementType<T> {
    private T element;
    private ElementType<T> next;
    public T getElement(){
        return element;
    }
    public void setElement(T elem) {
        this.element = elem;
    }

    public void setNext(ElementType<T> next) {
        this.next = next;
    }

    public ElementType<T> getNext() {
        return next;
    }
}
