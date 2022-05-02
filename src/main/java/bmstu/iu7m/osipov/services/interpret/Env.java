package bmstu.iu7m.osipov.services.interpret;

import bmstu.iu7m.osipov.structures.hashtables.STable;

public class Env {
    protected Env prev;
    protected STable table;

    public Env(Env p){
        this.prev = p;
        this.table = new STable();
    }

    public Env getPrev() {
        return prev;
    }

    public void add(Variable v){
        this.table.add(v);
    }

    public Variable get(String s){
        for(Env e = this; e != null; e = e.prev){
            Variable found = e.table.get(s);
            if(found != null)
                return found;
        }
        return null;
    }
}
