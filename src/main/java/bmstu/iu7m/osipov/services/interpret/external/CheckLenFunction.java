package bmstu.iu7m.osipov.services.interpret.external;

import bmstu.iu7m.osipov.services.interpret.Variable;
import bmstu.iu7m.osipov.structures.graphs.Elem;
import bmstu.iu7m.osipov.structures.trees.Action;

import java.util.List;
import java.util.function.Predicate;

public class CheckLenFunction extends AbstractCheckFunction implements Predicate<List<Object>> {


    public CheckLenFunction(ExternalFunctionInterpreter self) {
        super(self);
    }

    @Override
    public boolean test(List<Object> arg)  {
        boolean result = false;

        if(arg.size() != 1)
            return result;

        Object l = arg.get(0); //first item.
        while(l instanceof Elem<?>)
            l = ((Elem<?>) l).getV1();

        if(l instanceof List){ //raw list
            result = true;
            Variable v = new Variable("");
            v.setItems((List<Elem<Object>>) l);
            me.getParams().add(v);
        }
        else if(l instanceof String){ //raw string
            result = true;
            Variable v = new Variable("");
            v.setStrVal((String) l);
            me.getParams().add(v);
        }

        //Argument is variable and it is list.
        else if(l instanceof Variable && ((Variable) l).isList()){ //var list
            result = true;
            Variable v = new Variable("");
            v.setItems(((Variable) l).getItems());
            me.getParams().add(v);
        }
        else if(l instanceof Variable && ((Variable) l).getStrVal() != null){//var string
            result = true;
            Variable v = new Variable("");
            v.setStrVal(((Variable) l).getStrVal());
            me.getParams().add(v);
        }
        return result;
    }
}
