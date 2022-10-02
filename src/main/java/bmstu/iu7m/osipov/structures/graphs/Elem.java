package bmstu.iu7m.osipov.structures.graphs;

import java.util.Objects;

public class Elem<T> {
    private T v1;

    public Elem(T v){
        this.v1 = v;
    }

    public void setV1(T v1) {
        this.v1 = v1;
    }

    public T getV1() {
        return v1;
    }

    @Override
    public String toString(){
        return v1.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Elem)) return false;
        Elem<?> elem = (Elem<?>) o;
        return v1.equals(elem.v1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(v1);
    }
}
