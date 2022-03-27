package bmstu.iu7m.osipov.services.grammars.directives;

import bmstu.iu7m.osipov.services.grammars.xmlMeta.ClassElement;

import java.util.Map;

public interface TypeElement {
    public Map<String, ClassElement> getTypes();

    public Map<String, String> getAliases();
}
