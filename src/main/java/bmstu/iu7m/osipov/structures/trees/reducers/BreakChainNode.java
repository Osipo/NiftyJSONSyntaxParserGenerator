package bmstu.iu7m.osipov.structures.trees.reducers;

import bmstu.iu7m.osipov.services.grammars.Grammar;
import bmstu.iu7m.osipov.services.lexers.Token;
import bmstu.iu7m.osipov.structures.trees.Action;
import bmstu.iu7m.osipov.structures.trees.LinkedNode;
import bmstu.iu7m.osipov.structures.trees.Node;

import java.util.Set;

public class BreakChainNode implements Action<Node<Token>> {

    @Override
    public void perform(Node<Token> arg) {
        LinkedNode<Token> t = (LinkedNode<Token>) arg;
        LinkedNode<Token> p = t.getParent();

        //remove reduce chains: [parent -> B -> C -> ... -> d/f/g] => [parent -> d/f/g].
        if(p != null && p.getChildren().size() == 1){
            //set token(p) = token(t)
            //remove t from p and add its children (t_children) to p.
            p.setValue(t.getValue());
            p.getChildren().remove(t);
            for(LinkedNode<Token> c : t.getChildren()){
                p.getChildren().add(c);
                c.setParent(p);
            }
            t.setParent(null);
            t.setChildren(null);
        }

        // Recursive chain (from left or right recursion)
        else if(p != null && t.getValue().getName().equals(p.getValue().getName())){

            //Check whether a recursive N is a part or IF or WHILE operator
            //N -> if ( B ) N ELS,  ELS -> else N | empty
            //N -> while ( B ) N
            String ltok = p.getChildren().get(p.getChildren().size() - 1).getValue().getName();
            if(ltok.equals("if") || ltok.equals("while"))//it is not a part of IF or WHILE operator which may produce recursive chain as their bodies are sequence of operators.
                return;

            //delete t from p and add its children (t_children) to p.
            p.getChildren().remove(t);
            for(LinkedNode<Token> c : t.getChildren()){
                p.getChildren().add(c);
                c.setParent(p);
            }
            t.setParent(null);
            t.setChildren(null);
        }
    }
}
