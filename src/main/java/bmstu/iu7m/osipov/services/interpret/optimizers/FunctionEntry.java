package bmstu.iu7m.osipov.services.interpret.optimizers;

import bmstu.iu7m.osipov.services.grammars.AstSymbol;
import bmstu.iu7m.osipov.structures.trees.Node;

import java.util.ArrayList;
import java.util.List;

public class FunctionEntry {
    private List<String> paramNames;
    private Node<AstSymbol> body;
    private String fName;
    public FunctionEntry(String fname, Node<AstSymbol> body){
        this.paramNames = new ArrayList<>();
        this.fName = fname;
        this.body = body;
    }

    public FunctionEntry(Node<AstSymbol> body){
        this(null, body);
    }

    public String getfName() {
        return fName;
    }

    public Node<AstSymbol> getBody() {
        return body;
    }

    public void setBody(Node<AstSymbol> body) {
        this.body = body;
    }

    public List<String> getParamNames() {
        return paramNames;
    }

}
