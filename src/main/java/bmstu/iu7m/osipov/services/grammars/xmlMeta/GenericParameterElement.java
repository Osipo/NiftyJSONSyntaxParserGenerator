package bmstu.iu7m.osipov.services.grammars.xmlMeta;

import java.util.ArrayList;
import java.util.List;

public class GenericParameterElement extends ParameterElement implements Collectable {

    protected List<Collectable> params;

    public GenericParameterElement(String type, String name) {
        super(type, name);
        this.params = new ArrayList<>();
    }

    public GenericParameterElement(String type) {
        super(type);
        this.params = new ArrayList<>();
    }

    public void addChildParameter(Collectable p){
        this.params.add(p);
    }

    public Collectable getChildParameter(int idx){
        return this.params.get(idx);
    }

    public boolean IsEmpty(){
        return this.params.size() == 0;
    }
}
