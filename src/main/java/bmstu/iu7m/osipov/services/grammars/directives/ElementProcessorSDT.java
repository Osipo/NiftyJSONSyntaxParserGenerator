package bmstu.iu7m.osipov.services.grammars.directives;

import bmstu.iu7m.osipov.services.lexers.LanguageSymbol;
import bmstu.iu7m.osipov.services.lexers.Translation;
import bmstu.iu7m.osipov.structures.lists.LinkedStack;
import bmstu.iu7m.osipov.structures.trees.LinkedNode;
import bmstu.iu7m.osipov.structures.trees.Node;
import bmstu.iu7m.osipov.utils.GrammarBuilderUtils;

import java.util.HashMap;
import java.util.Map;

public class ElementProcessorSDT implements SDTParser {

    private AttributeProcessorSDT attr_processor;

    String curName;

    LinkedStack<Object> objects;

    LinkedStack<String> props_objects;

    Map<String, String> obj_attrs;

    public ElementProcessorSDT(AttributeProcessorSDT attr_processor){
        this.curName = null;
        this.objects = new LinkedStack<>();
        this.props_objects = new LinkedStack<>();
        this.obj_attrs = new HashMap<>();
        this.attr_processor = attr_processor;
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


                //this.attr_processor.putAttribute(obj_attrs, arg1, arg2);
                this.obj_attrs.put(arg1, arg2);

                break;
            }
            case "createObject": {
                String arg1 = t.getArguments().getOrDefault("className",null);
                arg1 = GrammarBuilderUtils.replaceSymRefsAtArgument(l_parent, arg1);
                if (
                        (arg1.contains(".") && this.obj_attrs.entrySet().size() > 0) ||
                        (arg1.contains(".") && this.objects.top() == null) ||
                        (arg1.contains(".") && (this.curName == null || !this.curName.equals(arg1.substring(0, arg1.indexOf('.')))))
                )
                {
                    System.out.println("Error: at Tag <"+arg1+">\n\t Complex attribute must be defined withing an object and have no String attributes!");
                    System.out.println("Note that name before '.' must be the same as enclosing Tag name!");
                    break; //complex property must be defined within object and its attributes are Tags and not str=vals pairs!
                }
                if(arg1.contains(".")){ //well-formed complex property
                    Object pObj = this.objects.top();
                }
                else {
                    this.curName = arg1;
                    System.out.println("{");
                    System.out.println("\tobject: "+this.curName);
                    System.out.println("\tattrs: "+this.obj_attrs.toString());
                    System.out.println("}");
                    this.obj_attrs.clear(); //clear str-valued attrs.
                }
            } //end 'createObject' action
        } // end switch
    }
}