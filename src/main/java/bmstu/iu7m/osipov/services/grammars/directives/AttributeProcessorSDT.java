package bmstu.iu7m.osipov.services.grammars.directives;

import bmstu.iu7m.osipov.services.lexers.LanguageSymbol;
import bmstu.iu7m.osipov.services.lexers.Translation;
import bmstu.iu7m.osipov.structures.trees.LinkedNode;
import bmstu.iu7m.osipov.structures.trees.Node;
import bmstu.iu7m.osipov.utils.GrammarBuilderUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AttributeProcessorSDT implements SDTParser {

    private Map<String, String> attrs;

    private StringBuilder prefix;

    private long elems = 0;

    public AttributeProcessorSDT(){
        this.attrs = new HashMap<>();
        this.prefix = new StringBuilder();
    }

    public Map<String, String> getAttrs(){
        return this.attrs;
    }

    @Override
    public void exec(Translation t, Node<LanguageSymbol> parent) {
        if(t == null || t.getArguments() == null)
            return;
        String action = t.getActName();

        LinkedNode<LanguageSymbol> l_parent = (LinkedNode<LanguageSymbol>) parent;

        switch (action){
            case "putAttr": {

                String arg1 = t.getArguments().getOrDefault("key", null);
                String arg2 = t.getArguments().getOrDefault("val", null);
                arg1 = GrammarBuilderUtils.replaceSymRefsAtArgument(l_parent, arg1);
                arg2 = GrammarBuilderUtils.replaceSymRefsAtArgument(l_parent, arg2);

                arg1 = prefix.toString() + arg1; //include current prefix

                this.attrs.put(arg1, arg2); //key = value.
                break;
            }
            case "addPrefix": {
                String arg1 = t.getArguments().getOrDefault("pref",null);
                arg1 = GrammarBuilderUtils.replaceSymRefsAtArgument(l_parent, arg1);
                this.prefix.append(arg1).append('_').append(elems).append('.');
                elems++;
                break;
            }

            case "removePrefix":{
                //a_1. -> ''
                //a_1.b_2. -> a_1.

                String pr = this.prefix.toString();
                int idx = pr.indexOf('.');
                if(idx + 1 == pr.length()){
                    this.prefix.delete(0, pr.length());
                }
                else{
                    idx = pr.lastIndexOf('.', pr.length() - 2);
                    this.prefix.delete(idx, this.prefix.length());
                }
                break;
            }

            case "showAttrs": {
                Set<Map.Entry<String, String>> vals = this.attrs.entrySet();
                for(Map.Entry<String, String> entry : vals){
                    System.out.println(entry.getKey() + " = "+entry.getValue());
                }
                break;
            }
        } // end switch
    }
}