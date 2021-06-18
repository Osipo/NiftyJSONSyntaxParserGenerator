package bmstu.iu7m.osipov.services.parsers.json.elements;

public class JsonString extends JsonElement<String> {
    private String val;
    public JsonString(String v){
        this.val = v;
    }

    public void setValue(String val) {
        this.val = val;
    }

    @Override
    public String getValue() {
        return val;
    }

    @Override
    public String toString(){
        return val;
    }
}
