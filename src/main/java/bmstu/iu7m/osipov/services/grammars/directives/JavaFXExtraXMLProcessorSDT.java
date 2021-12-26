package bmstu.iu7m.osipov.services.grammars.directives;

import bmstu.iu7m.osipov.services.grammars.xmlMeta.ClassElement;
import bmstu.iu7m.osipov.services.grammars.xmlMeta.ConstructorElement;
import bmstu.iu7m.osipov.services.grammars.xmlMeta.ParameterElement;
import bmstu.iu7m.osipov.services.grammars.xmlMeta.SceneConstructor;
import bmstu.iu7m.osipov.services.lexers.LanguageSymbol;
import bmstu.iu7m.osipov.services.lexers.Translation;
import bmstu.iu7m.osipov.structures.lists.KeyValuePair;
import bmstu.iu7m.osipov.structures.lists.LinkedStack;
import bmstu.iu7m.osipov.structures.trees.LinkedNode;
import bmstu.iu7m.osipov.structures.trees.Node;
import bmstu.iu7m.osipov.utils.ClassObjectBuilder;
import bmstu.iu7m.osipov.utils.GrammarBuilderUtils;
import bmstu.iu7m.osipov.utils.PrimitiveTypeConverter;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class JavaFXExtraXMLProcessorSDT implements SDTParser {
    protected Map<String, String> obj_attrs;
    protected Map<String, Object> res;
    protected Map<String, Object> fragments;
    protected LinkedStack<Object> objects;
    protected Object root;
    protected String curName;

    /* State initial = 0. */
    /*
    * -1 - Error.
    * 0 - recognize root (awaiting Stage, Resource or Fragment) (goto 1, 5, 6)
    * 1 - Stage recognized and created. awaiting Scene object or Resources.
    *   10 - recognized Stage.Resources. reading Stage.Resources content
    *   11 - read Resource file (dictionary)
    *   12 - read Fragment content
    *   30 - reading Array of items when sequence needed.
    * 2 - read Scene constructor properties.
    * 3 - read rootNode of the Scene.
    * 4 - read content of the rootNode of the Scene.
    *   40 - </Scene> was read. All content of the Scene has been created. Awaiting other Resources.
    *   4o -> 10.
    * 5 - read content of the root Resource
    * 6 - read content of the root Fragment
    * 7 - </Stage> was reached. Nothing to awaits. Finished state.
    */
    protected int state;

    // Type meta descriptor.
    private TypeProcessorSDT type_processor;

    public JavaFXExtraXMLProcessorSDT(TypeProcessorSDT type_processor){
        this.objects = new LinkedStack<>();
        this.obj_attrs = new HashMap<>();
        this.res = new HashMap<>();
        this.fragments = new HashMap<>();
        this.state = 0;
        this.type_processor = type_processor;
    }

    public void restart(){
        this.obj_attrs.clear();
        this.objects.clear();
        this.res.clear();
        this.fragments.clear();
        this.root = null;
        this.state = 0;
    }

    protected void setRoot(Object root){
        this.root = root;
    }

    public Object getRoot(){
        return root;
    }

    @Override
    public void exec(Translation t, Node<LanguageSymbol> parent) {
        if (t == null || t.getArguments() == null || this.state == 7) {
            return;
        }
        else if(this.state == -1){
            restart();
            return;
        }

        String action = t.getActName();
        LinkedNode<LanguageSymbol> l_parent = (LinkedNode<LanguageSymbol>) parent;

        switch (action){
            case "putAttr": {
                String arg1 = t.getArguments().getOrDefault("key", null);
                String arg2 = t.getArguments().getOrDefault("val", null);
                arg1 = GrammarBuilderUtils.replaceSymRefsAtArgument(l_parent, arg1);
                arg2 = GrammarBuilderUtils.replaceSymRefsAtArgument(l_parent, arg2);

                arg2 = arg2.substring(1, arg2.length() - 1); // unquote value.
                this.obj_attrs.put(arg1, arg2);
                break;
            }//end 'putAttr'

            case "createObject": {
                String arg1 = t.getArguments().getOrDefault("className",null);
                arg1 = GrammarBuilderUtils.replaceSymRefsAtArgument(l_parent, arg1);
                this.curName = arg1;
                changeState();
                checkState();
                this.obj_attrs.clear();
                break;
            }//end 'createObject'

            case "removePrefix":{
                String clTag = t.getArguments().getOrDefault("pref", null);
                clTag = GrammarBuilderUtils.replaceSymRefsAtArgument(l_parent, clTag);
                break;
            }//end 'removePrefix'

        }//end switch
    }//end method

    private void changeState(){
        if(this.state == -1)
            return;
        if(this.state == 0 && this.curName.equalsIgnoreCase("Stage"))
            this.state = 1;
        else if(this.state == 0 && this.curName.equalsIgnoreCase("Resource"))
            this.state = 5;
        else if(this.state == 0 && this.curName.equalsIgnoreCase("Fragment"))
            this.state = 6;
        else if(this.state == 0) // Error element must be IN (<Stage>, <Fragment>, <Resource>)
            this.state = -1;
        else if(this.state == 1 && this.curName.equalsIgnoreCase("Scene"))
            this.state = 2;
        else if(this.state == 1 && this.curName.equalsIgnoreCase("Stage.Resources"))
            this.state = 10;
        else if(this.state == 10 && this.curName.equalsIgnoreCase("Resource"))
            this.state = 11;
        else if(this.state == 10 && this.curName.equalsIgnoreCase("Fragment"))
            this.state = 12;
        else if(this.state == 10 && (this.curName.equalsIgnoreCase("Array")
            || this.curName.equalsIgnoreCase("List")
        ))
            this.state = 30;
        else if(this.state == 2)
            this.state = 3;
        else if(this.state == 3)
            this.state = 4;
        else if(this.state == 40 && this.curName.equalsIgnoreCase("Stage.Resources"))
            this.state = 10;
    }

    private void checkState(){
        KeyValuePair<Constructor<?>, Object[]> ctr_with_vals = null;
        ConstructorElement meta_ctr = null;

        switch (this.state){
            case 0: case -1:{ //Cannot find Stage at start OR Error.
                return;
            }
            case 1:{ /* curName = 'Stage' */
                meta_ctr = getTypeConstructorElement();
                if(meta_ctr == null)
                    break;
                ctr_with_vals = getConstructorWithValues(meta_ctr);
                if(ctr_with_vals == null)
                    break;

                Object stage = ClassObjectBuilder.createInstance(ctr_with_vals.getKey(), ctr_with_vals.getValue(), PrimitiveTypeConverter::convertConstructorArguments, 0);
                if(stage == null)
                    break;
                this.objects.push(stage); //push new root node.
                processSimpleProperties(stage);
                return;
            }
            case 2:{ /* curName = 'Scene' */

                // Extract Scene constructor.
                meta_ctr = getTypeConstructorElement();
                if(meta_ctr == null){
                    try {
                        ctr_with_vals = new KeyValuePair<>(
                                Scene.class.getDeclaredConstructor(Parent.class),
                                new Object[] { null }
                        );
                    } catch (NoSuchMethodException | SecurityException e) { }
                }
                else{
                    ctr_with_vals = getConstructorWithValues(meta_ctr);
                }
                this.objects.push(ctr_with_vals); // save Scene constructor with args.
                return;
            }
            case 3: { /* Create Scene. Create root Node of the Scene graph, AND set Scene. */
                meta_ctr = getTypeConstructorElement();
                if(meta_ctr == null)
                    break;
                ctr_with_vals = getConstructorWithValues(meta_ctr);
                if(ctr_with_vals == null)
                    break;
                Object sceneRoot = ClassObjectBuilder.createInstance(ctr_with_vals.getKey(), ctr_with_vals.getValue(), PrimitiveTypeConverter::convertConstructorArguments, 0);
                if(sceneRoot == null)
                    break;
                if(!processScene(sceneRoot))
                    break;
                processSimpleProperties(sceneRoot);
                return;
            }
            case 4: { /* Scene graph content */
                meta_ctr = getTypeConstructorElement();
                if(meta_ctr == null)
                    break;
                ctr_with_vals = getConstructorWithValues(meta_ctr);
                if(ctr_with_vals == null)
                    break;
                Object elem_or_layout = ClassObjectBuilder.createInstance(ctr_with_vals.getKey(), ctr_with_vals.getValue(), PrimitiveTypeConverter::convertConstructorArguments, 0);
                if(elem_or_layout == null)
                    break;
                if(!processChildren(elem_or_layout))
                    break;
                this.objects.push(elem_or_layout); //push new root node.
                processSimpleProperties(elem_or_layout);
                return;
            }
        }//end switch
        this.state = -1; //if reached here -> no return operator performed -> 'break' -> Error
    }//end method

    private ConstructorElement getTypeConstructorElement(){
        //1. get className and its meta data.
        String fullTypeName = this.type_processor
                .getAliases()
                .getOrDefault(this.curName, null);

        ClassElement class_meta = this.type_processor
                .getTypes()
                .getOrDefault(fullTypeName, null);
        if(class_meta == null)
            return null;

        //2. Find matching constructor.
        int m = 0; //MAX matches
        int m_i = 0; //matches at ctr_i.
        int idx = -1; //ctr index.
        int idx_i = -1;//idx iterator at cycle
        if(class_meta.getConstructors() == null)
           return null;

        for(ConstructorElement ctr : class_meta.getConstructors()){
            m_i = 0;
            idx_i++;
            if((ctr.getParams() == null || ctr.getParams().size() == 0) && idx == -1){
                idx = idx_i;
                continue;
            }
            else if(ctr.getParams() == null || ctr.getParams().size() == 0){
                continue;
            }
            for(ParameterElement param : ctr.getParams()){
                if(obj_attrs.getOrDefault(param.getName(), null) != null){
                    m_i++;
                }
            }
            if(m_i > m){
                m = m_i;
                idx  = idx_i;
            }
        }
        return (idx == -1) ? null : class_meta.getConstructors().get(idx);
    }


    private KeyValuePair<Constructor<?>, Object[]> getConstructorWithValues(ConstructorElement meta_ctr){
        int pargs = 0;
        int ii = 0;
        if(!meta_ctr.haveNoParams())//for no-args constructor: getParams() returns null.
            pargs = meta_ctr.getParams().size();

        Class<?>[] ctr_types = (pargs == 0) ? null : new Class[pargs];
        Object[] vals = new Object[pargs];
        if(this.state == 2 && ctr_types != null){
            ctr_types[0] = Parent.class;
            ii = 1;
        }
        for(;ii < pargs; ii++){
            try {
                Class<?> ptype = ClassObjectBuilder.getPrimitiveTypes().getOrDefault(meta_ctr.getParams().get(ii).getType(), null);
                ptype = (ptype != null) ? ptype : Class.forName(meta_ctr.getParams().get(ii).getType());
                ctr_types[ii] = ptype;
                vals[ii] = obj_attrs.getOrDefault(meta_ctr.getParams().get(ii).getName(), null);
                obj_attrs.remove(meta_ctr.getParams().get(ii).getName());
            } catch (ClassNotFoundException ex){
                return null;
            }
        }

        return new KeyValuePair<>(ClassObjectBuilder.getDeclaredConstructor(meta_ctr.getClassName(), ctr_types), vals);
    }

    private boolean processChildren(Object child){
        Object parent = this.objects.top();
        if(parent == null || child == null)
            return false;

        Method m = ClassObjectBuilder.getMethod(parent, "getChildren");
        m = (m == null) ? ClassObjectBuilder.getMethod(parent, "getItems") : m;
        if(m == null)
            return false;

        Object children = null;
        try{
            children = m.invoke(parent);
            m = children.getClass().getMethod("addAll", Object[].class);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e){
            return false;
        }
        try {
            // WRAP VARARGS WITH Object[] array.
            // Second array may have specific type
            // But as it is generic then the type is Object.
            m.invoke(children, new Object[]{ new Object[]{ child } });
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e){
            return false;
        }
        return true;
    }

    private boolean processScene(Object root){
        if(root == null)
            return false;
        KeyValuePair<Constructor<?>, Object[]> scene_ctr_with_args = null;
        try{
            scene_ctr_with_args = (KeyValuePair<Constructor<?>, Object[]>) this.objects.top();
        } catch (ClassCastException | NullPointerException e){
            return false;
        }
        if(scene_ctr_with_args.getKey() == null)
            return false;
        if(scene_ctr_with_args.getValue() == null)
            return false;
        this.objects.pop(); //remove scene constructor from Stack.

        Constructor<?> scene_ctr = scene_ctr_with_args.getKey();
        Object[] args = scene_ctr_with_args.getValue();
        args[0] = root;
        Object stage = this.objects.top(); //extract Stage object
        if(stage == null)
            return false;

        Object scene = ClassObjectBuilder.createInstance(scene_ctr, args, PrimitiveTypeConverter::convertConstructorArguments, 1);
        if(scene == null)
            return false;

        // Call stage.setScene(scene)
        Method setScene = null;
        try{
            setScene = stage.getClass().getDeclaredMethod("setScene", Scene.class);
            setScene.invoke(stage, scene);
        } catch (NoSuchMethodException | SecurityException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e){
            return false;
        }
        this.objects.push(scene); // add Scene object with sceneRoot.
        this.objects.push(root);
        return true;
    }

    private void processSimpleProperties(Object root){
        if(root == null)
            return;
        String methodName = null;
        Method m = null;
        System.out.println("Process properties of: "+root.getClass().getSimpleName());
        for(Map.Entry<String, String> entry : this.obj_attrs.entrySet()) {
            if (entry.getValue() == null)
                continue;

            methodName = entry.getKey();
            System.out.println("Looking setter for: '" + methodName + "'");
            if (methodName.contains(".")) {
                processStaticProperty(methodName, root, entry.getValue());
                continue;
            }
            m = ClassObjectBuilder.getMethod(root, "set" + methodName);
            if(m == null)
                m = ClassObjectBuilder.getMethod(root, "init" + methodName);
            if(m == null)
                continue;
            try{
                System.out.println("Found instance setter prop: "+methodName);
                Class<?> propType = m.getParameters()[0].getType();
                System.out.println("Property type: "+propType.getSimpleName());

                /* if Resource setter */
                if(entry.getValue().charAt(0) == '{'
                        && entry.getValue().charAt(entry.getValue().length() - 1) == '}'
                )
                {
                    String rkey = entry.getValue().substring(1, entry.getValue().length() - 1);
                    m.invoke(root, this.res.getOrDefault(rkey, null));
                }
                else{
                    m.invoke(root, PrimitiveTypeConverter.castTo(propType, entry.getValue()));
                }

            } catch (InvocationTargetException | IllegalArgumentException | IllegalAccessException e){
                System.out.println(e);
            }// end try/catch
        }// end for cycle
    }//end method

    private void processStaticProperty(String setter, Object root, String val){
        String fullTypeName = this.type_processor
                .getAliases()
                .getOrDefault(setter.substring(0, setter.indexOf('.')), null);
        if(fullTypeName == null){
            System.out.println("Cannot find class: '"+setter.substring(0, setter.indexOf('.')) + "'");
            return;
        }
        String setterName = setter.substring(setter.indexOf('.') + 1);
        try {
            Class<?> clazz = Class.forName(fullTypeName);

            Method m = ClassObjectBuilder.getClassMethod(clazz, setterName);
            m = (m == null) ? ClassObjectBuilder.getClassMethod(clazz, "set" + setterName) : m;
            m = (m == null) ? ClassObjectBuilder.getClassMethod(clazz, "init" + setterName) : m;
            if(m == null){
                System.out.println("Cannot find static property setter: '"+ setterName+ "'");
                return;
            }
            /* if Resource setter */
            if(val.charAt(0) == '{'
                    && val.charAt(val.length() - 1) == '}'
            )
            {
                String rkey = val.substring(1, val.length() - 1);
                m.invoke(null, root, this.res.getOrDefault(rkey, null));
            }
            else{
                m.invoke(null, root, PrimitiveTypeConverter.castTo(clazz, val));
            }
        } catch (ClassNotFoundException | InvocationTargetException | IllegalArgumentException | IllegalAccessException e){
            System.out.println(e);
        }
    }
}