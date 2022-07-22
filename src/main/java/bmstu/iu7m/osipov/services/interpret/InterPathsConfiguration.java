package bmstu.iu7m.osipov.services.interpret;

import bmstu.iu7m.osipov.Main;
import bmstu.iu7m.osipov.utils.PathStringUtils;

public interface InterPathsConfiguration {
    public static final String PROJECT_NAME = "NiftyJSONSyntaxParserGenerator";
    public static final String TEST_JSON_DOCS_DIR = PathStringUtils.replaceSeparator(Main.PROJECT_DIR + "src\\test\\\\resources\\\\json\\"); //PathStringUtils.replaceSeparator("C:\\Users\\Oleg\\IdeaProjects\\NiftyJSONCompilerGenerator\\src\\test\\resources\\json\\");
    public static final String GRAMMARS = PathStringUtils.replaceSeparator(Main.PROJECT_DIR + "src\\test\\resources\\grammars\\");
    public static final String LEXERS = PathStringUtils.replaceSeparator(Main.PROJECT_DIR + "src\\test\\resources\\grammars\\lexers\\");
    public static final String PARSERS = PathStringUtils.replaceSeparator(Main.PROJECT_DIR + "src\\test\\resources\\grammars\\parsers\\");
    public static final String LLPARSERS = PathStringUtils.replaceSeparator(Main.PROJECT_DIR + "src\\test\\resources\\grammars\\parsers\\LL1\\");
    public static final String PARSER_INPUT = PathStringUtils.replaceSeparator(Main.PROJECT_DIR + "src\\test\\resources\\input\\");
    public static final String TEST_RESOURCES = PathStringUtils.replaceSeparator(Main.PROJECT_DIR + "src\\test\\resources\\");
}
