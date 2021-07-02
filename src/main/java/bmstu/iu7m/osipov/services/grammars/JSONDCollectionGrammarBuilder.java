package bmstu.iu7m.osipov.services.grammars;

import bmstu.iu7m.osipov.services.parsers.json.elements.*;
import bmstu.iu7m.osipov.services.parsers.json.meta.JsonDocumentDescriptor;
import bmstu.iu7m.osipov.services.parsers.json.meta.JsonProperty;
import bmstu.iu7m.osipov.structures.lists.LinkedStack;
import bmstu.iu7m.osipov.utils.PathStringUtils;

import java.util.*;

public class JSONDCollectionGrammarBuilder {

    private Map<String, GrammarString> cache = new HashMap<>();
    private Set<String> processed = new HashSet<>();

    public Grammar getCommonGrammar(JsonDocumentDescriptor D){

        //Grammar for Empty-Language [L = empty-set]
        Grammar G = new Grammar();

        //For each Pointer.
        for(String pointer : D.getProperties().keySet()){
            LinkedStack<String> elems = new LinkedStack<>(
                    PathStringUtils.splitPath(pointer, "/")
            );

            //from right to the left elems of pointer
            // / a /a b
            while(!elems.isEmpty()){
                String key = elems.top();
                String sub_p = pointer.substring(0, pointer.lastIndexOf(key));
                sub_p = sub_p + key; // => [/key => / + key, /k1/k2 => /k1/ + k2, / + k1]
                elems.pop();
                if(processed.contains(sub_p))
                    continue;

                //Scan each alternative value for
                for(JsonProperty property : D.getProperties().get(sub_p)){
                    parseAlterntive(G, D, property, sub_p, key);
                }
            }
        }
        return G;
    }

    private void parseAlterntive(Grammar G, JsonDocumentDescriptor D, JsonProperty prop, String path, String key){
        Set<GrammarString> product_rules = G.getProductions().get(key);// try get information about production to identify alternatives
        if(product_rules == null)
            product_rules = new HashSet<>();
        try{
            int a_idx = Integer.parseInt(key);
        } catch (NumberFormatException e){
            G.getMeta().getKeywords().add(PathStringUtils.quoute(key));
            G.getNonTerminals().add("N" + G.getNonTerminals().size());
            GrammarString nbody = new GrammarString();
            nbody.addSymbol(new GrammarSymbol('t', PathStringUtils.quoute(key) ));
            nbody.addSymbol(new GrammarSymbol('t', ":"));
            if(prop.getVal() instanceof JsonNull){
                nbody.addSymbol(new GrammarSymbol('t', "null"));
            }
            else if(prop.getVal() instanceof JsonBoolean){
                String literal = ((JsonBoolean)prop.getVal()).getValue() ? "true" : "false";
                nbody.addSymbol(new GrammarSymbol('t', literal));
            }
        }
    }


    private <T> T extractType(JsonDocumentDescriptor desc, String p, Class<T> clazz){
        ArrayList<JsonProperty> props = desc.getProperties().get(p);
        if(props == null)
            return null;
        for(JsonProperty prop : props){
            if(clazz.isAssignableFrom(prop.getClass()))
                return clazz.cast(prop);
        }
        return null;
    }
}
