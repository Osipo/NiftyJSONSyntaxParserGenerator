package bmstu.iu7m.osipov.services.interpret.external;

import bmstu.iu7m.osipov.services.grammars.AstSymbol;
import bmstu.iu7m.osipov.services.interpret.Env;
import bmstu.iu7m.osipov.services.interpret.FunctionInterpreter;
import bmstu.iu7m.osipov.services.interpret.Variable;
import bmstu.iu7m.osipov.structures.lists.LinkedStack;
import bmstu.iu7m.osipov.structures.trees.Action;
import bmstu.iu7m.osipov.structures.trees.Action2;
import bmstu.iu7m.osipov.structures.trees.Node;

import java.util.List;
import java.util.function.Predicate;

public class ExternalFunctionInterpreter extends FunctionInterpreter {

    protected Predicate<List<Object>> checkArgs;
    protected Action2<List<Variable>, LinkedStack<Object>> code;

    public ExternalFunctionInterpreter(Node<AstSymbol> root, Env context, List<Variable> params) {
        super(root, context, params);
    }

    public void setCheckArgs(Predicate<List<Object>> checkArgs) {
        this.checkArgs = checkArgs;
    }

    public void setCode(Action2<List<Variable>, LinkedStack<Object>> code) {
        this.code = code;
    }

    @Override
    public void bindArguments(List<Object> args) throws Exception {
        if(checkArgs != null && !checkArgs.test(args))
            throw new Exception("Argument of the external function '" + this.funName + "' are not valid!");
    }

    public void callExternal(LinkedStack<Object> expr){
        code.perform(this.params, expr);
        this.params.clear(); //remove args after execution.
    }
}
