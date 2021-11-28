package bmstu.iu7m.osipov.services.grammars.directives;

import bmstu.iu7m.osipov.services.grammars.xmlMeta.ClassElement;
import bmstu.iu7m.osipov.services.grammars.xmlMeta.ConstructorElement;
import bmstu.iu7m.osipov.services.grammars.xmlMeta.ParameterElement;
import bmstu.iu7m.osipov.services.lexers.LanguageSymbol;
import bmstu.iu7m.osipov.services.lexers.Translation;
import bmstu.iu7m.osipov.structures.lists.LinkedStack;
import bmstu.iu7m.osipov.structures.trees.LinkedNode;
import bmstu.iu7m.osipov.structures.trees.Node;
import bmstu.iu7m.osipov.utils.GrammarBuilderUtils;
import com.eclipsesource.v8.debug.mirror.Scope;

import java.util.HashMap;
import java.util.Map;

public class TypeProcessorSDT implements SDTParser {

    private HashMap<String, ClassElement> types;

    LinkedStack<Object> objects;

    LinkedStack<String> names;

    Map<String, String> obj_attrs;

    public TypeProcessorSDT(){
        this.types = new HashMap<>();
        this.objects = new LinkedStack<>();
        this.names = new LinkedStack<>();
        this.obj_attrs = new HashMap<>();
    }

    public Map<String, ClassElement> getTypes(){
        return this.types;
    }

    //1. Parent is SchemaType and element is already exists -> error
    //2. Parent is SchemaType > create new element.
    //3. ElementName with '.' and it has attributes or parentName before '.' is not equal -> error
    //4. ElementName with '.' and property is 'Constructors' > init property.
    //5. ElementName with '.' and property is 'Params' > init property.
    @Override
    public void exec(Translation t, Node<LanguageSymbol> parent) {
        if(t == null || t.getArguments() == null)
            return;
        String action = t.getActName();

        LinkedNode<LanguageSymbol> l_parent = (LinkedNode<LanguageSymbol>) parent;

        switch (action){
            case "addPrefix": {
                String arg1 = t.getArguments().getOrDefault("pref", null);
                arg1 = GrammarBuilderUtils.replaceSymRefsAtArgument(l_parent, arg1);
                this.names.push(arg1);
                break;
            }

            case "removePrefix": {
                String arg1 = t.getArguments().getOrDefault("pref", null);
                arg1 = GrammarBuilderUtils.replaceSymRefsAtArgument(l_parent, arg1);
                String arg2 = t.getArguments().getOrDefault("closed", null); //is closed tag <name/>
                if(this.names.top() == null){ //no parents and elems!
                    System.out.println("Error: Illegal sequence of tags.");
                    break;
                }
                else if(arg2 == null && !this.names.top().equals(arg1)){ // not matched by parent open tag <name>.
                    System.out.println("Enclosing tag: </"+arg1+"> is not matched with opening: <"+this.names.top()+">");
                    break;
                }
                Object cur = this.objects.top(); //may be null at </SchemaTypes>
                if(arg1.equals("Constructor")) { // </Constructor> tag.
                    if(! (cur instanceof ConstructorElement)){
                        System.out.println("Error: At tag </Constructor> cannot find created object.");
                        break;
                    }
                    this.objects.pop();
                    Object par = this.objects.top();
                    if( !(par instanceof ClassElement)){ // Illegal parent Tag of <Constructor>
                        System.out.println("Error: At tag </Constructor>, parent tag must be type!");
                        break;
                    }
                    ClassElement claz = (ClassElement) par;
                    ConstructorElement ctr = (ConstructorElement) cur;
                    claz.addConstructor(ctr);
                }
                else if(!arg1.contains(".") && arg2 == null){ //non-empty tag and not property-object marker.
                    this.objects.pop();
                }

                this.names.pop();
                break;
            } //end removePrefix

            case "putAttr": {
                String arg1 = t.getArguments().getOrDefault("key", null);
                String arg2 = t.getArguments().getOrDefault("val", null);
                arg1 = GrammarBuilderUtils.replaceSymRefsAtArgument(l_parent, arg1);
                arg2 = GrammarBuilderUtils.replaceSymRefsAtArgument(l_parent, arg2);
                this.obj_attrs.put(arg1, arg2);

                break;
            }
            case "createObject": {
                if(this.names.top() == null){
                    System.out.println("Error: Illegal sequence of tags.");
                    break;
                }
                String arg1 = t.getArguments().getOrDefault("className",null);
                arg1 = GrammarBuilderUtils.replaceSymRefsAtArgument(l_parent, arg1);
                if (
                        (arg1.contains(".") && this.obj_attrs.entrySet().size() > 0) ||
                                (arg1.contains(".") && this.objects.top() == null) ||
                                (arg1.contains(".") && (
                                        this.names.topFrom(1) == null ||
                                        !this.names.topFrom(1).equals(arg1.substring(0, arg1.indexOf('.')))))
                )
                {
                    System.out.println("Error: at Tag <"+arg1+">\n\t Complex attribute must be defined withing an object and have no String attributes!");
                    System.out.println("Note that name before '.' must be the same as enclosing Tag name!");
                    break; //complex property must be defined within object and its attributes are Tags and not str=vals pairs!
                }
                if(arg1.contains(".")){ //well-formed complex property
                    break;
                }

                if(this.names.top().equals("SchemaTypes")) { //root element.
                    this.obj_attrs.clear();
                    break;
                }

                this.names.pop();
                if(this.names.top() == null){ //root was extracted previously!
                    System.out.println("Error: Illegal sequence of tags.");
                    this.names.push(arg1);//put back for 'removePrefix'
                    break;
                }

                if(this.names.top().equals("SchemaTypes") //non-unique type name at SchemaTypes
                    && this.types.getOrDefault(this.names.top(), null) != null
                )
                {
                    System.out.println("Error: at Tag <"+arg1+">\n\t It is already defined at <SchemaTypes>. Only unique type names are allowed.");
                    break;
                }
                else if(this.names.top().equals("SchemaTypes")){ //new type definition.
                    ClassElement element = new ClassElement(arg1);
                    this.types.put(arg1, element);
                    this.objects.push(element);
                    this.obj_attrs.clear();
                    this.names.push(arg1);//put back for 'removePrefix' SchemaTypes > Stage
                    break;
                }

                this.names.push(arg1); //put back after root element checks
                if(this.names.top().equals("Constructor")
                        && (this.objects.top() == null ||
                                !(this.objects.top() instanceof ClassElement)
                            )
                )
                {
                    System.out.println("Error: Constructor without type!");
                    break;
                }
                else if(this.names.top().equals("Constructor")){
                    ConstructorElement ctr = new ConstructorElement();
                    this.objects.push(ctr);
                    this.obj_attrs.clear();
                    break;
                }
                else if(this.names.top().equals("Param")){ //new param definition.
                    this.names.pop();
                    if(this.names.top() == null){ //no parent.
                        System.out.println("Error: Illegal sequence of tags.");
                        break;
                    }
                    if(this.names.top().equals("Constructor.Params")){ //parent: Constructor.Params
                        if(!(this.objects.top() instanceof ConstructorElement)){
                            System.out.println("tag <Constructor.Params> defined at wrong element.");
                            break;
                        }
                        ParameterElement param = new ParameterElement(
                                obj_attrs.getOrDefault("type", null),
                                obj_attrs.getOrDefault("name", null)
                        );

                        ConstructorElement ctr = (ConstructorElement) this.objects.top();
                        ctr.addParameter(param);
                    }

                    this.obj_attrs.clear();
                    this.names.push("Param");
                }
                break;
            } //end 'createObject' action.

            case "showAttrs": {
                System.out.println("BEGIN");
                for(String k: this.types.keySet()){
                    System.out.println(this.types.get(k));
                }
                System.out.println("END");
                break;
            }
        } // end switch
    }
}