package bmstu.iu7m.osipov.services.grammars.xmlMeta;

public class ParameterElement {
    private String type;
    private String name;

    public ParameterElement(String type, String name){
        this.type = type;
        this.name = name;
    }

    public String getType(){
        return type;
    }

    public String getName(){
        return name;
    }

    @Override
    public String toString(){
        return "{ \"type\": " + type+ ", \"name\": " + name + "}";
    }
}
