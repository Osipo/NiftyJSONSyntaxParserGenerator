package bmstu.iu7m.osipov.structures.trees;

public class VisitorsNextIteration<T> {
    private Node<T> nextNode;

    private int opts = 0;

    public VisitorsNextIteration(Node<T> nextNode){
        this.nextNode = nextNode;
    }
    public VisitorsNextIteration(){
        this(null);
    }

    public void setNextNode(Node<T> nextNode) {
        this.nextNode = nextNode;
    }

    public Node<T> getNextNode() {
        return nextNode;
    }

    public int getOpts() {
        return opts;
    }

    public void setOpts(int opts) {
        this.opts = opts;
    }
}