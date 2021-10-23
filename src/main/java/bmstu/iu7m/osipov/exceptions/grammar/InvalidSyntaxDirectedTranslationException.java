package bmstu.iu7m.osipov.exceptions.grammar;

public class InvalidSyntaxDirectedTranslationException extends RuntimeException {
    public InvalidSyntaxDirectedTranslationException(){
        super("Cannot create SyntaxDirectedAction object. Check properties of json object.\n" +
                "(\"act\" is required and all properties must have type \"String\"", null);
    }

    public InvalidSyntaxDirectedTranslationException(String message, Throwable throwable){
        super(message, throwable);
    }
}
