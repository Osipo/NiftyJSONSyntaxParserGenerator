package bmstu.iu7m.osipov.services.grammars.xmlMeta;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ConstructorElement {

    private List<ParameterElement> params;

    public ConstructorElement(){

    }

    public void addParameter(ParameterElement p){
        if(this.params == null)
            this.params = new ArrayList<>(20);
        this.params.add(p);
    }

    public boolean haveNoParams(){
        return this.params == null || this.params.size() == 0;
    }

    public List<ParameterElement> getParams(){
        return this.params;
    }

    @Override
    public boolean equals(Object ob){
        if(!(ob instanceof ConstructorElement))
            return false;
        ConstructorElement other = (ConstructorElement) ob;
        List<ParameterElement> params1 = this.params;
        List<ParameterElement> params2 = other.getParams();

        if(params1 == params2)
            return true;
        if( (params1 == null && params2 != null) || (params1 != null && params2 == null))
            return false;
        if(params1.size() != params2.size())
            return false;

        Iterator<ParameterElement> it1 = params1.iterator();
        Iterator<ParameterElement> it2 = params2.iterator();
        while(it1.hasNext() && it2.hasNext()){
            if(!it1.next().getType().equals(it2.next().getType()))
                return false; //at position parameter types different.
        }

        return true;
    }

    @Override
    public String toString(){
        if(this.params == null || this.params.size() == 0)
            return "Default constructor ()";
        StringBuilder sb = new StringBuilder();
        sb.append("Constructor with params\n(");
        for(ParameterElement p : this.params){
            sb.append(p.toString()).append('\n');
        }
        sb.append(')');
        return sb.toString();
    }
}
