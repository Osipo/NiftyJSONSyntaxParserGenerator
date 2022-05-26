package bmstu.iu7m.osipov.services.grammars;

public interface AstSymbol {
    String getType();
    String getValue();

    void setValue(String val);
}
