package bmstu.iu7m.osipov.exceptions.grammar;

public class InvalidRegexSyntaxException extends RuntimeException {
    public InvalidRegexSyntaxException(String message, Throwable throwable){
        super(message, throwable);
    }
}
