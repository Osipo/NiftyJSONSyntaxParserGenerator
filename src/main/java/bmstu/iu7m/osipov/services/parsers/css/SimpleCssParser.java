package bmstu.iu7m.osipov.services.parsers.css;

import bmstu.iu7m.osipov.structures.graphs.Pair;
import bmstu.iu7m.osipov.structures.lists.LinkedStack;
import bmstu.iu7m.osipov.ui.models.entities.UIComponent;
import bmstu.iu7m.osipov.ui.models.stores.UIComponentStore;
import com.codepoetics.protonpack.maps.MapStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Component("CssParser")
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
@DependsOn({"uiStore"})
/*
  Read and parse css file from 'resources' directory
  Preserves the style rule that was previously applied IF one has the high priority.
  Example: id.p1 class.p1 ->  id.p1 (id > class)
  Example: class1.p1, class2.p1 => class2.p2 (prev priority is <= new priority).
 */
public class SimpleCssParser {

    @Autowired
    protected UIComponentStore uiStore;

    public void applyStylesFromResource(String url){
        String curRule = null;
        StringBuffer buf = null;
        String prop = null;
        String val = null;
        HashMap<String, HashMap<String, String>> css_data = null; // selector -> dictionary of props
        HashMap<UIComponent, Pair<String, HashMap<String, String>>> applied_styles = null; // component -> dictionary of props

        //Table of transitions
        // 0 == start state.
        // 1 == process RuleName.
        // 2 == awaiting first symbol of Prop name.
        // 3 == process Prop name
        // 4 == process prop value string.
        // (0, 'non-space-symbol') -> 1.
        // (1, '{') == 2.
        // (2, 'non-space-symbol') -> 3.
        // (3, ':') -> 4.
        // (4, ';') -> 2.
        // (2, '}') -> 0.
        int state = 0;


        try{
            InputStream io = getClass().getClassLoader().getResourceAsStream(url);
            if(io == null)
                throw new FileNotFoundException();
            curRule = null;
            buf = new StringBuffer();
            css_data = new HashMap<>();
            applied_styles = new HashMap<>();
            char ch = 0;

            System.out.println(url);
            //MAIN LOOP
            while(ch != 65535){
                ch = (char) io.read();
                if(state != 4 && (ch == '\t' || ch == ' ' || ch == '\n' || ch == '\r')) {
                    continue;
                }
                if(state == 0 && ch != '{') {
                    state = 1;
                }
                else if(state == 1 && ch == '{'){
                    curRule = buf.toString();
                    buf.delete(0, buf.length());
                    state = 2;
                    System.out.println(curRule);
                    css_data.put(curRule, new HashMap<String, String>());
                    continue;
                }
                else if(state == 2 && ch == '}'){
                    state = 0;
                    applyStyle(curRule, css_data, applied_styles);
                    buf.delete(0, buf.length());
                    continue;
                }
                else if(state == 2 && ch != ':'){
                    state = 3;
                }
                else if(state == 3 && ch == ':'){
                    state = 4;
                    prop = buf.toString();
                    buf.delete(0, buf.length());
                    continue;
                }
                else if(state == 4 && ch == ';'){
                    state = 2;
                    val = buf.toString();
                    buf.delete(0, buf.length());
                    css_data.get(curRule).put(prop, val);
                    continue;
                }
                buf.append(ch);
            }

            io.close();
        } catch (FileNotFoundException e){
            System.out.println("Cannot find resource file for url: " + url);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        finally {
            if(buf != null) {
                buf.delete(0, buf.length());
                buf = null;
            }
            curRule = null;

            if(css_data != null) {
                css_data.clear();
            }
            css_data = null;
            if(applied_styles != null){
                applied_styles.clear();
            }
            applied_styles = null;
        }
    }


    private void applyStyle(String rule,
                            HashMap<String, HashMap<String, String>> css_styles,
                            HashMap<UIComponent, Pair<String, HashMap<String, String>>> applied_styles
    )
    {
        char type = rule.charAt(0);
        String sel = rule.substring(1);// id or className
        HashMap<String, String> props = css_styles.get(rule);
        //System.out.println("Selector: "+rule+" -> "+buf.toString());
        switch (type){
            case '.':{ //extract objects from uiStore by className (getClass().getSimpleName() == sel)
                MapStream.of(uiStore.getComponents())
                        .filter(x -> x.getValue().getType().equals(sel))
                        .forEach(x -> {
                            checkStyle(rule, x.getValue(), applied_styles, props);
                        });
                break;
            }
            case '#':{
                UIComponent c = this.uiStore.getComponents().get(sel);
                if(c != null) {
                    checkStyle(rule, c, applied_styles, props);
                }
                break;
            }
        }
    }

    private String toStyleString(HashMap<String, String> props){
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String, String> entry : props.entrySet()){
            sb.append(entry.getKey()).append(':').append(entry.getValue()).append(";\n");
        }
        return sb.toString();
    }

    // overrides redefined properties at other rules. (if c1 and c2 have the same prop => use c2.prop)
    // if(id and className have the same prop then use id.prop).
    private void checkStyle(String rule, UIComponent c, HashMap<UIComponent, Pair<String, HashMap<String, String>>> css_meta,
                            HashMap<String, String> props
    )
    {
        //Pair of [RuleName: properties with values]
        Pair<String, HashMap<String, String>> app_props = css_meta.computeIfAbsent(c, k -> new Pair<>(rule, new HashMap<>(props)));
        for(String k : props.keySet()){

            // id rule was previously set.
            if(app_props.getV1().charAt(0) == '#' && rule.charAt(0) != '#'
                && app_props.getV2().containsKey(k))
                continue;
            app_props.getV2().put(k, props.get(k));
        }
        c.setStyle(toStyleString(app_props.getV2()));
    }
}
