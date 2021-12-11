package bmstu.iu7m.osipov.services.grammars.directives;

import bmstu.iu7m.osipov.services.grammars.xmlMeta.ClassElement;
import bmstu.iu7m.osipov.services.grammars.xmlMeta.ConstructorElement;
import bmstu.iu7m.osipov.services.grammars.xmlMeta.ParameterElement;
import bmstu.iu7m.osipov.services.grammars.xmlMeta.SceneConstructor;
import bmstu.iu7m.osipov.services.lexers.LanguageSymbol;
import bmstu.iu7m.osipov.services.lexers.Translation;
import bmstu.iu7m.osipov.structures.lists.LinkedStack;
import bmstu.iu7m.osipov.structures.trees.LinkedNode;
import bmstu.iu7m.osipov.structures.trees.Node;
import bmstu.iu7m.osipov.utils.ClassObjectBuilder;
import bmstu.iu7m.osipov.utils.GrammarBuilderUtils;
import bmstu.iu7m.osipov.utils.PrimitiveTypeConverter;
import bmstu.iu7m.osipov.utils.ProcessNumber;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ElementProcessorSDT implements SDTParser {

    private AttributeProcessorSDT attr_processor;

    private TypeProcessorSDT type_processor;

    protected static Map<String, Class<?>> primitives;

    protected static Map<String, Class<?>> boxedTypes;

    protected Map<String, Object> resources;

    static {
        primitives = new HashMap<>();
        primitives.put("boolean", boolean.class);
        primitives.put("byte", byte.class);
        primitives.put("char",char.class);
        primitives.put("short",short.class);
        primitives.put("int", int.class);
        primitives.put("long", long.class);
        primitives.put("float", float.class);
        primitives.put("double", double.class);

        boxedTypes = new HashMap<>();
        boxedTypes.put("boolean", Boolean.class);
        boxedTypes.put("byte", Byte.class);
        boxedTypes.put("char",Character.class);
        boxedTypes.put("short",Short.class);
        boxedTypes.put("int", Integer.class);
        boxedTypes.put("long", Long.class);
        boxedTypes.put("float", Float.class);
        boxedTypes.put("double", Double.class);
    }

    public static Map<String, Class<?>> getPrimitiveTypes(){
        return primitives;
    }
    public static Map<String, Class<?>> getBoxedTypes(){
        return boxedTypes;
    }

    /*state machine for javafx-objects.
        -1 = Error.
        0 = Awaiting Stage (creates Stage and goto 1)
        1 = Awaiting Scene (remember constructor of Scene and goto 2)
        10 = Read resource objects (java Objects and some javafx style Objects like Background, Insets, BackgroundFill)
        2 = Awaiting Root (extract Scene constructor with its parent Stage and goto 3)
        3 = Read Scene graph.
     */
    private int state = 0;

    private SceneConstructor sceneCtr;
    private Object stage; //root stage.

    String curName;

    LinkedStack<Object> objects;

    LinkedStack<String> props_objects;

    Map<String, String> obj_attrs;

    public ElementProcessorSDT(AttributeProcessorSDT attr_processor, TypeProcessorSDT type_processor){
        this.curName = null;
        this.objects = new LinkedStack<>();
        this.props_objects = new LinkedStack<>();
        this.obj_attrs = new HashMap<>();
        this.attr_processor = attr_processor;
        this.type_processor = type_processor;
        this.resources = new HashMap<>();
    }

    public Object getStage(){
        return this.stage;
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

                arg2 = arg2.substring(1, arg2.length() - 1);
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
                    String pTag = arg1.substring(0, arg1.indexOf('.'));
                    if(pTag.equals("Stage") && this.state == 1){
                        this.state = 10;
                    }
                }
                else {
                    this.curName = arg1;
                    System.out.println("{");
                    System.out.println("\tobject: "+this.curName);
                    System.out.println("\tattrs: "+this.obj_attrs.toString());
                    System.out.println("}");

                    if(!this.curName.equals("Stage") && state == 0)
                        this.state = -1;
                    if(!this.curName.equals("Scene") && state == 1)
                        this.state = -1;

                    if(this.state == -1){
                        this.obj_attrs.clear();
                        break;
                    }

                    //1. get className and its meta data.
                    String fullTypeName = this.type_processor
                            .getAliases()
                            .getOrDefault(arg1, null);

                    ClassElement class_meta = this.type_processor
                            .getTypes()
                            .getOrDefault(fullTypeName, null);
                    if(class_meta == null){
                        this.obj_attrs.clear(); //clear str-valued attrs.
                        break;
                    }

                    //2. Find matching constructor.
                    int m = 0; //MAX matches
                    int m_i = 0; //matches at ctr_i.
                    int idx = -1; //ctr index.
                    int idx_i = -1;//idx iterator at cycle
                    HashMap<String, String> ctr_params = new HashMap<>();
                    if(class_meta.getConstructors() == null){
                        this.obj_attrs.clear();
                        break;
                    }
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
                    //constructor not found. If element is <Scene> then suppose
                    // that called Scene(Parent root) .ctor.
                    if(idx == -1 && this.state != 1){
                        this.obj_attrs.clear();
                        break;
                    }

                    /* <Scene> object detected */
                    if(this.state == 1) {
                        Constructor<?> ctr = null;
                        if (idx == -1) {
                            try {
                                ctr = Scene.class.getDeclaredConstructor(Parent.class);
                            } catch (NoSuchMethodException | SecurityException e) { }
                            this.sceneCtr = new SceneConstructor(ctr, new Object[] { null});
                        } else {
                            ConstructorElement ctr_i = class_meta.getConstructors().get(idx);
                            int spargs = ctr_i.getParams().size();
                            ;
                            Class<?>[] ctr_types = new Class[spargs];
                            ctr_types[0] = Parent.class;
                            Object[] vals = new Object[spargs];
                            for (int ii = 1; ii < spargs; ii++) {
                                try {
                                    Class<?> ptype = primitives.getOrDefault(ctr_i.getParams().get(ii).getType(), null);
                                    ptype = (ptype != null) ? ptype : Class.forName(ctr_i.getParams().get(ii).getType());
                                    ctr_types[ii] = ptype;
                                    vals[ii] = obj_attrs.getOrDefault(ctr_i.getParams().get(ii).getName(), null);
                                    obj_attrs.remove(ctr_i.getParams().get(ii).getName());
                                } catch (ClassNotFoundException ex) {
                                    //System.out.println("Cannot find class: '"+ctr_i.getParams().get(ii).getType()+"'");
                                }
                            }
                            ctr = ClassObjectBuilder.getDeclaredConstructor(fullTypeName, ctr_types);
                            this.sceneCtr = new SceneConstructor(ctr, vals);
                        }
                        this.obj_attrs.clear();
                        this.state = 2;
                        break;
                    }

                    //3. Extract parameters and their types.
                    ConstructorElement ctr_i = class_meta.getConstructors().get(idx);
                    int pargs = 0;

                    //if constructor HaveNoParams then Params is null else get size.
                    if(!ctr_i.haveNoParams())//for no-args constructor: parameterTypes are null.
                        pargs = ctr_i.getParams().size();
                    Class<?>[] ctr_types = (pargs == 0) ? null : new Class[pargs];
                    Object[] vals = new Object[pargs];
                    for(int ii = 0; ii < pargs; ii++){
                        try {
                            Class<?> ptype = primitives.getOrDefault(ctr_i.getParams().get(ii).getType(), null);
                            ptype = (ptype != null) ? ptype : Class.forName(ctr_i.getParams().get(ii).getType());
                            ctr_types[ii] = ptype;
                            vals[ii] = obj_attrs.getOrDefault(ctr_i.getParams().get(ii).getName(), null);
                            obj_attrs.remove(ctr_i.getParams().get(ii).getName());
                        } catch (ClassNotFoundException ex){}
                    }

                    processChildren(ClassObjectBuilder.createInstance(fullTypeName, ctr_types, vals));
                    ctr_types = null;
                    vals = null;

                    this.obj_attrs.clear(); //clear str-valued attrs.
                    if(this.curName.equals("Stage") && state == 0) {
                        this.state = 1;
                        this.stage = this.objects.top();
                    }
                }
                break;
            } //end 'createObject' action

            case "removePrefix":{
                String clTag = t.getArguments().getOrDefault("pref", null);
                clTag = GrammarBuilderUtils.replaceSymRefsAtArgument(l_parent, clTag);
                if(clTag.contains(".") && clTag.substring(0, clTag.indexOf('.')).equals("Stage")) {
                    this.state = 1; // 10 -> 1.
                    break;
                }
                else if(this.state == 10)
                    break;
                if(this.objects.top() != null)
                    System.out.println(this.objects.top().getClass().getSimpleName());
                this.objects.pop();
                break;
            }
        } // end switch
    }

    //try to find parent with methods getChildren or getItems
    //on collection returned by these methods the 'add' method invoked.
    //'add' appends child 'c' to the collection.
    private void processChildren(Object c){
        System.out.println("state = "+this.state + " Stack: "+this.objects.toString());
        if(c == null) {
            System.out.println("Cannot create object of class '"+this.curName+"'");
            return;
        }
        if(this.state == 10){
            processSimpleProperties(c);
            this.resources.put(this.obj_attrs.get("key"), c);
            return;
        }
        if(this.state == 2){
            processScene(c);
            processSimpleProperties(c);
            return;
        }
        Object parent = this.objects.top();
        if(parent == null){
            this.objects.push(c);
            processSimpleProperties(c);
            return;
        }
        Method m = null;
        try {
            m = parent.getClass().getMethod("getChildren");
        } catch (NoSuchMethodException | SecurityException e){}
        if(m == null){
            try{
                m = parent.getClass().getMethod("getItems");
            } catch (NoSuchMethodException | SecurityException e){}
        }
        if(m == null) {
            System.out.println("No such method getChildren or getItems");
            return;
        }

        Object children = null;
        try{
            children = m.invoke(parent);
            m = children.getClass().getMethod("addAll", Object[].class);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                    | NoSuchMethodException | SecurityException e){
            m = null;
            System.out.println("method add not found.");
            children = null;
        }
        if(m == null || children == null)
            return;
        try {
            // WRAP VARARGS WITH Object[] array.
            // Second array may have specific type
            // But as it is generic then the type is Object.
            m.invoke(children, new Object[]{ new Object[]{ c } });
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e){
            System.out.println("cannot add child. Illegal child type (argument to addAll)");
            System.out.println(e);
        }

        this.objects.push(c);
        processSimpleProperties(c);
    }//end method.

    private void processScene(Object root){
        Object stage = this.objects.top();
        Object scene = null;
        try{
            Object[] args = this.sceneCtr.getArgs();
            args[0] = root;
            Constructor<?> c = this.sceneCtr.getConstrutor();
            if(c == null)
                System.out.println("NULL SCENE CONSTRUCTOR");
            else {
                Parameter[] actual_params = c.getParameters();
                for(int i = 1; i < args.length; i++){
                    args[i] = PrimitiveTypeConverter.castTo(actual_params[i].getType(), args[i].toString());
                }
                scene = c.newInstance(args);
            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e){
            System.out.println(e);
        }

        if(scene == null)
            System.out.println("Cannot create Scene with Parent: "+root.getClass().getSimpleName());

        Method setScene = null;
        try{
            setScene = stage.getClass().getDeclaredMethod("setScene", Scene.class);
            setScene.invoke(stage, scene);
        } catch (NoSuchMethodException | SecurityException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e){}
        this.objects.push(scene);
        this.objects.push(root);
        this.state = 3;
    }

    private void processSimpleProperties(Object c){
        if(c == null)
            return;
        String methodName = null;
        Method m = null;
        System.out.println("Process properties of: "+c.getClass().getSimpleName());
        for(Map.Entry<String, String> entry : this.obj_attrs.entrySet()){
            if(entry.getValue() == null)
                continue;
            methodName = entry.getKey();
            System.out.println("Looking for: '"+methodName+"'");
            if(methodName.contains(".")){
                processStaticProperties(methodName, c, entry.getValue());
                continue;
            }

            m = ClassObjectBuilder.getMethod(c, "set" + methodName);
            if(m == null)
                m = ClassObjectBuilder.getMethod(c, "init" + methodName);

            /* inherited Properties.
            if(methodName.equalsIgnoreCase("style") || methodName.equalsIgnoreCase("id")){
                m = ClassObjectBuilder.getMethod(c, "set" + methodName);
                m = (m == null) ?  ClassObjectBuilder.getMethod(c, "init" + methodName) : m;
            }*/
            if(m == null)
                continue;
            try {
                System.out.println("Found setter prop: "+methodName);
                Class<?> propType = m.getParameters()[0].getType();
                System.out.println("Property type: "+propType.getSimpleName());

                /* If String setter */
                if(propType.getSimpleName().equalsIgnoreCase("String"))
                    m.invoke(c, entry.getValue());

                /* If boolean setter */
                else if(propType.getSimpleName().equals("boolean")){
                    m.invoke(c, entry.getValue().equals("true"));
                }

                /* If Enum setter */
                else if(propType.isEnum()){
                    m.invoke(c,
                            Enum.valueOf((Class<? extends Enum>)propType, entry.getValue())
                    );
                }

                /* Resource setter */
                else if(entry.getValue().charAt(0) == '{'
                        && entry.getValue().charAt(entry.getValue().length() - 1) == '}'
                )
                {
                    String rkey = entry.getValue().substring(1, entry.getValue().length() - 1);
                    m.invoke(c, this.resources.getOrDefault(rkey, null));
                }

                /* Number setter */
                else {
                    m.invoke(c, ProcessNumber.parseNumber(entry.getValue(), propType));
                }
            }catch (InvocationTargetException | IllegalArgumentException | IllegalAccessException e){
                System.out.println(e);
            }
        }
    } //end method.

    //Process properties like 'HBox.margin', or 'Grid.rowSpan'
    private void processStaticProperties(String methodName, Object arg1, String value){

        String fullTypeName = this.type_processor
                .getAliases()
                .getOrDefault(methodName.substring(0, methodName.indexOf('.')), null);
        if(fullTypeName == null){
            System.out.println("Cannot find class: '"+methodName.substring(0, methodName.indexOf('.')) + "'");
            return;
        }
        String setterName = methodName.substring(methodName.indexOf('.') + 1);
        try {
            Class<?> clazz = Class.forName(fullTypeName);

            Method m = ClassObjectBuilder.getClassMethod(clazz, setterName);
            m = (m == null) ? ClassObjectBuilder.getClassMethod(clazz, "set" + setterName) : m;
            m = (m == null) ? ClassObjectBuilder.getClassMethod(clazz, "init" + setterName) : m;
            if(m != null) {
                System.out.println("Found setter STATIC prop: "+setterName);
                Class<?> propType = m.getParameters()[1].getType(); //zero argument - child node.
                System.out.println("Property: "+propType.getSimpleName());

                /* if String setter */
                if(propType.getSimpleName().equalsIgnoreCase("String"))
                    m.invoke(null, arg1, value);

                /* if boolean setter */
                else if(propType.getSimpleName().equals("boolean")){
                    m.invoke(null,arg1, value.equals("true"));
                }

                /* if Enum setter */
                else if(propType.isEnum()){
                    System.out.println("Enum<"+propType.getSimpleName()+">");
                    m.invoke(null, arg1, Enum.valueOf((Class) propType, value));
                }
                /* Resource setter */
                else if(value.charAt(0) == '{'
                        && value.charAt(value.length() - 1) == '}'
                )
                {
                    String rkey = value.substring(1, value.length() - 1); // remove '{}'
                    m.invoke(null, arg1, this.resources.getOrDefault(rkey, null));
                }
                /* if Number setter */
                else {
                    m.invoke(null,arg1, ProcessNumber.parseNumber(value, propType));
                }
            }
        }catch (ClassNotFoundException   | InvocationTargetException | IllegalArgumentException | IllegalAccessException e){
            System.out.println(e);
        }
    }// end method.
}