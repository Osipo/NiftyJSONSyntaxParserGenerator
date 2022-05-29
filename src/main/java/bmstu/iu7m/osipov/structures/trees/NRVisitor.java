package bmstu.iu7m.osipov.structures.trees;


import bmstu.iu7m.osipov.structures.lists.LinkedStack;

//Works fine with IMMUTABLE TREES.
public class NRVisitor<T> implements Visitor<T> {

    protected boolean noCount;

    public NRVisitor(){
        this.noCount = true;
    }

    public void setNoCount(boolean f){
        this.noCount = f;
    }

    public boolean isNoCount(){
        return noCount;
    }

    //MAY PRODUCE ERRORS WHEN TREE IS BEING MODIFIED. (MUTABLE)
    @Override
    public void preOrder(Tree<T> tree, Action<Node<T>> act){
        Node<T> m = tree.root();//ROOT(T)

        if(act == null){
            act = (n) -> System.out.print(tree.value(n).toString()+" ");
        }

        LinkedStack<Node<T>> STACK = new LinkedStack<>();
        long c  = 0;

        while(true){
            if(m != null){
                if(!noCount)
                    c++;
                act.perform(m);//LABEL(node,TREE)
                STACK.push(m);
                m = tree.leftMostChild(m);//LEFTMOST_CHILD(node,TREE)
            }
            else{
                if(STACK.isEmpty()){
                    if(!noCount)
                        System.out.println("Visited: "+c);
                    return;
                }
                m = tree.rightSibling(STACK.top());//RIGHT_SIBLING(TOP(S),TREE) where TOP(S) is node
                STACK.pop();//POP(S)
            }
        }
    }

    @Override
    public  void inOrder(Tree<T> t, Action<Node<T>> act) {
        Node<T> m = t.root();//ROOT(T)

        if(act == null){
            act = (n) -> System.out.print(t.value(n).toString()+" ");
        }

        LinkedStack<Node<T>> STACK = new LinkedStack<>();
        LinkedStack<Node<T>> STACK2 = new LinkedStack<>();
        long c  = 0;

        while(true){
            if(m != null){
                if(!noCount)
                    c++;
                STACK.push(m);
                m = t.leftMostChild(m);//LEFTMOST_CHILD(node,TREE) while current != null current = current.leftson
            }
            else{
                if(STACK.isEmpty()){
                    if(!noCount)
                        System.out.println("Visited: "+c);
                    return;
                }
                //Node<T> c = STACK.Top();
                //1) 1,2 S: 4. 2)4 3 5 8 S: 4. 3) 4 6 10 S: empty.
                act.perform(STACK.top());
                m = t.rightSibling(t.leftMostChild(STACK.top()));//right son of the STACK //current = top.rightson
                STACK.pop();
                if(m != null && t.rightSibling(m) != null){
                    STACK2.push(t.rightSibling(m));//PUSH THIRD CHILD...
                }

                if(STACK.isEmpty() && m == null && !(STACK2.isEmpty())){
                    //STACK.Push(STACK2.Top());
                    //STACK2.Pop();
                    m = STACK2.top();
                    STACK2.pop();
                }
            }
        }
    }


    @Override
    public  void postOrder(Tree<T> tree, Action<Node<T>> act) {
        Node<T> m = tree.root();//ROOT(T)

        if(act == null){
            act = (n) -> System.out.print(tree.value(n).toString()+" ");
        }

        LinkedStack<Node<T>> STACK = new LinkedStack<>();
        long c  = 0;
        while(true){
            if(m != null){
                //act.perform(m);//LABEL(node,TREE)
                STACK.push(m);
                m = tree.leftMostChild(m);//LEFTMOST_CHILD(node,TREE)
            }
            else{
                if(STACK.isEmpty()){
                    if(!noCount)
                        System.out.println("Visited: "+c);
                    return;
                }
                act.perform(STACK.top());
                if(!noCount)
                    c++;
                m = tree.rightSibling(STACK.top());//RIGHT_SIBLING(TOP(S),TREE) where TOP(S) is node
                STACK.pop();//POP(S)
            }
        }
    }

    //SAME METHODS WITH Action (2nd param) with 2 ARGS, WHERE 2 ARG IS NextIterationStrategy. (3rd param of these methods)
    @Override
    public void preOrder(Tree<T> tree, Action2<Node<T>, VisitorsNextIteration<T>> act, VisitorsNextIteration<T> nextItrStrategy){
        Node<T> m = tree.root();//ROOT(T)

        if(act == null){
            act = (n, nextItr) -> System.out.print(tree.value(n).toString()+" ");
        }

        LinkedStack<Node<T>> STACK = new LinkedStack<>();
        long c  = 0;

        while(true){
            if(m != null){
                if(!noCount)
                    c++;

                act.perform(m, nextItrStrategy);//LABEL(node,TREE)
                STACK.push(m);
                if(nextItrStrategy.getOpts() == 0) {
                    m = tree.leftMostChild(m);//LEFTMOST_CHILD(node,TREE)
                }
                else{
                    m = null;
                }
            }
            else{
                if(STACK.isEmpty()){
                    if(!noCount)
                        System.out.println("Visited: "+c);
                    return;
                }

                if(nextItrStrategy.getOpts() == 0) {
                    m = tree.rightSibling(STACK.top());//RIGHT_SIBLING(TOP(S),TREE) where TOP(S) is node
                }
                STACK.pop();//POP(S)
            }
        }
    }


    @Override
    public  void postOrder(Tree<T> tree, Action2<Node<T>, VisitorsNextIteration<T>> act, VisitorsNextIteration<T> nextItrStrategy) {
        Node<T> m = tree.root();//ROOT(T)

        if(act == null){
            act = (n, nextItr) -> System.out.print(tree.value(n).toString()+" ");
        }

        LinkedStack<Node<T>> STACK = new LinkedStack<>();
        long c  = 0;
        while(true){
            if(m != null){
                STACK.push(m);
                m = tree.leftMostChild(m);
            }
            else{
                if(STACK.isEmpty()){
                    if(!noCount)
                        System.out.println("Visited: "+c);
                    return;
                }
                act.perform(STACK.top(), nextItrStrategy);

                if(!noCount)
                    c++;

                if(nextItrStrategy.getNextNode() != null){
                    m = nextItrStrategy.getNextNode();
                    nextItrStrategy.setNextNode(null);
                    continue;
                }
                else if(nextItrStrategy.getOpts() == 0) {
                    m = tree.rightSibling(STACK.top());//RIGHT_SIBLING(TOP(S),TREE) where TOP(S) is node
                }
                else if(nextItrStrategy.getOpts() == 2){
                    m = tree.rightSibling(tree.rightSibling(STACK.top())); //skip first right sibling (get second right)
                    nextItrStrategy.setOpts(0);
                }
                else if(nextItrStrategy.getOpts() == 3){
                    nextItrStrategy.setOpts(0);
                    STACK.top().setValue(null);
                    tree.detachNode(STACK.top());
                }
                STACK.pop();//POP(S)
            }
        }
    }
}