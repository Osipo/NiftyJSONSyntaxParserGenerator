package bmstu.iu7m.osipov.ui.models;

import bmstu.iu7m.osipov.services.lexers.ILexer;
import bmstu.iu7m.osipov.services.parsers.Parser;

// Returns lexer and parser modules.
public interface SyntaxAnalyzer {
    public ILexer getCurLexer();

    public Parser getCurParser();
}
