package bmstu.iu7m.osipov.services.grammars.directives;

import bmstu.iu7m.osipov.services.grammars.Grammar;
import bmstu.iu7m.osipov.services.lexers.DFALexer;
import bmstu.iu7m.osipov.services.lexers.FALexerGenerator;
import bmstu.iu7m.osipov.services.lexers.LanguageSymbol;
import bmstu.iu7m.osipov.services.lexers.Translation;
import bmstu.iu7m.osipov.services.parsers.LRAlgorithm;
import bmstu.iu7m.osipov.services.parsers.LRParser;
import bmstu.iu7m.osipov.services.parsers.json.SimpleJsonParser2;
import bmstu.iu7m.osipov.services.parsers.json.elements.JsonObject;
import bmstu.iu7m.osipov.structures.automats.DFA;
import bmstu.iu7m.osipov.structures.lists.LinkedStack;
import bmstu.iu7m.osipov.structures.trees.LinkedNode;
import bmstu.iu7m.osipov.structures.trees.Node;
import bmstu.iu7m.osipov.utils.GrammarBuilderUtils;

import java.util.HashMap;
import java.util.Map;

public class JavaFXExtraXMLProcessorSDT implements SDTParser {
    protected Map<String, String> obj_attrs;
    protected LinkedStack<Object> objects;
    protected Object root;
    protected String curName;

    /* State initial = 0. */
    /*
    * -1 - Error.
    * 0 - recognize root (awaiting Stage, Resource or Fragment) (goto 1, 4, 5)
    * 1 - Stage recognized and created -> awaiting Scene
    *   10 - reading Stage.Resources content
    *   11 - read Resource file (dictionary)
    *   12 - read Fragment content
    *   30 - reading Array of items when sequence needed.
    * 2 - awaiting Scene root Node
    * 3 - read content of the Scene
    * 4 - read content of the root Resource
    * 5 - read content of the root Fragment
    */
    protected int state;

    public JavaFXExtraXMLProcessorSDT(){
        this.objects = new LinkedStack<>();
        this.obj_attrs = new HashMap<>();
        this.state = 0;

    }

    public void restart(){
        this.obj_attrs.clear();
        this.objects.clear();
        this.root = null;
        this.state = 0;
    }

    protected void setRoot(Object root){
        this.root = root;
    }

    public Object getRoot(){
        return root;
    }

    @Override
    public void exec(Translation t, Node<LanguageSymbol> parent) {
        if (t == null || t.getArguments() == null || this.state == -1)
            return;

        String action = t.getActName();
        LinkedNode<LanguageSymbol> l_parent = (LinkedNode<LanguageSymbol>) parent;

        switch (action){
            case "putAttr": {
                String arg1 = t.getArguments().getOrDefault("key", null);
                String arg2 = t.getArguments().getOrDefault("val", null);
                arg1 = GrammarBuilderUtils.replaceSymRefsAtArgument(l_parent, arg1);
                arg2 = GrammarBuilderUtils.replaceSymRefsAtArgument(l_parent, arg2);

                arg2 = arg2.substring(1, arg2.length() - 1); // unquote value.
                this.obj_attrs.put(arg1, arg2);

                break;
            }//end 'putAttr'
        }//end switch
    }//end method
}