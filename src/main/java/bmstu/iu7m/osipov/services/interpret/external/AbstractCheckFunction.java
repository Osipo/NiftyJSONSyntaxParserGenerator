package bmstu.iu7m.osipov.services.interpret.external;

import java.util.List;
import java.util.function.Predicate;

public abstract class AbstractCheckFunction  implements Predicate<List<Object>>  {

    protected ExternalFunctionInterpreter me;

    public  AbstractCheckFunction(ExternalFunctionInterpreter self){
        this.me = self;
    }

    @Override
    public abstract boolean test(List<Object> objects);
}
