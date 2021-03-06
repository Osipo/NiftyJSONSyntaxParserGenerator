package bmstu.iu7m.osipov.structures.graphs;

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
}
