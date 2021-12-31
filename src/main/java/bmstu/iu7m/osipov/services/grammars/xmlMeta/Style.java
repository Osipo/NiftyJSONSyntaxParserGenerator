package bmstu.iu7m.osipov.services.grammars.xmlMeta;

import bmstu.iu7m.osipov.services.parsers.json.elements.StrLinkedHashMap;

import java.util.Map;

public class Style extends StrLinkedHashMap<String> {

    public Style(){}

    public void copyFrom(Map<String, String> other){
        // copy parent's content into current map.
        for(Map.Entry<String, String> e : other.entrySet()){
            this.put(e.getKey(), e.getValue());
        }
    }
}
