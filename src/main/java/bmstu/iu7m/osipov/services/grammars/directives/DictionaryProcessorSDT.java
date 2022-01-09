package bmstu.iu7m.osipov.services.grammars.directives;

import bmstu.iu7m.osipov.services.lexers.LanguageSymbol;
import bmstu.iu7m.osipov.services.lexers.Token;
import bmstu.iu7m.osipov.services.lexers.Translation;
import bmstu.iu7m.osipov.structures.trees.LinkedNode;
import bmstu.iu7m.osipov.structures.trees.Node;
import java.util.LinkedHashMap;
import java.util.Map;

// Can be used to replace tag names at XML (and other specified lexemes at Syntax tree)
// Use ''nodenum'' argument to specify lexeme at the rule to be replaced.
// applied to Grammar javafx_xml.json
// see file test/resources/input/stage_trans_example2.xml
public class DictionaryProcessorSDT implements SDTParser {

    private Map<String, String> dictionary;

    public DictionaryProcessorSDT(){
        this.dictionary = new LinkedHashMap<>(); //main dictionary of terms!
//        dictionary.put("Dungeon", "Stage");
//        dictionary.put("Dungeon.Masters","Stage.Resources");
//        dictionary.put("Bondage", "Insets");
//        dictionary.put("Gym", "Scene");
//        dictionary.put("Boss","Button");
//        dictionary.put("WebSite", "TextField");
//        dictionary.put("Stairs","VBox");
//        dictionary.put("CatBoxes", "HBox");
//        dictionary.put("Vaaan", "Fragment");
//        dictionary.put("Latex", "Style");
//        dictionary.put("Glove","Item");
        // Where have no new terms yet. I am sorry...
    }

    @Override
    public void exec(Translation t, Node<LanguageSymbol> parent) {
        if(t == null || t.getArguments() == null)
            return;
        String action = t.getActName();
        LinkedNode<LanguageSymbol> l_parent = (LinkedNode<LanguageSymbol>) parent;

        String arg1 = t.getArguments().getOrDefault("nodenum", null);
        LanguageSymbol sym = l_parent.getChildren().get(Integer.parseInt(arg1)).getValue();
        if(sym instanceof Token){
            ((Token) sym).setLexem(this.dictionary.getOrDefault(sym.getLexeme(), sym.getLexeme()));
        }
    }// end method
}