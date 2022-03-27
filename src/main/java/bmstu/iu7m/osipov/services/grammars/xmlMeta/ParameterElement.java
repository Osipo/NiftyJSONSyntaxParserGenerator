package bmstu.iu7m.osipov.services.grammars.xmlMeta;;

public class ParameterElement implements Collectable {
    private String type;
    private String name;

    public ParameterElement(String type, String name){
        this.type = type;
        this.name = name;
    }

    public ParameterElement(String type){
        this.type = type;
        this.name = null;
    }

    public String getType(){
        return type;
    }

    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString(){
        return "{ \"type\": " + type+ ", \"name\": " + name + "}";
    }

    @Override
    public boolean isCollectionType() {
        return type.contains("List") || type.contains("Array") || type.contains("Map") || type.contains("Set") || type.contains("Collection");
    }
}
