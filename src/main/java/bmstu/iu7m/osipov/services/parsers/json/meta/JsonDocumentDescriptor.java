package bmstu.iu7m.osipov.services.parsers.json.meta;

import bmstu.iu7m.osipov.structures.lists.LinkedStack;
import bmstu.iu7m.osipov.structures.lists.Triple;
import bmstu.iu7m.osipov.services.parsers.json.elements.*;

import java.util.*;

public class JsonDocumentDescriptor {
    private Map<String, ArrayList<JsonProperty>> properties;

    public JsonDocumentDescriptor(){
        this.properties = new HashMap<>();
    }

    public Map<String, ArrayList<JsonProperty>> getProperties() {
        return properties;
    }

    public void clearDescriptor(){
        for(String k : this.properties.keySet()){
            ArrayList<JsonProperty> props = this.properties.getOrDefault(k, null);
            if(props != null){
                props.clear();
            }
        }
        this.properties.clear();
    }

    public void merge(JsonDocumentDescriptor desc){
        if(desc == null)
            return;
        Set<Map.Entry<String, ArrayList<JsonProperty>>> props = desc.getProperties().entrySet();
        for(Map.Entry<String, ArrayList<JsonProperty>> en : props){
            if(this.properties.containsKey(en.getKey())){
                ArrayList<JsonProperty> rules = this.properties.get(en.getKey());
                rules.addAll(en.getValue());
            }
            else{
                this.properties.put(en.getKey(), en.getValue());
            }
        }
    }

    public void describe2(JsonObject root){
        LinkedStack<Triple<String, JsonElement, String>> S = new LinkedStack<>();//entity
        S.push(new Triple<>("", root, ""));
        while(!S.isEmpty()){

            String prop = S.top().getV1();
            JsonElement e = S.top().getV2();
            String c = S.top().getV3();
            S.pop();
            if(e instanceof JsonObject){
                ArrayList<JsonProperty> rules = new ArrayList<>();
                JsonProperty self_obj = new JsonProperty(prop, "object",true, e);
                rules.add(self_obj);
                properties.put(prop, rules);

                //ADD <key, value> pair to the STACK.
                JsonObject ob = (JsonObject)e;
                Iterator<String> ks = ob.getValue().keySet().iterator();

                //FOR EACH PAIR create JSONPointer and add it to the Stack.
                while(ks.hasNext()) {
                    String p = ks.next();
                    p = p.replaceAll("~", "~0");//encoded by RFC 6901
                    p = p.replaceAll("/", "~1");
                    String k = prop + "/" + p;// JSON_Pointer
                    S.push(new Triple<>(k, ob.getProperty(p), p));
                }
            }
            else if(e instanceof JsonArray){
                /* Put JsonProperty for array itself*/
                ArrayList<JsonProperty> rules = new ArrayList<>();
                JsonProperty self_arr = new JsonProperty(prop, "array",true, e);
                rules.add(self_arr);
                properties.put(prop, rules);

                ArrayList<JsonElement> arr = ((JsonArray)e).getValue();
                int s = arr.size();
                int i = 0;


                JsonElement el = null;
                while(i < s){
                    el = arr.get(i);
                    String k = prop + "/" + i;// JSON_Pointer
                    S.push(new Triple<>(k, el, i + ""));
                    i++;
                }
            }
            else if(e instanceof JsonNull){
                ArrayList<JsonProperty> rules = new ArrayList<>();
                JsonProperty np = new JsonProperty(c, "null", false, e);
                rules.add(np);
                properties.put(prop, rules);
            }
            else if(e instanceof JsonBoolean){
                ArrayList<JsonProperty> rules = new ArrayList<>();
                JsonProperty np = new JsonProperty(c, "boolean", false, e);
                rules.add(np);
                properties.put(prop, rules);
            }
            else if(e instanceof JsonNumber){
                ArrayList<JsonProperty> rules = new ArrayList<>();
                JsonPropertyNumber np = new JsonPropertyNumber(c, "number", false, e);
                rules.add(np);
                properties.put(prop, rules);
            }
            else if(e instanceof JsonString){
                ArrayList<JsonProperty> rules = new ArrayList<>();
                JsonPropertyString np = new JsonPropertyString(c, "string", false, e, ((JsonString) e).getValue().length());
                rules.add(np);
                properties.put(prop, rules);
            }
        }
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(String p : properties.keySet()){
            sb.append(p).append(" : {\n\t");
            ArrayList<JsonProperty> vals = properties.getOrDefault(p, null);
            if(vals == null)
                continue;
            for(JsonProperty val : vals){
                sb.append(val.toString());
            }
            sb.append("\n}\n");
        }
        return sb.toString();
    }
}
