package bmstu.iu7m.osipov.services.grammars.xmlMeta;

import java.util.ArrayList;
import java.util.List;

public class ClassElement {
    private List<ConstructorElement> ctors;

    private String typeName;

    public ClassElement(String name){
        this.typeName = name;
    }

    public void addConstructor(ConstructorElement c){
        if(this.ctors == null)
            this.ctors = new ArrayList<>(10);
        for(ConstructorElement c_i: ctors)
            if(c_i.equals(c)) //do not add equal constructor!
                return;

        this.ctors.add(c);
    }

    public List<ConstructorElement> getConstructors(){
        return this.ctors;
    }

    @Override
    public String toString(){
        if(this.ctors == null || this.ctors.size() == 0)
            return "Class " + typeName + " with no constructors.";
        StringBuilder sb = new StringBuilder();
        sb.append("Class ").append(typeName).append(" with constructors:\n[ ");
        for(ConstructorElement c : this.ctors){
            sb.append(c.toString()).append('\n');
        }
        sb.append(']');
        return sb.toString();
    }
}
