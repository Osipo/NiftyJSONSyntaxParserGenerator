package bmstu.iu7m.osipov.structures.trees.translators;

import bmstu.iu7m.osipov.services.grammars.directives.SDTParser;
import bmstu.iu7m.osipov.services.grammars.directives.SyntaxDirectedTranslation;
import bmstu.iu7m.osipov.services.lexers.LanguageSymbol;
import bmstu.iu7m.osipov.services.lexers.Translation;
import bmstu.iu7m.osipov.structures.trees.Action;
import bmstu.iu7m.osipov.structures.trees.LinkedNode;
import bmstu.iu7m.osipov.structures.trees.Node;

import java.util.HashMap;
import java.util.Map;

public class ExecuteTranslationNode implements Action<Node<LanguageSymbol>> {

    private Map<String, SDTParser> act_parsers;

    public ExecuteTranslationNode(){
        this.act_parsers = new HashMap<>();
    }

    public void putActionParser(String actName, SDTParser parser){
        this.act_parsers.put(actName, parser);
    }

    @Override
    public void perform(Node<LanguageSymbol> arg) {
        LinkedNode<LanguageSymbol> t = (LinkedNode<LanguageSymbol>) arg;
        if(t.getValue() instanceof Translation && t.getValue() instanceof SyntaxDirectedTranslation){
            SyntaxDirectedTranslation act = (SyntaxDirectedTranslation) t.getValue();
            SDTParser executor = act_parsers.getOrDefault(act.getActName(), null);
            if(executor != null)
                executor.exec(act, t);
        }
    }
}
