package bmstu.iu7m.osipov.services.grammars.xmlMeta;

import java.lang.reflect.Constructor;

public class SceneConstructor {
    private final Constructor<?> ctor;
    private final Object[] args;

     public SceneConstructor(Constructor<?> ctor, Object[] args){
         this.ctor = ctor;
         this.args = args;
     }

    public Constructor<?> getConstrutor() {
        return ctor;
    }

    public Object[] getArgs() {
        return args;
    }
}
