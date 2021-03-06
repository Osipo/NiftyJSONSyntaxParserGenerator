package bmstu.iu7m.osipov.services.interpret.optimizers;

import bmstu.iu7m.osipov.services.grammars.AstNode;
import bmstu.iu7m.osipov.services.grammars.AstSymbol;
import bmstu.iu7m.osipov.structures.lists.LinkedStack;
import bmstu.iu7m.osipov.structures.trees.*;

import java.util.ArrayList;
import java.util.List;

//1. replace single expressions functions with block (program/prog node)
//2. eliminate tail recursive call.

public class FunctionOptimizer {

    private int l_id = 0;

    public void optimize(PositionalTree<AstSymbol> ast){
        VisitorsNextIteration<AstSymbol> nextItr = new VisitorsNextIteration<>();
        List<FunctionEntry> funs = new ArrayList<>();

        //get all function nodes and its parameters.
        ast.visit(VisitorMode.PRE, (n, next) -> {
            if(n.getValue().getType().equals("lambda")) {
                String fName =
                        ast.parent(n).getValue().getType().equals("assign") ?
                                ast.rightSibling(n).getValue().getValue() //variable_name => name of function.
                                : null; //anonymous function

                funs.add(new FunctionEntry(fName, n));
            }

            //parameters of lambda.
            else if(ast.parent(n) != null && ast.parent(n).getValue().getType().equals("params")){
                funs.get(funs.size() - 1).getParamNames().add(n.getValue().getValue());
            }
        }, nextItr);

        int last_id = ast.getCount() + 1;

        //replace single expression functions with block.
        //to attach label node later to expression.
        for(FunctionEntry f: funs){
            Node<AstSymbol> fn = f.getBody();
            LinkedNode<AstSymbol> f_body = ast.getRealChildren(fn).get(1); //second child.
            if(!f_body.getValue().getType().equals("program")){

                LinkedNode<AstSymbol> n_body = new LinkedNode<>();
                n_body.setValue(new AstNode("program", "prog")); //program/prog
                n_body.setIdx(last_id++);

                LinkedNode<AstSymbol> n_body_start = new LinkedNode<>();
                n_body_start.setValue(new AstNode("start", "prog")); //start/prog
                n_body_start.setIdx(last_id++);

                n_body.getChildren().add(n_body_start); //attach start to nbody
                n_body_start.setParent(n_body);

                n_body.getChildren().add(f_body); //move f_body node  to nbody and replace parent ref.
                f_body.setParent(n_body);

                //detach f_body from old parent f and attach n_body.
                ast.getRealChildren(fn).remove(f_body);
                ast.getRealChildren(fn).add(n_body);
                n_body.setParent((LinkedNode<AstSymbol>) fn);
                f.setBody(n_body);
            }
            else
                f.setBody(f_body);
        } //end for.


        //for each function.
        for(FunctionEntry f : funs){
            Node<AstSymbol> call_node = PositionalTreeUtils.rightMostLeafOf(ast, f.getBody(), (x) -> x.getType().equals("call"));
            if(ast.getChildren(call_node).size() == 1
                    && call_node.getValue().getValue().equals(f.getfName())
                    && !(
                            ast.parent(call_node).getValue().getType().equals("operator")
                        ||  ast.parent(call_node).getValue().getType().equals("relop")
                        ||  ast.parent(call_node).getValue().getType().equals("boolop")
                    )
            )
            {
                Node<AstSymbol> args = ast.getChildren(call_node).get(0);
                Node<AstSymbol> call_parent = ast.parent(call_node);

                int IdxOfCall = PositionalTreeUtils.indexOfChild(ast, call_parent, call_node);
                String tempVar = "";

                LinkedStack<LinkedNode<AstSymbol>> REVERSE_ARGS = new LinkedStack<>();
                for(int i = 0, j = 1; i < f.getParamNames().size(); i++, j++){
                    LinkedNode<AstSymbol> assign_node = new LinkedNode<>();
                    assign_node.setValue(new AstNode("assign", "="));
                    assign_node.setIdx(last_id++);

                    tempVar = "$_" + last_id; //1, 3, 5.
                    LinkedNode<AstSymbol> var_node = new LinkedNode<>();
                    var_node.setValue(new AstNode("variable", tempVar));
                    var_node.setIdx(last_id++);

                    LinkedNode<AstSymbol> exp_node = ast.getRealChildren(args).get(j); //argument expression

                    exp_node.setParent(assign_node);
                    var_node.setParent(assign_node);
                    assign_node.getChildren().add(exp_node);
                    assign_node.getChildren().add(var_node); //tempVar = expr

                    //attach assign node before call_node.
                    assign_node.setParent((LinkedNode<AstSymbol>) call_parent);

                    /*
                    //evaluate at temps to remove side effect of evaluation.
                    LinkedNode<AstSymbol> assing_node2 = new LinkedNode<>();
                    assing_node2.setValue(new AstNode("assign", "="));
                    assing_node2.setIdx(last_id++);
                    LinkedNode<AstSymbol> tempvar_node = new LinkedNode<>();
                    tempvar_node.setValue(new AstNode("variable", tempVar));
                    tempvar_node.setIdx(last_id++);
                    LinkedNode<AstSymbol> var_node2 = new LinkedNode<>();
                    var_node2.setValue(new AstNode("variable", f.getParamNames().get(i)));
                    var_node2.setIdx(last_id++);

                    var_node2.setParent(assing_node2);
                    tempvar_node.setParent(assing_node2);
                    assing_node2.getChildren().add(tempvar_node);
                    assing_node2.getChildren().add(var_node2); //variable = expr

                    assing_node2.setParent((LinkedNode<AstSymbol>) call_parent);
                    */
                    REVERSE_ARGS.push(assign_node);
                    //REVERSE_ARGS.push(assing_node2); //t = exp, v = t.
                }

                int plast_id = last_id - 1;
                for(int i = 1; i < f.getParamNames().size(); i++)
                    plast_id = plast_id - 2;

                for(int i = 0, j = 1; i < f.getParamNames().size(); i++, j++){

                    tempVar = "$_" + plast_id;

                    plast_id = plast_id + 2;

                    //evaluate at temps to remove side effect of evaluation.
                    LinkedNode<AstSymbol> assing_node2 = new LinkedNode<>();
                    assing_node2.setValue(new AstNode("assign", "="));
                    assing_node2.setIdx(last_id++);
                    LinkedNode<AstSymbol> tempvar_node = new LinkedNode<>();
                    tempvar_node.setValue(new AstNode("variable", tempVar));
                    tempvar_node.setIdx(last_id++);
                    LinkedNode<AstSymbol> var_node2 = new LinkedNode<>();
                    var_node2.setValue(new AstNode("variable", f.getParamNames().get(i)));
                    var_node2.setIdx(last_id++);

                    var_node2.setParent(assing_node2);
                    tempvar_node.setParent(assing_node2);
                    assing_node2.getChildren().add(tempvar_node);
                    assing_node2.getChildren().add(var_node2); //variable = expr

                    assing_node2.setParent((LinkedNode<AstSymbol>) call_parent);
                    REVERSE_ARGS.push(assing_node2); //t = exp, v = t.
                }


                //preserve same order of argument list via usage of Stack.
                //as add method behaves like a stack. (insertion shift all items to the right side).
                while(!REVERSE_ARGS.isEmpty()) {
                    LinkedNode<AstSymbol> arg_node = REVERSE_ARGS.top();
                    REVERSE_ARGS.pop();
                    ast.getRealChildren(call_parent).add(IdxOfCall, arg_node);
                }

                //replace call_node with goto_node.
                LinkedNode<AstSymbol> goto_node = new LinkedNode<>();
                goto_node.setValue(new AstNode("goto", ">$" + l_id));
                goto_node.setIdx(last_id++);

                IdxOfCall = PositionalTreeUtils.indexOfChild(ast, call_parent, call_node);
                goto_node.setParent((LinkedNode<AstSymbol>) call_parent);
                ast.getRealChildren(call_parent).set(IdxOfCall, goto_node);

                //attach label node to f_body.

                LinkedNode<AstSymbol> label_node = new LinkedNode<>();
                label_node.setValue(new AstNode("label", ">$" + l_id));
                label_node.setIdx(last_id++);

                label_node.setParent((LinkedNode<AstSymbol>) f.getBody());
                ast.getRealChildren(f.getBody()).add(1, label_node); //add label after start node in program.

                l_id++; //increment label id for next function.
            } //one caller (with args)
        } //end for.
    }
}