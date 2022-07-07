package bmstu.iu7m.osipov.services.interpret;

import bmstu.iu7m.osipov.services.grammars.AstSymbol;
import bmstu.iu7m.osipov.structures.trees.PositionalTree;

public interface Interpreter {
    public Env getRootContext();
    void setRootContext(Env ctx);
    void interpret(PositionalTree<AstSymbol> ast);
    void setModuleProcessor(ModuleProcessor mp);
}
