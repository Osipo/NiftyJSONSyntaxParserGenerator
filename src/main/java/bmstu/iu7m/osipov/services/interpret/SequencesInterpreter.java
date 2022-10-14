package bmstu.iu7m.osipov.services.interpret;

import bmstu.iu7m.osipov.services.grammars.AstSymbol;
import bmstu.iu7m.osipov.structures.graphs.Elem;
import bmstu.iu7m.osipov.structures.lists.LinkedDeque;
import bmstu.iu7m.osipov.structures.lists.LinkedList;
import bmstu.iu7m.osipov.structures.lists.LinkedStack;
import bmstu.iu7m.osipov.structures.trees.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

//Interpret sequences.
public class SequencesInterpreter {
    private Node<AstSymbol> exprRoot;
    private Node<AstSymbol> sequencesRoot;
    private Env context;
    private PositionalTree<AstSymbol> ast;

    private LinkedStack<Object> exp;
    private LinkedStack<List<Elem<Object>>> lists;
    private ArrayList<List<Elem<Object>>> indices;
    private LinkedStack<FunctionInterpreter> functions;
    private LinkedStack<ArrayList<Object>> args;
    private LinkedStack<SequencesInterpreter> matrices;
    private LinkedList<Elem<?>> outer_vector;
    private Map<String, Integer> vector_idx;

    private BaseInterpreter parentInter;

    public SequencesInterpreter(PositionalTree<AstSymbol> ast,
                                Node<AstSymbol> exprRoot,
                                Node<AstSymbol> seqRoot,
                                Env context,
                                LinkedStack<Object> exp,
                                LinkedStack<List<Elem<Object>>> lists,
                                ArrayList<List<Elem<Object>>> indices,
                                LinkedStack<FunctionInterpreter> functions,
                                LinkedStack<ArrayList<Object>> args,
                                LinkedStack<SequencesInterpreter> matrices,
                                BaseInterpreter parentInterpreter,
                                LinkedList<Elem<?>> outer_vector,
                                Map<String, Integer> vector_idx
    )
    {
        this.exprRoot = exprRoot;
        this.sequencesRoot = seqRoot;
        this.context = context;
        this.ast = ast;

        this.exp = exp;
        this.lists = lists;
        this.indices = indices;
        this.functions = functions;
        this.args = args;
        this.parentInter = parentInterpreter;
        this.matrices = matrices;
        this.outer_vector = outer_vector;
        this.vector_idx = vector_idx;
    }

    public void generateItems(List<Elem<Object>> parentList, LinkedStack<ArrayList<Variable>> accs, LinkedStack<Boolean> is_acc_items) throws Exception {

        //Collect all sequence items.
        VisitorsNextIteration<AstSymbol>  nextItr = new VisitorsNextIteration<>();

        LinkedList<String> vnames = new LinkedList<String>();

        Map<String, Integer> vnames_idxs = new HashMap<>();
        AtomicReference<Integer> i = new AtomicReference<>();
        AtomicReference<Integer> j = new AtomicReference<>();
        i.set(1);
        j.set(0);

        if(this.vector_idx != null){
            vnames_idxs.putAll(this.vector_idx);
            i.set(1 + this.vector_idx.size());
            j.set(this.vector_idx.size()); //from outer vector.
        }

        LinkedDeque<LinkedList<Elem<?>>> MATRIX_DATA = new LinkedDeque<>();
        MATRIX_DATA.push(new LinkedList<>()); //EMPTY LIST.

        AtomicReference<MatrixValues> matrix = new AtomicReference<>();
        AtomicReference<Env> a_context = new AtomicReference<>(this.context);

        //1. At first extract data from sequence and match variable names (vnames) to indices of the vectors.
        ast.visitFrom(VisitorMode.POST, (c, next) -> {
            String nodeType = c.getValue().getType();
            String nodeVal = c.getValue().getValue();
            switch (nodeType){
                case "seqitem": {
                    vnames.add(nodeVal); //name of seqitem.
                    //CHECK Source brother.
                    if(ast.rightSibling(c).getValue().getType().equals("operator")){
                        Node<AstSymbol> subTree = ast.rightSibling(c);
                        ast.visitFrom(VisitorMode.POST, (sc, sni) -> {
                            try {
                                parentInter.applyOperation(
                                        ast,
                                        a_context,
                                        sc,
                                        this.exp,
                                        this.lists,
                                        this.indices,
                                        null,
                                        this.functions,
                                        this.args,
                                        sni,
                                        null,
                                        null,
                                        -1,
                                        matrices,
                                        false
                                );
                            } catch (Exception e){
                                System.err.println(e.getMessage());
                                sni.setOpts(-1);
                            }
                        }, subTree, nextItr); //til parent of start

                        //do not traverse through processed nodes.
                        if(next.getOpts() != -1){
                            nextItr.setNextNode(subTree);
                            nextItr.setOpts(15); //move to list/items node (see next case "list") and process it as it leaf.
                        }
                    } //END CHECK BROTHER.
                    break;
                } //end seqitem.

                case "variable": {
                    Variable data = context.get(nodeVal, v -> v.getItems() != null);
                    if(data == null){
                        nextItr.setOpts(-1);
                        System.err.println("Variable '" + nodeVal + "' is not a sequence!");
                        return;
                    }

                    while(vnames.size() > 0){
                        String item_name = vnames.get(1);
                        vnames.removeAt(1); //delete from begining.
                        vnames_idxs.put(item_name, i.get());

                        Iterator<LinkedList<Elem<?>>> snapshot = MATRIX_DATA.reverseIterator(); //get current SNAPSHOT.
                        MATRIX_DATA.clear(); //remove data as we have snapshot.
                        while(snapshot.hasNext()){
                            LinkedList<Elem<?>> old_vector_i = snapshot.next();
                            for(Elem<?> item : data.getItems()){
                                LinkedList<Elem<?>> new_vector_i = new LinkedList<>();
                                new_vector_i.addAll(old_vector_i);
                                new_vector_i.add(new Elem<>(item)); //item in item.
                                MATRIX_DATA.push(new_vector_i);
                            } //end for
                        } //end inner while

                        i.set(i.get() + 1);
                    } // end while.
                    break;
                } //end variable

                case "start": {
                        Node<AstSymbol> subTree =
                                (PositionalTreeUtils.hasParentThat(ast, c, (cv) -> cv.getType().equals("call")) ?
                                PositionalTreeUtils.getParentThat(ast, c, (cv) -> cv.getType().equals("call")) :
                                ast.parent(c));
                        //System.out.println(subTree.getValue());
                        ast.visitFrom(VisitorMode.POST, (sc, sni) -> {
                            try {
                                parentInter.applyOperation(
                                        ast,
                                        a_context,
                                        sc,
                                        this.exp,
                                        this.lists,
                                        this.indices,
                                        null,
                                        this.functions,
                                        this.args,
                                        sni,
                                        null,
                                        null,
                                        -1,
                                        matrices,
                                        false
                                );
                            } catch (Exception e){
                                System.err.println(e.getMessage());
                                sni.setOpts(-1);
                            }
                        }, subTree, nextItr); //til parent of start

                        //do not traverse through processed nodes.
                        if(next.getOpts() != -1){
                            nextItr.setNextNode(subTree);
                            nextItr.setOpts(15); //move to list/items node (see next case "list") and process it as it leaf.
                        }
                        break;
                } //end start of the list.

                case "list": case "call": case "operator": {
                    List<Elem<Object>> data = null;
                    if(c.getValue().getType().equals("call") || c.getValue().getType().equals("operator")){ //function call.
                        if(this.exp.top() instanceof List) {
                            data = (List<Elem<Object>>) this.exp.top();
                            this.exp.pop();
                        }
                    }
                    else {
                        data = this.lists.top();
                        this.lists.pop();
                    }

                    if(data == null){
                        nextItr.setOpts(-1);
                        System.err.println("Expression at sequence generator is not a list!");
                        return;
                    }

                    while(vnames.size() > 0){
                        String item_name = vnames.get(1);
                        vnames.removeAt(1); //delete from begining.
                        vnames_idxs.put(item_name, i.get());

                        Iterator<LinkedList<Elem<?>>> snapshot = MATRIX_DATA.reverseIterator(); //get current SNAPSHOT.
                        MATRIX_DATA.clear(); //remove data as we have snapshot.
                        while(snapshot.hasNext()){
                            LinkedList<Elem<?>> old_vector_i = snapshot.next();
                            for(Elem<?> item : data){
                                LinkedList<Elem<?>> new_vector_i = new LinkedList<>();
                                new_vector_i.addAll(old_vector_i);
                                new_vector_i.add(new Elem<>(item)); //item in item.
                                MATRIX_DATA.push(new_vector_i);
                            } //end for
                        } //end inner while

                        i.set(i.get() + 1);
                    } // end while.
                    break;
                } //end list.

                //root node reached.
                case "sequences": {
                    matrix.set(new MatrixValues(vnames_idxs, MATRIX_DATA));
                    break;
                }
            } //end switch

        }, sequencesRoot, nextItr);

        //System.out.println("I = " + i.get());
        //System.err.println("data = " + MATRIX_DATA);

        //3. now compute expressions based on matrix AND add them to list.
        Iterator<LinkedList<Elem<?>>> vectors = matrix.get().getDATA().reverseIterator();
        int expr_len = ast.getChildren(exprRoot).size() - 1; //length of vector except start/node.

        String matrixType = ast.parent(exprRoot).getValue().getType(); //matrix or reduce node.

        boolean isReduce = matrixType.equals("reduce");

        Node<AstSymbol> se = ast.leftMostChild(exprRoot); //skip start/vector node while processing vectors
        ast.detachNode(se);

        //System.out.println("Matrix len = " + matrix.get().getDATA().size());
        //System.out.println(matrix.get().getDATA());
        while(vectors.hasNext()){
            LinkedList<Elem<?>> vector_i = vectors.next();
            if(vector_i.size() == 0)
                continue;
            if(j.get() != 0){ //inner vector expression generator  like [gen [gen [gen... for k in iii end] for j in ii end] for x in i end]
                int offset = j.get();
                while(offset > 0){
                    vector_i.add(1, outer_vector.get(offset)); //add from outer vector. Note: get from vector only it can not be found in current context.
                    offset--;
                }
            }
            //System.out.println("apply: " + vector_i);

            //parse expression.
            //for each row computes vector expression
            ast.visitFrom(VisitorMode.POST, (c, next) -> {
                try {
                    parentInter.applyOperation(
                            ast,
                            a_context,
                            c,
                            this.exp,
                            this.lists,
                            this.indices,
                            null,
                            this.functions,
                            this.args,
                            nextItr,
                            vector_i,
                            matrix.get().getNameIndices(),
                            expr_len,
                            matrices,
                            isReduce
                    );
                } catch (Exception e){
                    System.err.println(e.getMessage());
                    nextItr.setOpts(-1);
                }
            }, exprRoot, nextItr);
        } //end vectors

        //if reduce operation with accumulators.
        //attach them to the list
        List<Variable> accs_in = (accs == null) ? null : accs.top();

        if(accs_in != null && accs_in.size() > 0 && isReduce){
            for(int ii = 0; ii < accs_in.size(); ii++)
                parentList.add(new Elem<>(TypeChecker.CheckValue(accs_in.get(ii), null)));
            accs_in.clear(); //flush list of accumulators.
            accs.pop(); //remove inner reduce operator.
            is_acc_items.pop(); //pop false item.
        }

        ast.attachTo(se, exprRoot);
    }
}