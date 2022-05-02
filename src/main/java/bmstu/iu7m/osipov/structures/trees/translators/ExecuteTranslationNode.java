package bmstu.iu7m.osipov.structures.trees.translators;

import bmstu.iu7m.osipov.services.grammars.directives.SDTParser;
import bmstu.iu7m.osipov.services.grammars.directives.SyntaxDirectedTranslation;
import bmstu.iu7m.osipov.services.lexers.LanguageSymbol;
import bmstu.iu7m.osipov.services.lexers.Translation;
import bmstu.iu7m.osipov.structures.trees.Action;
import bmstu.iu7m.osipov.structures.trees.LinkedNode;
import bmstu.iu7m.osipov.structures.trees.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExecuteTranslationNode implements Action<Node<LanguageSymbol>> {

    private Map<String, List<SDTParser>> act_parsers;

    public ExecuteTranslationNode(){
        this.act_parsers = new HashMap<>();
    }

    public void putActionParser(String actName, SDTParser parser){
        List<SDTParser> actors = null;
        if( (actors = this.act_parsers.getOrDefault(actName, null)) == null){
            actors = new ArrayList<>();
            actors.add(parser);
            this.act_parsers.put(actName, actors);
        }
        else{
            actors.add(parser);
        }
    }

    @Override
    public void perform(Node<LanguageSymbol> arg) {
        LinkedNode<LanguageSymbol> t = (LinkedNode<LanguageSymbol>) arg;

        if(t.getValue() instanceof Translation && t.getValue() instanceof SyntaxDirectedTranslation){
            SyntaxDirectedTranslation act = (SyntaxDirectedTranslation) t.getValue();
            List<SDTParser> executors = act_parsers.getOrDefault(act.getActName(), null);
            if(executors == null)
                return;
            for(SDTParser e : executors){
                e.exec(act, t.getParent());
            }
        }
    }
}