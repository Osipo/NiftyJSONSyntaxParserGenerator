package bmstu.iu7m.osipov.structures.lists;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.Spliterator;

//Adds and removes items from start and the end of the list.
public class LinkedDeque<T> implements Iterable<T> {
    private DoubleElementType<T> _head;
    private DoubleElementType<T> _tail;
    private int _count;

    public LinkedDeque(){
        this._count = 0;
    }

    //EMPTY(S): BOOLEAN
    public boolean isEmpty(){ return this._count == 0; }

    //PUSH
    public void push(T item){
        _count += 1;
        DoubleElementType<T> node = new DoubleElementType<T>();
        node.setElement(item);
        node.setNext(_head);
        if(_count > 1) {
            _head.setPrevious(node);
        }
        else { //_count == 1.
            _tail = node;
        }
        _head = node;
    }

    //POP
    public void pop(){
        if(!isEmpty()){
            DoubleElementType<T> temp = _head;
            _head = _head.getNext();
            _count -= 1;
            if(_count != 0)
                _head.setPrevious(null);
            else
                _tail = _head; //null.
        }
    }

    //APPEND
    public void append(T item){
        _count += 1;
        DoubleElementType<T> node = new DoubleElementType<T>();
        node.setElement(item);
        if(_count > 1){
            node.setPrevious(_tail);
            _tail.setNext(node);
        } else {
            _count -= 1;
            push(item); //push increment count and add item.
        }
        _tail = node;
    }

    //SHIFT TO THE LEFT
    public void shift(){
        if(!isEmpty()){
            DoubleElementType<T> temp = _tail;
            _tail = _tail.getPrev();
            _count -= 1;
            if(_count != 0)
                _tail.setNext(null);
            else
                _head = _tail; //null.
        }
    }

    //TOP
    public T top(){
        if(isEmpty())
            return null;
        else
            return _head.getElement();
    }

    //TOP from step.
    public T topFrom(int step){
        if(step == 0)
            return top();
        else if(step < 0)
            return null;
        DoubleElementType<T> temp = _head;
        while(step > 0 && temp != null) {
            temp = temp.getNext();
            step--;
        }
        return (temp == null) ? null : temp.getElement();
    }

    //TAIL from step.
    public T tailFrom(int step){
        if(step == 0)
            return tail();
        else if(step < 0)
            return null;
        DoubleElementType<T> temp = _tail;
        while(step > 0 && temp != null) {
            temp = temp.getPrev();
            step--;
        }
        return (temp == null) ? null : temp.getElement();
    }

    //TAIL
    public T tail(){
        if(isEmpty())
            return null;
        else
            return _tail.getElement();
    }

    //CONTAINS
    public boolean contains(T item){
        DoubleElementType<T> q = _head;
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
        //Make sure that it is Isolation Island.
        this._head = null;
        this._tail = null;
        this._count = 0;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("->[ ");
        DoubleElementType<T> c = _head;
        while(c != null){
            sb.append(c.getElement().toString()+" ");
            c = c.getNext();
        }
        sb.append("]<-");
        return sb.toString();
    }

    @Override
    public Iterator<T> iterator() {
        return new IterS();
    }

    public Iterator<T> reverseIterator(){
        return new ReverseIterS();
    }


    private class IterS implements Iterator<T> {

        DoubleElementType<T> current = _head;

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

    private class ReverseIterS implements Iterator<T> {

        DoubleElementType<T> current = _tail;

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public T next() {
            T el = current.getElement();
            current = current.getPrev();
            return el;
        }
    }
}