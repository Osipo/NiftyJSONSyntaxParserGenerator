package bmstu.iu7m.osipov.services.grammars.xmlMeta;

import java.util.ArrayList;
import java.util.List;

public class ClassElement {
    private List<ConstructorElement> ctors;

    private String typeName;

    private String packageName;

    public ClassElement(String name, String packageName){
        this.typeName = name;
        this.packageName = packageName;
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

    public String getName(){
        return this.typeName;
    }

    public String getPackageName(){
        return this.packageName;
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
