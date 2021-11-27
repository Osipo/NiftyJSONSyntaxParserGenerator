package bmstu.iu7m.osipov.structures.lists;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


public class LinkedStack<T> implements Iterable<T> {
    private ElementType<T> _head;
    private int _count;//count

    public LinkedStack(){
        this._count = 0;
    }

    public LinkedStack(LinkedStack<T> elems){
        this._count = 0;
        for(T elem : elems){
            push(elem);
        }
    }
    public LinkedStack(List<T> elems){
        this._count = 0;
        for(T elem : elems){
            push(elem);
        }
    }

    //EMPTY(S): BOOLEAN
    public boolean isEmpty(){
        return this._count == 0;
    }

    //PUSH
    public void push(T item){
        _count += 1;
        ElementType<T> node = new ElementType<T>();
        node.setElement(item);
        node.setNext(_head);
        _head = node;
    }

    //POP
    public void pop(){
        if(isEmpty()){
            System.out.println("Error. Stack is empty");
            return;
        }
        else{
            ElementType<T> temp = _head;
            _head = _head.getNext();
            _count -= 1;
        }
    }

    //TOP
    public T top(){
        if(isEmpty()){
            System.out.println("Error. Stack is empty");
            return null;
        }
        else return _head.getElement();
    }

    public T topFrom(int step){
        if(step == 0)
            return top();
        else if(step < 0)
            return null;
        ElementType<T> temp = _head;
        while(step > 0 && temp != null) {
            temp = temp.getNext();
            step--;
        }
        return (temp == null) ? null : temp.getElement();
    }

    public void add(T item){
        push(item);
    }

    public void addAll(Collection<T> col){
        for(T el: col){
            push(el);
        }
    }

    public boolean remove(T item){
        if(isEmpty()){
            System.out.println("Error. Stack is empty");
            return false;
        }
        else if(item == null && top() == null){
            pop();
            return true;
        }
        else if(item == null && top() != null)
            return false;
        else if(top() == null && item != null)
            return false;
        else if(top().equals(item)){
            pop();
            return true;
        }
        else
            return false;
    }

    public boolean contains(T item){
        ElementType<T> q = _head;
        while(q != null){
            if(q.getElement() == null && item == null)
                return true;
            else if((q.getElement() == null && item != null)
                    || (q.getElement() != null && item == null)){
                q = q.getNext();
            }
            else if(q.getElement().equals(item)){
                return true;
            }
            else
                q = q.getNext();
        }
        return false;
    }

    public boolean isReadOnly(){
        return false;
    }

    public int size(){
        return _count;
    }

    //MAKENULL
    public void clear(){
        this._head = null;
        this._count = 0;
    }

    public void copy(T[] array, int arrayIndex){
        if(arrayIndex < 0 || arrayIndex >= array.length){
            System.out.println("arrayIndex is out of range");
            return;
        }
        ElementType<T> c = _head;
        for(int i = arrayIndex; i < array.length && c != null; i++){
            array[i] = c.getElement();
            c = c.getNext();
        }
    }

    public T[] toArray(T[] ar){
        ElementType<T> c = _head;
        for(int i = 0; i < ar.length && c != null; i++){
            ar[i] = c.getElement();
            c = c.getNext();
        }
        return ar;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("->[ ");
        ElementType<T> c = _head;
        while(c != null){
            sb.append(c.getElement().toString()+" ");
            c = c.getNext();
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public Iterator<T> iterator() {
        return new IterS();
    }


    private class IterS implements Iterator<T>{

        ElementType<T> current = _head;

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public T next() {
            T el = current.getElement();
            current = current.getNext();
            return el;
        }

    }
}
