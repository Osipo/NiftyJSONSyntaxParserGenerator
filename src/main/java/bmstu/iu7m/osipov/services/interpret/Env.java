package bmstu.iu7m.osipov.services.interpret;

import bmstu.iu7m.osipov.structures.hashtables.STable;

import java.util.function.Predicate;

public class Env {
    protected Env prev;
    protected Env next;
    protected STable table;

    public Env(Env p){
        this.prev = p;
        this.table = new STable();
        if(p != null)
            p.next = this;
    }

    public Env getPrev() {
        return prev;
    }

    public Env getNext() {
        return next;
    }

    public void add(Variable v){
        this.table.add(v);
    }

    //Bottom_up context
    public Variable get(String s){
        for(Env e = this; e != null; e = e.prev){
            Variable found = e.table.get(s);
            if(found != null)
                return found;
        }
        return null;
    }

    public Variable get(String s, Predicate<Variable> condition){
        for(Env e = this; e != null; e = e.prev){
            Variable found = e.table.get(s);
            if(found != null && condition.test(found))
                return found;
        }
        return null;
    }

    //Top_down (into module) context.
    public Variable getOn(String s){
        for(Env e = this; e != null; e = e.next){
            Variable found = e.table.get(s);
            if(found != null)
                return found;
        }
        return null;
    }
    public Variable getOn(String s, Predicate<Variable> condition){
        for(Env e = this; e != null; e = e.next){
            Variable found = e.table.get(s);
            if(found != null && condition.test(found))
                return found;
        }
        return null;
    }

    public Variable getAtCurrent(String s){
        return this.table.get(s);
    }

    public Variable getAtCurrent(String s, Predicate<Variable> condition){
        Variable v = this.table.get(s);
        return (v != null && condition.test(v)) ? v : null;
    }
}
