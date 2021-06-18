package bmstu.iu7m.osipov.structures.trees;

import bmstu.iu7m.osipov.structures.lists.LinkedStack;

//Works fine with IMMUTABLE Binary Search Trees.
public class BinaryNRVisitor<T> extends NRSubVisitor<T> implements Visitor<T>, SubVisitor<T> {

    //SYMMETRIC BSTREE TRAVERSAL WITHOUT RECURSION
    @Override
    public void inOrder(Tree<T> tree, Action<Node<T>> act){
        LinkedBinaryNode<T> m1 = (LinkedBinaryNode<T>) tree.root();
        if(act == null){
            act = (n) -> System.out.println(tree.value(n).toString()+" ");
        }

        LinkedStack<Node<T>> STACK = new LinkedStack<>();
        long c = 0;
        while(true){
            if(m1 != null){
                STACK.push(m1);
                m1 = m1.getLeft() != null ? (LinkedBinaryNode<T>) tree.leftMostChild(m1) : null;//LEFTMOST_CHILD(node,TREE1)
            }
            else{
                if(STACK.isEmpty()){
                    if(!noCount)
                        System.out.println("Visited: "+c);
                    return;
                }
                act.perform(STACK.top());//LABEL(node1) on TREE1 AND TREE2
                if(!noCount)
                    c++;
                Node<T> c1 = STACK.top();
                STACK.pop();
                m1 =  ((LinkedBinaryNode<T>) c1).getRight();
            }
        }
    }
}
