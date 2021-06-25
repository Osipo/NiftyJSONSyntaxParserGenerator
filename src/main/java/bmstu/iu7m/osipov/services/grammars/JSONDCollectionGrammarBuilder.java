package bmstu.iu7m.osipov.services.grammars;

import bmstu.iu7m.osipov.services.parsers.json.elements.JsonArray;
import bmstu.iu7m.osipov.services.parsers.json.meta.JsonDocumentDescriptor;
import bmstu.iu7m.osipov.services.parsers.json.meta.JsonProperty;
import bmstu.iu7m.osipov.structures.lists.LinkedStack;
import bmstu.iu7m.osipov.utils.PathStringUtils;

import java.util.*;

public class JSONDCollectionGrammarBuilder {

    private Map<String, GrammarString> cache = new HashMap<>();
    public Grammar getCommonGrammar(JsonDocumentDescriptor D){
        Set<String> T = new HashSet<>();
        int nrules = 0;
        Set<String> keywords = new HashSet<>();
        Set<String> N = new HashSet<>();
        Map<String,Set<GrammarString>> P = new HashMap<>();
        String start = null;
        String em = null;
        Map<String, List<String>> lexs = new HashMap<>();
        GrammarMetaInfo meta = new GrammarMetaInfo();

        //For each Pointer.
        for(String pointer : D.getProperties().keySet()){
            LinkedStack<String> elems = new LinkedStack<>(
                    PathStringUtils.splitPath(pointer, "/")
            );
            //from right to the left elems of pointer
            while(!elems.isEmpty()){
                String key = elems.top();
                String sub_p = pointer.substring(0, pointer.lastIndexOf(key));
                elems.pop();
                if(cache.containsKey(sub_p))
                    continue;
                try{
                    int a_idx = Integer.parseInt(key);
                    JsonArray arr = extractType(D, sub_p, JsonArray.class);
                    if(arr == null)
                        return null;
                    parseArray(D, arr, sub_p);
                } catch (NumberFormatException e){
                    keywords.add(key);
                    ArrayList<JsonProperty> val = D.getProperties().get(sub_p);
                    N.add("N"+key+nrules);
                    nrules++;
                    GrammarString nbody = new GrammarString();

                    if(!cache.values().contains(nbody)){
                        nbody.addSymbol(new GrammarSymbol('t', key));
                    }
                }
            }
        }

        return new Grammar(T, N, P, start, em, lexs, meta);
    }

    private void parseArray(JsonDocumentDescriptor D, JsonArray arr, String prop){

    }

    private void parseAlterntive(){

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
