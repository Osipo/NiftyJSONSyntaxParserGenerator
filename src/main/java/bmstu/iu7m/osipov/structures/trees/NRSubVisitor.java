package bmstu.iu7m.osipov.structures.trees;

import bmstu.iu7m.osipov.structures.lists.LinkedStack;

public class NRSubVisitor<T> extends NRVisitor<T> implements SubVisitor<T> {
    @Override
    public void preOrder(Tree<T> tree, Action<Node<T>> act, Node<T> node){
        Node<T> m = node;
        Node<T> subRoot = m;

        if(act == null){
            act = (n) -> System.out.print(tree.value(n).toString()+" ");
        }

        LinkedStack<Node<T>> STACK = new LinkedStack<>();
        long c  = 0;
        if(m == null)
            return;

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
                if(STACK.top().getIdx() == subRoot.getIdx()){ //only till SubRoot.
                    STACK.pop();
                    return;
                }
                m = tree.rightSibling(STACK.top());//RIGHT_SIBLING(TOP(S),TREE) where TOP(S) is node
                STACK.pop();//POP(S)
            }
        }
    }

    @Override
    public void inOrder(Tree<T> t, Action<Node<T>> act, Node<T> node) {
        Node<T> m = node;

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
    public void postOrder(Tree<T> tree, Action<Node<T>> act, Node<T> node) {
        Node<T> m = node;
        Node<T> subRoot = m;
        if(act == null){
            act = (n) -> System.out.print(tree.value(n).toString()+" ");
        }

        LinkedStack<Node<T>> STACK = new LinkedStack<>();

        if(m == null)
            return;

        long c  = 0;
        while(true){
            if(m != null){
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
                if(STACK.top().getIdx() == subRoot.getIdx()){
                    STACK.pop();
                    return;
                }
                m = tree.rightSibling(STACK.top());//RIGHT_SIBLING(TOP(S), TREE) where TOP(S) is node
                STACK.pop();//POP(S)
            }
        }
    }


    //SAME METHODS WITH Action (2nd param) with 2 ARGS, WHERE 2 ARG IS NextIterationStrategy. (4th param of these methods)
    @Override
    public void preOrder(Tree<T> tree, Action2<Node<T>, VisitorsNextIteration<T>> act, Node<T> node, VisitorsNextIteration<T> nextItr){
        Node<T> m = node;
        Node<T> subRoot = m;

        if(act == null){
            act = (n, itr) -> System.out.print(tree.value(n).toString()+" ");
        }

        LinkedStack<Node<T>> STACK = new LinkedStack<>();
        long c  = 0;
        if(m == null)
            return;

        while(true){
            if(m != null){
                if(!noCount)
                    c++;
                act.perform(m, nextItr);
                STACK.push(m);
                m = tree.leftMostChild(m);//LEFTMOST_CHILD(node,TREE)
            }
            else{
                if(STACK.isEmpty()){
                    if(!noCount)
                        System.out.println("Visited: "+c);
                    return;
                }
                if(STACK.top().getIdx() == subRoot.getIdx()){ //only till SubRoot.
                    STACK.pop();
                    return;
                }

                if(nextItr.getOpts() == 0) {
                    m = tree.rightSibling(STACK.top());//RIGHT_SIBLING(TOP(S),TREE) where TOP(S) is node
                }
                STACK.pop();//POP(S)
            }
        }
    }

    @Override
    public void postOrder(Tree<T> tree, Action2<Node<T>, VisitorsNextIteration<T>> act, Node<T> node, VisitorsNextIteration<T> nextItr) {
        Node<T> m = node;
        Node<T> subRoot = m;
        if(act == null){
            act = (n, itr) -> System.out.print(tree.value(n).toString()+" ");
        }

        LinkedStack<Node<T>> STACK = new LinkedStack<>();

        if(m == null)
            return;

        long c  = 0;
        while(true){
            if(m != null){
                STACK.push(m);
                m = tree.leftMostChild(m);//LEFTMOST_CHILD(node,TREE)
            }
            else{
                if(STACK.isEmpty()){
                    if(!noCount)
                        System.out.println("Visited: "+c);
                    return;
                }
                act.perform(STACK.top(), nextItr);
                if(!noCount)
                    c++;


                if(STACK.top().getIdx() == subRoot.getIdx()){
                    STACK.pop();
                    return;
                }

                if(nextItr.getOpts() == 0){
                    m = tree.rightSibling(STACK.top());//RIGHT_SIBLING(TOP(S), TREE) where TOP(S) is node
                }
                STACK.pop();//POP(S)
            }
        }
    }

}