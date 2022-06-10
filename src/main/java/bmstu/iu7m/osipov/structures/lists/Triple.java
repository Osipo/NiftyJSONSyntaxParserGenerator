package bmstu.iu7m.osipov.structures.lists;

import bmstu.iu7m.osipov.structures.graphs.Pair;

public class Triple<T1, T2, T3> {
    private T1 v1;
    private T2 v2;
    private T3 v3;

    public Triple(T1 v1, T2 v2, T3 v3){
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
    }

    public T1 getV1(){
        return v1;
    }

    public T2 getV2(){
        return v2;
    }

    public T3 getV3(){
        return v3;
    }

    @Override
    public boolean equals(Object o){
        if(o == null)
            return false;
        Triple<T1,T2, T3> p = null;
        try{
            p = (Triple<T1, T2, T3>)o;
        }
        catch (ClassCastException e){
            return false;
        }
        return p.getV1().equals(v1) && p.getV2().equals(v2) && p.getV3().equals(v3);
    }

    @Override
    public int hashCode(){
        String b = getV1().toString() +
                getV2().toString() + getV3().toString();
        return b.hashCode();
    }

    @Override
    public String toString(){
        return "( " + v1.toString() + ", " + v2.toString() + ", " + v3.toString() + ")";
    }
}
