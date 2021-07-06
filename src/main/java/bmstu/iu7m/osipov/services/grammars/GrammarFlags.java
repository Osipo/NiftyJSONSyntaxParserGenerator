package bmstu.iu7m.osipov.services.grammars;

public interface GrammarFlags {
    int ORIGINAL = 0x0;
    int SHORTENED = 0x1;
    int NOT_USELESS = 0x2;
    int NO_EMPTY_STRINGS = 0x4;
    int NOT_CYCLED = 0x8;
    int INDEXED = 0x10;
    int NON_LEFT_RECURSIVE = 0x20;
    int NON_LEFT_PREFIXES = 0x40;
    int NON_EQUAL_RULES = 0x80;
    int REACHABLE = 0x100;
    int CHOMSKY = 0x200;
}
