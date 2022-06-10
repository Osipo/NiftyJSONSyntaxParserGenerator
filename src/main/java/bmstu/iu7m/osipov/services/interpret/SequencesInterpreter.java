package bmstu.iu7m.osipov.services.interpret;

import bmstu.iu7m.osipov.services.grammars.AstSymbol;
import bmstu.iu7m.osipov.structures.trees.Node;
import bmstu.iu7m.osipov.structures.trees.PositionalTree;
import bmstu.iu7m.osipov.structures.trees.VisitorMode;
import bmstu.iu7m.osipov.structures.trees.VisitorsNextIteration;

//Interpret sequences.
public class SequencesInterpreter {
    private Node<AstSymbol> exprRoot;
    private Node<AstSymbol> sequencesRoot;
    private Env context;
    private PositionalTree<AstSymbol> ast;

    public SequencesInterpreter(PositionalTree<AstSymbol> ast, Node<AstSymbol> exprRoot, Node<AstSymbol> seqRoot, Env context){
        this.exprRoot = exprRoot;
        this.sequencesRoot = seqRoot;
        this.context = context;
        this.ast = ast;
    }

    public void generateItems(Node<AstSymbol> parentList){

        //Collect all sequence items.
        VisitorsNextIteration<AstSymbol>  nextItr = new VisitorsNextIteration<>();
        ast.visitFrom(VisitorMode.POST, (c, next) ->{

        }, sequencesRoot, nextItr);
    }
}