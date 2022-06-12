package bmstu.iu7m.osipov.structures.lists;

public class DoubleElementType<T> {
    private T element;
    private DoubleElementType<T> next;
    private DoubleElementType<T> prev;

    public T getElement(){
        return element;
    }
    public void setElement(T elem) {
        this.element = elem;
    }

    public void setNext(DoubleElementType<T> next) {
        this.next = next;
    }
    public DoubleElementType<T> getNext() {
        return next;
    }

    public void setPrevious(DoubleElementType<T> prev){ this.prev = prev;}
    public DoubleElementType<T> getPrev() { return prev;}
}
