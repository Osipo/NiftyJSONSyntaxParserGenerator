package bmstu.iu7m.osipov.services.parsers.json.elements;

public class JsonNull extends JsonElement<String> {
    @Override
    public String getValue() {
        return "null";
    }
}
