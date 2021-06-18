package bmstu.iu7m.osipov.services.parsers.json;

public class JsonParseException extends RuntimeException {
    public JsonParseException(String message, Throwable throwable){
        super(message, throwable);
    }
}
