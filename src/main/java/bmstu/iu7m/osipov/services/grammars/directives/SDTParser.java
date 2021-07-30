package bmstu.iu7m.osipov.services.grammars.directives;

import bmstu.iu7m.osipov.services.lexers.LanguageSymbol;
import bmstu.iu7m.osipov.services.lexers.Translation;
import bmstu.iu7m.osipov.structures.trees.Node;

public interface SDTParser {
    public void exec(Translation t, Node<LanguageSymbol> parent);
}
