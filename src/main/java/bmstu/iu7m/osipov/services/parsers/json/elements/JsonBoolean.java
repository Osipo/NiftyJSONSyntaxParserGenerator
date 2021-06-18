package bmstu.iu7m.osipov.services.parsers.json.elements;

public class JsonBoolean extends JsonElement<Boolean> {

    private boolean v;
    public JsonBoolean(Character c){
        v = (c == 't');
    }

    @Override
    public Boolean getValue() {
        return v;
    }

}
