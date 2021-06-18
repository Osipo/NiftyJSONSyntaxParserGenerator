package bmstu.iu7m.osipov.exceptions;

public class InvalidJsonGrammarException extends RuntimeException {
    public InvalidJsonGrammarException(String message, Throwable throwable){
        super(message, throwable);
    }
}
