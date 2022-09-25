package bmstu.iu7m.osipov.structures.hashtables;

import bmstu.iu7m.osipov.services.interpret.Variable;
import bmstu.iu7m.osipov.structures.trees.BinarySearchTree;
import bmstu.iu7m.osipov.structures.trees.PrintlnAction;
import bmstu.iu7m.osipov.structures.trees.VisitorMode;
import bmstu.iu7m.osipov.utils.StringContainerComparator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class STable implements Iterable<Variable> {
    private ArrayList<BinarySearchTree<Variable>> table;

    private final int HASH_MIN;
    private final int HASH_MAX;

    private List<Variable> added;

    public STable(){
        this(0,65535 * 2);
    }

    public STable(int hmin, int hmax){
        this.HASH_MIN = hmin;
        this.HASH_MAX = hmax;
        this.added = new ArrayList<>(64);
        init();
    }

    public void clear(){
        int l = HASH_MAX - HASH_MIN + 1;
        for(int i = 0; i < l; i++){
            this.table.set(i, null);
        }
        this.table = null;
        //System.out.println("Table was clear.");
    }

    public void init(){
        int l = HASH_MAX - HASH_MIN + 1;
        this.table = new ArrayList<>(l);
        for(int i = 0; i < l; i++){
            this.table.add(null);
        }
        //System.out.println("Table was initiated with "+table.size()+" null elements.");
    }

    public boolean add(Variable entry){
        boolean isAdded = false;
        int h = hash(entry.getValue());
        BinarySearchTree<Variable> rec = table.get(h);
        if(rec == null){
            rec = new BinarySearchTree<Variable>(new StringContainerComparator<Variable>());
            rec.add(entry);
            this.table.set(h, rec);
            isAdded = true;
        }
        else{
            isAdded = rec.add(entry);
        }
        if(isAdded)
            this.added.add(entry);
        return isAdded;
    }

    public void override(Variable entry){
        int h = hash(entry.getValue());
        BinarySearchTree<Variable> rec = table.get(h);
        if(rec == null) //not found => add new variable
            add(entry);
        else {
            this.added.remove(get(entry.getValue())); //remove old variable from iterator.
            rec.override(entry); //put new variable to tree and erase old variable.
            this.added.add(entry); //and add new variable to iterator.
        }
    }

    public int size(){
        return this.added.size();
    }

    public boolean add(String s){
        int h = hash(s);
        boolean isAdded = false;
        BinarySearchTree<Variable> rec = table.get(h);
        if(rec == null){
            rec = new BinarySearchTree<Variable>(new StringContainerComparator<Variable>());
            Variable v_i = new Variable(s);
            rec.add(v_i);
            this.table.set(h, rec);
            this.added.add(v_i);
            isAdded = true;
        }
        else{
            Variable v_i = new Variable(s);
            isAdded = rec.add(v_i);
            if(isAdded)
                this.added.add(v_i);
        }
        return isAdded;
    }

    public void printTable(){
        for(BinarySearchTree<Variable> t : table){
            if(t != null)
                t.visit(VisitorMode.IN,new PrintlnAction<Variable>());
        }
    }

    public Variable get(String s){
        Variable it = new Variable(s);
        int h = hash(s);
        BinarySearchTree<Variable> rec = table.get(h);
        if(rec == null)
            return null;//no record in hashTable.
        return rec.get(it);//returns Variable from tree. (not from variable it!).
    }

    private int hash(String s){

        int l = (s.length() ) / 2;
        int result = ((int)s.charAt(0) + (int)s.charAt(l) - HASH_MIN) % (HASH_MAX - HASH_MIN + 1) + HASH_MIN;
        if(result < HASH_MIN)
            result = HASH_MIN;
        return result;
    }

    @Override
    public Iterator<Variable> iterator() {
        return this.added.iterator();
    }
}