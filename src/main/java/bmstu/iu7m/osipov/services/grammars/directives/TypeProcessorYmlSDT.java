package bmstu.iu7m.osipov.services.grammars.directives;

import bmstu.iu7m.osipov.services.grammars.xmlMeta.ClassElement;
import bmstu.iu7m.osipov.services.grammars.xmlMeta.ConstructorElement;
import bmstu.iu7m.osipov.services.grammars.xmlMeta.GenericParameterElement;
import bmstu.iu7m.osipov.services.grammars.xmlMeta.ParameterElement;
import bmstu.iu7m.osipov.services.lexers.LanguageSymbol;
import bmstu.iu7m.osipov.services.lexers.Translation;
import bmstu.iu7m.osipov.structures.lists.LinkedStack;
import bmstu.iu7m.osipov.structures.trees.LinkedNode;
import bmstu.iu7m.osipov.structures.trees.Node;
import bmstu.iu7m.osipov.utils.GrammarBuilderUtils;

import java.util.HashMap;
import java.util.Map;

public class TypeProcessorYmlSDT implements SDTParser, TypeElement {
    private HashMap<String, ClassElement> types;

    private HashMap<String, String> aliases;

    ConstructorElement curCtr;

    LinkedStack<String> pkgs;

    LinkedStack<Object> params;


    public TypeProcessorYmlSDT(){
        this.types = new HashMap<>();
        this.aliases = new HashMap<>();
        this.curCtr = null;
        this.pkgs = new LinkedStack<>();
        this.params = new LinkedStack<>();
    }

    public Map<String, ClassElement> getTypes(){
        return this.types;
    }

    public Map<String, String> getAliases(){
        return this.aliases;
    }

    @Override
    public void exec(Translation t, Node<LanguageSymbol> parent) {
        if (t == null || t.getArguments() == null)
            return;
        String action = t.getActName();

        LinkedNode<LanguageSymbol> l_parent = (LinkedNode<LanguageSymbol>) parent;

        switch (action){
            case "addPkg":{
                String arg1 = t.getArguments().getOrDefault("pkgname", null);
                arg1 = GrammarBuilderUtils.replaceSymRefsAtArgument(l_parent, arg1);
                if(this.pkgs.isEmpty())
                    this.pkgs.push(arg1);
                else
                    this.pkgs.push(this.pkgs.top() + "." + arg1); // add child package with dot '.'
                return;
            }
            case "popPkg":{
                this.pkgs.pop();
                return;
            }
            case "addType":{
                String arg1 = t.getArguments().getOrDefault("typeName", null);
                arg1 = GrammarBuilderUtils.replaceSymRefsAtArgument(l_parent, arg1);
                ClassElement element = null;
                element = this.types.getOrDefault(this.pkgs.top() + "." + arg1, null);
                if(element == null) {
                    element = new ClassElement(arg1, this.pkgs.top());
                    this.types.put(this.pkgs.top() + "." + arg1, element); //save as 'fullPkgName.TypeName' (aka typeFullTName)
                    this.aliases.put(arg1, this.pkgs.top() + "." + arg1); // add reference to typeFullName by TypeName
                }
                ConstructorElement ctr = new ConstructorElement();
                ctr.setClassName(element.getFullName());
                element.addConstructor(ctr);
                this.curCtr = ctr; //save current ctr.
                return;
            }
            case "addParamType":{ // non-generic ctr parameter
                String arg1 = t.getArguments().getOrDefault("paramType", null);
                arg1 = GrammarBuilderUtils.replaceSymRefsAtArgument(l_parent, arg1);
                if(arg1 == null || this.curCtr == null) //Err
                    return;

                ParameterElement param = new ParameterElement(arg1); //type is in arg1.
                if(this.params.top() instanceof GenericParameterElement){
                    GenericParameterElement pgp = (GenericParameterElement) this.params.top(); //parent generic.
                    pgp.addChildParameter(param);
                    return; //do not add to stack as it has no children,
                }
                this.params.push(param); //save parameter.
                return;
            }
            case "addGenericParamType":{ // generic ctr parameter
                String arg1 = t.getArguments().getOrDefault("genType", null);
                arg1 = GrammarBuilderUtils.replaceSymRefsAtArgument(l_parent, arg1);
                if(arg1 == null || this.curCtr == null) //Err
                    return;

                GenericParameterElement gp = new GenericParameterElement(arg1);
                if(this.params.top() instanceof GenericParameterElement){
                    GenericParameterElement pgp = (GenericParameterElement) this.params.top(); //parent generic.
                    pgp.addChildParameter(gp);
                }
                this.params.push(gp); //generic parameter has children
                return;
            }
            case "removeInnerGenType":{
                if(this.params.size() == 1) //do not delete outerMost generic parameter.
                    return;
                if(this.params.top() instanceof GenericParameterElement) //additional check
                    this.params.pop(); //delete inner generic type.
            }

            case "addCtrParam":{ // extract parameter name and add it to parameter.
                String arg1 = t.getArguments().getOrDefault("paramName", null);
                arg1 = GrammarBuilderUtils.replaceSymRefsAtArgument(l_parent, arg1);
                if(arg1 == null || this.curCtr == null) //Err
                    return;
                if(this.params.isEmpty() || !(this.params.top() instanceof ParameterElement))
                    return;
                ParameterElement p = (ParameterElement) this.params.top();
                this.params.pop();
                p.setName(arg1);// add name to ctr parameter.
                this.curCtr.addParameter(p);
                return;
            }
        }
    }
}
