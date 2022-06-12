package bmstu.iu7m.osipov.services.interpret;

import bmstu.iu7m.osipov.structures.graphs.Elem;
import bmstu.iu7m.osipov.structures.lists.LinkedDeque;
import bmstu.iu7m.osipov.structures.lists.LinkedList;
import bmstu.iu7m.osipov.structures.lists.LinkedStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatrixValues {
    private Map<String, Integer> vnames_idxs;

    //LinkedStack replaced with LinkedDeque.
    private LinkedDeque<LinkedList<Elem<?>>> DATA;


    //LinkedStack replaced with LinkedDeque.
    public MatrixValues(Map<String, Integer> vnames_idxs, LinkedDeque<LinkedList<Elem<?>>> data){
        this.vnames_idxs = vnames_idxs;
        this.DATA = data;
        /*
        //reverse STACK.
        this.DATA = new LinkedStack<>();
        while(!data.isEmpty()){
            this.DATA.push(data.top());
            data.pop();
        }
        */
    }

    public Map<String, Integer> getNameIndices() {
        return vnames_idxs;
    }

    public LinkedDeque<LinkedList<Elem<?>>> getDATA(){
        return DATA;
    }
}
