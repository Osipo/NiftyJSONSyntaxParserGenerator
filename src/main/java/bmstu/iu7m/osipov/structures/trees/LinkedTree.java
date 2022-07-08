package bmstu.iu7m.osipov.structures.trees;

import bmstu.iu7m.osipov.structures.lists.LinkedQueue;
import bmstu.iu7m.osipov.structures.lists.LinkedStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class LinkedTree<T> implements Tree<T>, PositionalTree<T> {
    private int _count;
    private LinkedNode<T> _r;
    private SubVisitor<T> _visitor;

    public LinkedTree(){
        _r = new LinkedNode<T>();
        _visitor = new NRSubVisitor<T>();
        _count = 1;
    }

    public LinkedTree(LinkedNode<T> n){
        LinkedNode<T> root = new LinkedNode<>(n);
        root.setParent(null);
        _r = root;
        _visitor = new NRSubVisitor<T>();
        _count = 0;
        Node<Integer> nc = new Node<>(_count); //wrapper node to pass _count by ref.
        __ComputeC(nc);
        _count = nc.getValue();
    }


    private void __ComputeC(Node<Integer> val){
        HashSet<Node<T>> hs = new HashSet<Node<T>>();
        LinkedStack<Node<T>> STACK = new LinkedStack<>();
        Node<T> n;
        STACK.push(root());
        while(!STACK.isEmpty()){
            n = STACK.top();
            if(hs.contains(n)) {
                STACK.pop();
            } else {
                hs.add(n);
                val.setValue(val.getValue() + 1);
                List<Node<T>> children = getChildren(n);
                for(int c = children.size() - 1; c >= 0; c--){
                    STACK.push(children.get(c));
                }
            }
        }
    }


    public void add(T item){
        LinkedNode<T> n = _r;
        while(n.getChildren().size() != 0){
            n = n.getChildren().get(0);// ADD to LEFTMOST_CHILD
        }
        addTo(n, item);
    }

    /*IPositionalTree  implementation*/

    @Override
    public void addTo(Node<T> n, T item){
        LinkedNode<T> p = (LinkedNode<T>) n;
        LinkedNode<T> it = new LinkedNode<T>();
        it.setValue(item);
        it.setParent(p);
        _count+= 1;
        p.getChildren().add(it);
    }

    @Override
    public Node<T> rightMostChild(Node<T> n){
        LinkedNode<T> c = (LinkedNode<T>) n;
        if(c == null || c.getChildren().size() == 0)
            return null;
        return c.getChildren().get(c.getChildren().size() - 1);
    }



    @Override
    public List<Node<T>> getChildren(Node<T> n){
        LinkedNode<T> c = (LinkedNode<T>) n;
        return c.getChildren() == null ? new ArrayList<>() : new ArrayList<>(c.getChildren());
    }

    @Override
    public List<LinkedNode<T>> getRealChildren(Node<T> n){
        LinkedNode<T> c = (LinkedNode<T>) n;
        return c.getChildren() == null ? null : c.getChildren();
    }

    @Override
    public PositionalTree<T> getSubTree(Node<T> n){
        LinkedNode<T> ln = (LinkedNode<T>) n;
        return new LinkedTree<T>(ln);
    }

    @Override
    public void attachTo(Node<T> n, Node<T> parent) {
        LinkedNode<T> ln = (LinkedNode<T>) n;
        LinkedNode<T> lp = (LinkedNode<T>) parent;
        ln.setParent(lp);
        lp.getChildren().add(0, ln);
    }

    @Override
    public void detachNode(Node<T> n) {
        LinkedNode<T> ln = (LinkedNode<T>) n;
        if(ln.getParent() == null)
            return;

        ln.getParent().getChildren().remove(ln);
        ln.setParent(null);
    }

    public void visit(VisitorMode order, Action<Node<T>> act){
        Node<Integer> nc = new Node<>(0);//Some actions MAY MODIFY count of TREE_NODES.
        switch(order){
            case PRE:
                _visitor.preOrder(this,act);
                __ComputeC(nc);
                _count = nc.getValue();
                break;
            case POST:
                _visitor.postOrder(this, act);
                __ComputeC(nc);
                _count = nc.getValue();
                break;
            case IN:
                _visitor.inOrder(this,act);
                __ComputeC(nc);
                _count = nc.getValue();
                break;
            case NONE:
                act.perform(_r);//NONE => perform action on the root.
                __ComputeC(nc);
                _count = nc.getValue();
                break;
            default:
                break;
        }
    }

    public void visit(VisitorMode order, Action2<Node<T>, VisitorsNextIteration<T>> act, VisitorsNextIteration<T> nextItrStrategy){
        Node<Integer> nc = new Node<>(0);//Some actions MAY MODIFY count of TREE_NODES.
        switch(order){
            case PRE:
                _visitor.preOrder(this, act, nextItrStrategy);
                __ComputeC(nc);
                _count = nc.getValue();
                break;
            case POST:
                _visitor.postOrder(this, act, nextItrStrategy);
                __ComputeC(nc);
                _count = nc.getValue();
                break;
            default:
                break;
        }
    }



    @Override
    public <R extends Node<T>> void visitFrom(VisitorMode order, Action<Node<T>> act,R node){
        Node<Integer> nc = new Node<>(0);//Some actions MAY MODIFY count of TREE_NODES.
        switch(order){
            case PRE:
                _visitor.preOrder(this,act,node);
                __ComputeC(nc);
                _count = nc.getValue();
                break;
            case POST:
                _visitor.postOrder(this,act,node);
                __ComputeC(nc);
                _count = nc.getValue();
                break;
            case IN:
                _visitor.inOrder(this,act,node);
                __ComputeC(nc);
                _count = nc.getValue();
                break;
            case NONE:
                act.perform(node);//NONE => perform action on the root.
                __ComputeC(nc);
                _count = nc.getValue();
                break;
            default:
                break;
        }
    }

    @Override
    public <R extends Node<T>> void visitFrom(VisitorMode order, Action2<Node<T>, VisitorsNextIteration<T>> act, R node, VisitorsNextIteration<T> nextIterationStrategy){
        Node<Integer> nc = new Node<>(0);//Some actions MAY MODIFY count of TREE_NODES.
        switch(order){
            case PRE:
                _visitor.preOrder(this,act,node, nextIterationStrategy);
                __ComputeC(nc);
                _count = nc.getValue();
                break;
            case POST:
                _visitor.postOrder(this,act,node, nextIterationStrategy);
                __ComputeC(nc);
                _count = nc.getValue();
                break;
            default:
                break;
        }
    }

    /*Tree implementation*/
    @Override
    public Node<T> root(){
        return _r;
    }

    @Override
    public T value(Node<T> node){
        return node.getValue();
    }

    @Override
    public Node<T> parent(Node<T> node){
        LinkedNode<T> np = (LinkedNode<T>) node;
        if(np == null || np.getParent() == null){
            return null;
        }
        return np.getParent();
    }

    @Override
    public Node<T> leftMostChild(Node<T> node){
        LinkedNode<T> np = (LinkedNode<T>)node;
        if(np == null || np.getChildren() == null || np.getChildren().size() == 0){
            return null;
        }
        return np.getChildren().get(0);
    }

    @Override
    public Node<T> rightSibling(Node<T> node){
        LinkedNode<T> np = (LinkedNode<T>)node;
        if(np == null || np.getParent() == null){
            return null;
        }
        LinkedNode<T> parent = np.getParent();
        List<LinkedNode<T>> c = parent.getChildren();
        int i = 0;//own implementation of LinkedList.
        while(i < c.size() && !c.get(i).equals(np)){
            i++;
        }//or zero.
        if(i >= c.size() - 1)//last element or more.
            return null;
        return c.get(i + 1);
    }

    @Override
    public int getCount(){
        return this._count;
    }

    @Override
    public void clear(){
        _r = null;
        _r = new LinkedNode<T>();
        _count = 0;
    }

    @Override
    public void setVisitor(SubVisitor<T> visitor){
        this._visitor = visitor;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        HashSet<Node<T>> hs = new HashSet<Node<T>>();
        LinkedStack<Node<T>> STACK = new LinkedStack<>();
        Node<T> n;
        STACK.push(root());
        while(!STACK.isEmpty()){
            n = STACK.top();
            if(hs.contains(n)) { //1 -> 2 -> 3, 4;  5.  {1{2{3}{4}}{5}}
                STACK.pop();
                sb.append("}");
            } else {
                hs.add(n);
                sb.append("{" + n.getValue().toString());
                List<Node<T>> children = getChildren(n);
                for(int c = children.size() - 1; c >= 0; c--){
                    STACK.push(children.get(c));
                }
            }
        }
        return sb.toString();
    }

    public String toDot(String fName){
        StringBuilder sb = new StringBuilder();
        HashSet<Node<T>> hs = new HashSet<Node<T>>();
        LinkedStack<Node<T>> STACK = new LinkedStack<>();
        Node<T> n;
        STACK.push(root());
        sb.append("digraph ").append(fName).append(" {");
        while(!STACK.isEmpty()){
            n = STACK.top();
            if(hs.contains(n)) {
                STACK.pop();
            } else {
                hs.add(n);
                String name = n.getIdx()+"";
                String val = n.getValue().toString().replace("\"","\\\"");
                sb.append(name).append(" [label=\"").append(val).append("\"];");
                List<Node<T>> children = getChildren(n);

                int k = children.size() - 1;
                for(int c = 0; c < children.size(); c++){ //direct cycle for str digraph
                    STACK.push(children.get(k - c)); //preserve order of nodes (because of stack)

                    //add directly source node to graph.
                    sb.append(name).append(" -> ").append(children.get(c).getIdx()).append(";");
                }
            }
        }
        sb.append("}");
        return sb.toString();
    }
}
// p {p {f{+}} } {pl {t {b} } }