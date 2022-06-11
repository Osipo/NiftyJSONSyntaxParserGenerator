package bmstu.iu7m.osipov.services.interpret;

import bmstu.iu7m.osipov.structures.graphs.Elem;
import bmstu.iu7m.osipov.structures.lists.LinkedList;
import bmstu.iu7m.osipov.structures.lists.LinkedStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatrixValues {
    private Map<String, Integer> vnames_idxs;

    private LinkedStack<LinkedList<Elem<?>>> DATA;

    public MatrixValues(Map<String, Integer> vnames_idxs, LinkedStack<LinkedList<Elem<?>>> data){
        this.vnames_idxs = vnames_idxs;
        this.DATA = data;
    }

    public Map<String, Integer> getNameIndices() {
        return vnames_idxs;
    }

    public LinkedStack<LinkedList<Elem<?>>> getDATA(){
        return DATA;
    }
}
