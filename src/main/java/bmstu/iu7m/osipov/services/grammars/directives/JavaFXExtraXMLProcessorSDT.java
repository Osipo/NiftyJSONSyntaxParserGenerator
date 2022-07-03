package bmstu.iu7m.osipov.services.grammars.directives;

import bmstu.iu7m.osipov.services.grammars.xmlMeta.*;
import bmstu.iu7m.osipov.services.lexers.LanguageSymbol;
import bmstu.iu7m.osipov.services.lexers.Translation;
import bmstu.iu7m.osipov.structures.lists.KeyValuePair;
import bmstu.iu7m.osipov.structures.lists.LinkedStack;
import bmstu.iu7m.osipov.structures.lists.Triple;
import bmstu.iu7m.osipov.structures.trees.LinkedNode;
import bmstu.iu7m.osipov.structures.trees.LinkedTree;
import bmstu.iu7m.osipov.structures.trees.Node;
import bmstu.iu7m.osipov.structures.trees.VisitorMode;
import bmstu.iu7m.osipov.structures.trees.translators.ExecuteTranslationNode;
import bmstu.iu7m.osipov.utils.ClassObjectBuilder;
import bmstu.iu7m.osipov.utils.GrammarBuilderUtils;
import bmstu.iu7m.osipov.utils.PrimitiveTypeConverter;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Region;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class JavaFXExtraXMLProcessorSDT implements SDTParser {
    protected Map<String, String> obj_attrs;
    protected Map<String, Object> res;
    protected Map<String, FragmentContainer> fragments;// built fragments.
    protected Map<String, LinkedNode<LanguageSymbol>> fragment_roots; //for fragment parser

    // only needed to match <Fragment> tag with </Fragment> tag.
    // because of states 999, 998. (when ignore elements)
    private LinkedStack<Boolean> _fragment_nesting_ch;

    protected LinkedStack<Object> objects;
    protected Object root;
    protected String curName;
    protected String curFragment; //nested fragments are illegal. So fragment can be identified.
    protected LinkedNode<LanguageSymbol> curNode;
    protected List<SizeRelationItem> size_rels; //size relations between parents and children


    /* State initial = 0. */
    /*
    * -1 - Error.
    * 0 - recognize root (awaiting Stage, Resource or Fragment) (goto 1, 5, 6)
    * 1 - Stage recognized and created. awaiting Scene object or Resources.
    *   10 - recognized Stage.Resources. reading Stage.Resources content
    *   11 - read Resource file (dictionary)
    *   12 - create new Fragment
    *       16 - read Fragment content.
    *   13 - reading Array of items when sequence needed.
    *   14 - reading List of items when sequence needed.
    *   15 - creating Map of entries or Style.
    *       150 - reading entries of the Map (or Style).
    * 2 - read Scene constructor properties.
    * 3 - read rootNode of the Scene. Create scene with rootNode and set it to the Stage.
    * 4 - read content of the rootNode of the Scene.
    *   40 - </Scene> was read. All content of the Scene has been created. Awaiting other Resources.
    *   40 -> 41. (41 -> 40, 10 -> 1).  (Stage.Resources)
    *   41 -> 30 (10 -> 15), 30 -> 300 (15 -> 150). (Stage.Resources.Style)
    *   41 -> 24 (10 -> 12), 24 -> 32 (12 -> 16), 32 -> 64 (16 -> 20).
    * 5 - read content of the root Resource
    * 6 - read content of the root Fragment
    * 7 - </Stage> was reached. Nothing to awaits. Finished state.
    * 8 - read Fragment reference at scene Graph and go back. (8 -> 4)
    * 20 - read Fragment reference at Fragment content and go back. (20 -> 16)
    * 200 - detected complex element. List of items must be read before calling .ctor of complex element.
    * 210 - detected complex element at <Stage.Resources>. List of items must be read.
    * 200 -> 220. Read item of complex element.
    * 210 -> 230. Read item of complex element.
    * 998, 999 > ignore states at Stage.Resources.Fragment before Scene and after Scene.
    * 998, 999 - just skip processing content in <Fragment>...</Fragment>
    * as it will be processed JavaFXExtraFragmentProcessorSDT class.
    * Note: about 200,210,220,230. Complex element is objects such Background(Items[]...)
    * i.e. constructors which parameter type is only Collection<T> or Array[] or Varargs. (array)
    *
    */
    protected int state;

    // Type meta descriptor.
    protected TypeElement type_processor;

    public JavaFXExtraXMLProcessorSDT(TypeElement type_processor, int initialState){
        this.objects = new LinkedStack<>();
        this.obj_attrs = new HashMap<>();
        this.res = new HashMap<>();
        this.fragments = new HashMap<>();
        this.fragment_roots = new HashMap<>();
        this.size_rels = new ArrayList<>();
        this.type_processor = type_processor;
        this._fragment_nesting_ch = new LinkedStack<>();
        this.state = initialState;
    }
    public JavaFXExtraXMLProcessorSDT(TypeElement type_processor){
        this(type_processor, 0);
    }

    public void restart(){
        this.obj_attrs.clear();
        this.objects.clear();
        this.res.clear();
        this.fragments.clear();
        this.fragment_roots.clear();
        this._fragment_nesting_ch.clear();
        this.size_rels.clear();
        this.root = null;
        this.curFragment = null;
        this.curNode = null;
        this.curName = null;
        this.state = 0;
    }

    //bind and connect relations between sizes of parent and child widgets.
    public void bindSizes(){

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
            System.out.println("ParsingXMLError: illegal sequence of elements.");
            restart();
            return;
        }

        String action = t.getActName();
        LinkedNode<LanguageSymbol> l_parent = (LinkedNode<LanguageSymbol>) parent;
        this.curNode = l_parent;

        switch (action){
            case "putAttr": { //read attr1=val1
                String arg1 = t.getArguments().getOrDefault("key", null);
                String arg2 = t.getArguments().getOrDefault("val", null);
                arg1 = GrammarBuilderUtils.replaceSymRefsAtArgument(l_parent, arg1);
                arg2 = GrammarBuilderUtils.replaceSymRefsAtArgument(l_parent, arg2);

                arg2 = arg2.substring(1, arg2.length() - 1); // unquote value.
                this.obj_attrs.put(arg1, arg2);
                break;
            }//end 'putAttr'

            case "createObject": { //read open tag.
                String arg1 = t.getArguments().getOrDefault("className",null);
                arg1 = GrammarBuilderUtils.replaceSymRefsAtArgument(l_parent, arg1);
                this.curName = arg1;
                changeState();
                checkState();
                this.obj_attrs.clear();
                break;
            }//end 'createObject'

            case "removePrefix":{ //read close tag.
                String clTag = t.getArguments().getOrDefault("pref", null);
                clTag = GrammarBuilderUtils.replaceSymRefsAtArgument(l_parent, clTag);
                if(this.state == 220)
                    this.state = 200;
                else if(this.state == 230)
                    this.state = 210;
                else if(clTag.equalsIgnoreCase("Scene"))
                    this.state = 40;
                else if(clTag.equalsIgnoreCase("Stage.Resources") && this.state == 41) // 41 -> 40.
                    this.state = 40;
                else if((clTag.equalsIgnoreCase("Map") || clTag.equalsIgnoreCase("Style")
                        )  && this.state == 150
                )
                {
                    this.state = 10; //150 -> 10.
                    this.objects.pop();
                }
                else if((clTag.equalsIgnoreCase("Map") || clTag.equalsIgnoreCase("Style")
                )  && this.state == 300
                )
                {
                    this.state = 41; //300 -> 41.
                    this.objects.pop();
                }
                else if(clTag.equalsIgnoreCase("Stage.Resources")) //state == 10 (10 -> 1).
                    this.state = 1;
                else if(clTag.equalsIgnoreCase("Fragment") && this.state == 20) // 20 -> 16
                    this.state = 16;
                else if(clTag.equalsIgnoreCase("Fragment") && this.state == 64) //64 -> 32
                    this.state = 32;
                else if(clTag.equalsIgnoreCase("Fragment") && this.state == 8) { //8 -> 4.
                    this.state = 4;
                }
                else if(clTag.equalsIgnoreCase("Fragment") && this.state == 32){// 32 -> 41, end fragment definiton.
                    this.objects.pop();
                    this.state = 41;
                }
                else if(clTag.equalsIgnoreCase("Fragment") && this.state == 998){
                    this._fragment_nesting_ch.pop();
                    if(this._fragment_nesting_ch.isEmpty()) {
                        this.state = 10;
                        //System.out.println("read <Fragment>");
                    }
                }
                else if(clTag.equalsIgnoreCase("Fragment") && this.state == 999){
                    this._fragment_nesting_ch.pop();
                    if(this._fragment_nesting_ch.isEmpty())
                        this.state = 41;
                }
                else if(clTag.equalsIgnoreCase("Fragment")) { //</Fragment> tag reached.
                    this.objects.pop();
                    this.state = 10;
                }
                else if(this.state == 40 && clTag.equalsIgnoreCase("Stage")) {
                    this.state = 7;
                    this.objects.pop();
                }
                else if((this.state == 4 || this.state == 16) && !clTag.contains("."))
                    this.objects.pop();
                else if(this.state == 200 || this.state == 210){
                    Object list = this.objects.top(); //extract arraylist
                    this.objects.pop();
                    Constructor<?> ctr = (Constructor<?>) this.objects.top(); //and constructor of complex type.
                    this.objects.pop();


                    try{
                        // Convert ArrayList<Object> into Array<T>
                        Class<?> item_type = ((ArrayList<Object>) list).get(0).getClass();
                        int size = ((ArrayList<?>) list).size();
                        int arr_i = 0;
                        Iterator<Object> list_itr = ((ArrayList) list).iterator();
                        Object arr = Array.newInstance(item_type, size);
                        while(list_itr.hasNext()){
                            Array.set(arr, arr_i, list_itr.next());
                            arr_i++;
                        }
                        Object celem = ctr.newInstance(arr);
                        ResourceKey k = (ResourceKey) this.objects.top();
                        this.objects.pop();

                        if(k.getKey() != null){
                            processSimpleProperties(celem);
                            this.res.put(k.getKey(), celem);
                            this.state = k.getPrevState();
                            break;
                        }
                        processChildren(celem);
                        processSimpleProperties(celem);
                        this.objects.push(celem);
                        this.state = k.getPrevState();

                    }
                    catch (InvocationTargetException | InstantiationException | IllegalArgumentException | IllegalAccessException e)
                    {System.out.println(e.getMessage());}
                }
                break;
            }//end 'removePrefix'

        }//end switch
    }//end method

    protected void changeState(){
        System.out.println(this.state);
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
        else if(this.state == 41 && this.curName.equalsIgnoreCase("Fragment"))
            this.state = 24;
        else if(this.state == 10 && this.curName.equalsIgnoreCase("Array"))
            this.state = 13;
        else if(this.state == 10 && this.curName.equalsIgnoreCase("List"))
            this.state = 14;
        else if(this.state == 10
                && (this.curName.equalsIgnoreCase("Map") || this.curName.equalsIgnoreCase("Style"))
        )
            this.state = 15;
        else if(this.state == 41
                && (this.curName.equalsIgnoreCase("Map") || this.curName.equalsIgnoreCase("Style"))
        )
            this.state = 30;
        else if(this.state == 15) //Map -> read map.
            this.state = 150;
        else if(this.state == 30) //Map -> read map.
            this.state = 300;
        else if(this.state == 2) //read Scene constructor -> read root Node of the Scene.
            this.state = 3;
        else if(this.state == 3)//read root Node of the Scene -> read content of the Scene-graph.
            this.state = 4;
        else if((this.state == 16 || this.state == 12) && this.curName.equalsIgnoreCase("Fragment"))
            this.state = 20;
        else if((this.state == 32 || this.state == 24) && this.curName.equalsIgnoreCase("Fragment"))
            this.state = 64;
        else if(this.state == 4 && this.curName.equalsIgnoreCase("Fragment"))
            this.state = 8;
        else if(this.state == 4 && this.curName.contains(".")) // <Object.Property> tag.
            this.state = 44;
        else if(this.state == 44 && this.curName.contains(".")) // <Object> -> <Object.Property> -> <Object2> -> <Object2.Property> rule.
            this.state = -1;
        else if(this.state == 44) //Create object for Property of the Object owner.
            this.state = 4;
        else if(this.state == 12) //<Fragment> tag was previously read and created. awaiting children.
            this.state = 16;
        else if(this.state == 24)//<Fragment> tag read at <Stage.Resources> after <Scene>.
            this.state = 32; //12 -> 16, 24 -> 32.
        else if(this.state == 40 && this.curName.equalsIgnoreCase("Stage.Resources"))
            this.state = 41;
        else if((this.state == 999 || this.state == 998) && this.curName.equalsIgnoreCase("Fragment"))
            this._fragment_nesting_ch.push(true);
        else if(this.state == 200)
            this.state = 220;
        else if(this.state == 210)
            this.state = 230;
    }

    protected void checkState(){
        Triple<Constructor<?>, Class<?>[], Object[]> ctr_with_vals = null;
        ConstructorElement meta_ctr = null;
        //System.out.println(this.state);
        switch (this.state) {
            case 0:
            case 40:
            case -1: { //Cannot find Stage at start OR Error.
                break;
            }
            case 1: { /* curName = 'Stage' */
                meta_ctr = getTypeConstructorElement();
                if (meta_ctr == null)
                    break;
                ctr_with_vals = getConstructorWithValues(meta_ctr);
                if (ctr_with_vals == null)
                    break;

                //Object stage = ClassObjectBuilder.createInstance(ctr_with_vals.getKey(), ctr_with_vals.getValue(), PrimitiveTypeConverter::convertConstructorArguments, 0);
                Object stage = ClassObjectBuilder.createInstanceWithCovariance(ctr_with_vals.getV1(), ctr_with_vals.getV2(), ctr_with_vals.getV3(), PrimitiveTypeConverter::convertConstructorArguments, 0);


                if (stage == null) {
                    break;
                }
                this.objects.push(stage); //push new root node.
                this.root = stage; //save root Stage object.
                processSimpleProperties(stage);
                return;
            }
            case 2: { /* curName = 'Scene' */

                // Extract Scene constructor.
                meta_ctr = getTypeConstructorElement();
                if (meta_ctr == null) {
                    try {
                        ctr_with_vals = new Triple<>(
                                Scene.class.getDeclaredConstructor(Parent.class),
                                new Class<?>[]{Parent.class},
                                new Object[]{null}
                        );
                    } catch (NoSuchMethodException | SecurityException e) {
                    }
                } else {
                    ctr_with_vals = getConstructorWithValues(meta_ctr);
                }
                this.objects.push(ctr_with_vals); // save Scene constructor with args.
                return;
            }
            case 3: { /* Create Scene. Create root Node of the Scene graph, AND set Scene. */
                meta_ctr = getTypeConstructorElement();
                if (meta_ctr == null)
                    break;
                ctr_with_vals = getConstructorWithValues(meta_ctr);
                if (ctr_with_vals == null)
                    break;

                //Object sceneRoot = ClassObjectBuilder.createInstance(ctr_with_vals.getKey(), ctr_with_vals.getValue(), PrimitiveTypeConverter::convertConstructorArguments, 0);
                Object sceneRoot = ClassObjectBuilder.createInstanceWithCovariance(ctr_with_vals.getV1(), ctr_with_vals.getV2(), ctr_with_vals.getV3(), PrimitiveTypeConverter::convertConstructorArguments, 0);
                if (sceneRoot == null)
                    break;
                if (!processScene(sceneRoot))
                    break;
                processSimpleProperties(sceneRoot);
                return;
            }
            case 44: { // <Object.Property> tag.
                String ob_name = this.objects.top().getClass().getSimpleName();
                String a_name = this.curName.substring(0, this.curName.indexOf('.'));

                if (!ob_name.equals(a_name)) //only <Object> -> <Object.Property> valid.
                    break;

                String prop_name = this.curName.substring(this.curName.indexOf('.') + 1);
                Method m = ClassObjectBuilder.getMethod(this.objects.top(), "set" + prop_name);
                m = (m == null) ? ClassObjectBuilder.getMethod(this.objects.top(), "init" + prop_name) : m;
                if (m == null)
                    break;

                this.objects.push(m);
                return;
            }
            case 4:
            case 16:
            case 32:
            case 220: { /* Scene graph content */
                meta_ctr = getTypeConstructorElement();
                if (meta_ctr == null)
                    break;

                //check if meta_ctr describes complex type (parameter of constructor is a collection of objects to be created).
                if (isCollection(meta_ctr)) {
                    createListContainer(meta_ctr);
                    return;
                }

                ctr_with_vals = getConstructorWithValues(meta_ctr);
                if (ctr_with_vals == null)
                    break;

                //Object elem_or_layout = ClassObjectBuilder.createInstance(ctr_with_vals.getKey(), ctr_with_vals.getValue(), PrimitiveTypeConverter::convertConstructorArguments, 0);
                Object elem_or_layout = ClassObjectBuilder.createInstanceWithCovariance(ctr_with_vals.getV1(), ctr_with_vals.getV2(), ctr_with_vals.getV3(), PrimitiveTypeConverter::convertConstructorArguments, 0);
                if (elem_or_layout == null)
                    break;
                if (this.state == 220 && processItem(elem_or_layout))
                    return;
                if (this.state == 220) //break if NOT processItem() WHEN it is required!
                    break;

                processSimpleProperties(elem_or_layout);
                if (!processChildren(elem_or_layout))
                    break;
                this.objects.push(elem_or_layout); //push new root node.
                return;
            }
            case 8:
            case 20:
            case 64: { /* Fragment resource reference at Scene graph or inside Fragment definition. */
                String p = obj_attrs.getOrDefault("key", null);
                if (p == null || p.length() == 0)
                    return;

                LinkedNode<LanguageSymbol> fn = this.fragment_roots.getOrDefault(p, null);
                if (fn == null) {
                    System.out.println("Cannot find fragment '" + p + "'");
                    break;
                }

                LinkedTree<LanguageSymbol> sub_tree = new LinkedTree<>(fn);

                //translation actions have been already embedded.
                ExecuteTranslationNode sub_executor = new ExecuteTranslationNode();
                JavaFXExtraFragmentProcessorSDT sub_actor = new JavaFXExtraFragmentProcessorSDT(type_processor, fragment_roots, res);
                sub_executor.putActionParser("putAttr", sub_actor);
                sub_executor.putActionParser("createObject", sub_actor);
                sub_executor.putActionParser("removePrefix", sub_actor);

                System.out.println("created sub_tree");
                sub_tree.visit(VisitorMode.PRE, sub_executor); //perform exec method of sub_actor.
                if (sub_actor.state != 4) // Not Final State
                    break;
                if (!(sub_actor.getRoot() instanceof FragmentContainer))
                    break;
                System.out.println("Fragment parsed successful.");
                FragmentContainer f = (FragmentContainer) sub_actor.getRoot();
                this.size_rels.addAll(sub_actor.size_rels); //add size relations from Fragment.

                for (Object c : f.getChildren()) {
                    if (!processChildren(c)) {
                        this.state = -1;
                        System.out.println("cannot add fragment widget");
                        break; //breaks cycle with error but not switch block.
                    }
                }
                return; //if previous cycle performed 'break' > -1.
            }
            case 10:
            case 41:
            case 230: { /* parse Stage.Resource objects. */
                if (curName.equalsIgnoreCase("Stage.Resources")) //skip header.
                    return;
                meta_ctr = getTypeConstructorElement(); //get information from scheme
                if (meta_ctr == null)
                    break;

                //check if meta_ctr describes complex type (parameter of constructor is a collection of objects to be created).
                if (isCollection(meta_ctr)) {
                    createListContainer(meta_ctr);
                    return;
                }

                ctr_with_vals = getConstructorWithValues(meta_ctr); //and extract actual constructor of the class.
                if (ctr_with_vals == null)
                    break;


                System.out.println("create resource");
                Object elem_or_layout = ClassObjectBuilder.createInstanceWithCovariance(ctr_with_vals.getV1(), ctr_with_vals.getV2(), ctr_with_vals.getV3(), PrimitiveTypeConverter::convertConstructorArguments, 0);
                if (elem_or_layout == null)
                    break;

                System.out.println("resource created");
                if (this.state == 230 && processItem(elem_or_layout))
                    return;
                if (this.state == 230) //break if NOT processItem() WHEN it is required!
                    break;
                processSimpleProperties(elem_or_layout);
                this.res.put(this.obj_attrs.get("key"), elem_or_layout);
                return;
            }
            case 15:
            case 30: { // Create Style or Map resource.
                meta_ctr = getTypeConstructorElement();
                if (meta_ctr == null)
                    break;
                ctr_with_vals = getConstructorWithValues(meta_ctr);
                if (ctr_with_vals == null)
                    break;
                //Object style_or_map = ClassObjectBuilder.createInstance(ctr_with_vals.getKey(), ctr_with_vals.getValue(), PrimitiveTypeConverter::convertConstructorArguments, 0);
                Object style_or_map = ClassObjectBuilder.createInstanceWithCovariance(ctr_with_vals.getV1(), ctr_with_vals.getV2(), ctr_with_vals.getV3(), PrimitiveTypeConverter::convertConstructorArguments, 0);
                if (style_or_map == null)
                    break;
                if (style_or_map instanceof Style) {
                    Style s = (Style) style_or_map;
                    Object parent = getResourceObject(s, obj_attrs.getOrDefault("parent", null));
                    if (obj_attrs.getOrDefault("parent", null) != null && !(parent instanceof Style))
                        break;
                    else if (obj_attrs.getOrDefault("parent", null) != null)
                        s.copyFrom((Style) parent);
                }
                this.res.put(this.obj_attrs.get("key"), style_or_map);
                this.objects.push(style_or_map); // save map into stack.
                return;
            }
            case 150:
            case 300: {
                Object m = this.objects.top();
                if (m instanceof Style) {
                    String k = this.obj_attrs.getOrDefault("property", null);
                    String v = this.obj_attrs.getOrDefault("value", null);
                    if (k == null)
                        break;
                    ((Style) m).put(k, v);
                }
                return;
            }
            case 12:
            case 24: { /* Create container for Fragment object */
                String p = obj_attrs.getOrDefault("key", null);
                if (p == null || p.length() == 0)
                    break;

                //Add here check THAT only <Fragment></Fragment> form is well-formed.

                this.fragment_roots.put(p, this.curNode.getParent()); // STAG > ELEMENT (<Fragment> </Fragment> subTree)
                System.out.println("add fragment: '" + p + "'");
                //ignore til </Fragment> reached.
                this.state = (this.state == 12) ? 998 : 999;
                this._fragment_nesting_ch.push(true);
                return;
            }
            case 999:
            case 998: { //just ignore content.
                return;
            }
        }//end switch
        this.state = -1; //if reached here -> no return operator performed -> 'break' -> Error
    }//end method

    //extract info from scheme by parameter names (not by types)
    //info contains className and Constructor's parameters
    //Each parameter has its name and type.
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

        //2. Find matching constructor. (fix for resource logic)
        int m = 0; //MAX matches
        int m_i = 0; //matches at ctr_i.
        int p_i = 0; //count of params at ctr_i
        int p = 0; //MAX(count of params)
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
                else if(param.isCollectionType() && this.state == 10 || this.state == 41 || this.state == 220 || this.state == 230)
                    m_i++;
            }
            p_i = ctr.getParams().size();

            if(m_i > m){
                m = m_i;
                p = p_i;
                idx  = idx_i;
            }
            else if(m_i == m && p_i < p){ //if matches equals then select the most matched by params.
                p = p_i;
                idx = idx_i;
            }
        }
        return (idx == -1) ? null : class_meta.getConstructors().get(idx);
    }


    private Triple<Constructor<?>, Class<?>[], Object[]> getConstructorWithValues(ConstructorElement meta_ctr){
        int pargs = 0;
        int ii = 0;
        if(!meta_ctr.haveNoParams())//for no-args constructor: getParams() returns null.
            pargs = meta_ctr.getParams().size();

        Class<?>[] ctr_types = (pargs == 0) ? null : new Class[pargs];
        Object[] vals = new Object[pargs];
        if(this.state == 2 && ctr_types != null){
            ctr_types[0] = Parent.class; //reading Scene tag.
            ii = 1;
        }
        for(;ii < pargs; ii++){
            try {
                Class<?> ptype = ClassObjectBuilder.getPrimitiveTypes().getOrDefault(meta_ctr.getParams().get(ii).getType(), null);
                ptype = (ptype != null) ? ptype : Class.forName(meta_ctr.getParams().get(ii).getType());

                ctr_types[ii] = ptype;
                vals[ii] = obj_attrs.getOrDefault(meta_ctr.getParams().get(ii).getName(), null);

                //check whether value is resource reference.
                if(vals[ii] instanceof String){
                    String str_val = (String) vals[ii];
                    if(str_val.charAt(0) == '{' && str_val.charAt(str_val.length() - 1) == '}'){
                        vals[ii] = getResourceObject(null, str_val.substring(1, str_val.length() - 1));
                    }
                }
                obj_attrs.remove(meta_ctr.getParams().get(ii).getName());
            } catch (ClassNotFoundException ex){
                return null;
            }
        }

        return new Triple<>(ClassObjectBuilder.getDeclaredConstructor(meta_ctr.getClassName(), ctr_types), ctr_types, vals);
    }

    private boolean processItem(Object child){
        if(!(this.objects.top() instanceof ArrayList) || child == null){
            System.out.println("Child or parent is null");
            return false;
        }
        ArrayList<Object> children  = (ArrayList<Object>) this.objects.top();
        children.add(child);
        processSimpleProperties(child);
        return true;
    }

    private boolean processChildren(Object child){
        Object parent = this.objects.top();
        if(parent == null || child == null) {
            System.out.println("Child or parent is null");
            return false;
        }

        // 44 -> 4. child is Property value.
        if(parent instanceof Method){
            Method m = (Method) parent;
            Object owner = this.objects.topFrom(1); //get second elem from top(). (topFrom(0) == top())
            if(owner == null)
                return false;
            try {
                m.invoke(owner, child);
            }catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e){
                return false;
            }
            this.objects.pop(); //remove executed setter-method.
            return true;
        }

        Object children = null;
        Method m = null;

        m = ClassObjectBuilder.getMethod(parent, "getChildren");
        m = (m == null) ? ClassObjectBuilder.getMethod(parent, "getItems") : m;
        if(m == null)
            return false;

        try{
            children = m.invoke(parent);
            m = children.getClass().getMethod("addAll", Object[].class);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e){
            return false;
        }

        if(this.state == 16 || this.state == 32){
            Method id_getter = ClassObjectBuilder.getMethod(child, "getId");
            Method id_setter = ClassObjectBuilder.getMethod(child, "setId");
            String prefix = obj_attrs.getOrDefault("key", "");
            try{
                Object prev_id = id_getter.invoke(child);
                String prev_id_str = (prev_id instanceof String) ? (String) prev_id : "";
                id_setter.invoke(child, prefix + "_" + prev_id_str);
            } catch (NullPointerException | ClassCastException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e){
                return false;
            }
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
        Triple<Constructor<?>, Class<?>[], Object[]> scene_ctr_with_args = null;
        try{
            scene_ctr_with_args = (Triple<Constructor<?>, Class<?>[], Object[]>) this.objects.top();
        } catch (ClassCastException | NullPointerException e){
            return false;
        }
        if(scene_ctr_with_args.getV1() == null)
            return false;
        if(scene_ctr_with_args.getV3() == null)
            return false;
        this.objects.pop(); //remove scene constructor from Stack.

        Constructor<?> scene_ctr = scene_ctr_with_args.getV1();
        Object[] args = scene_ctr_with_args.getV3();
        args[0] = root;
        Object stage = this.objects.top(); //extract Stage object
        if(stage == null)
            return false;

        //Object scene = ClassObjectBuilder.createInstance(scene_ctr, args, PrimitiveTypeConverter::convertConstructorArguments, 1);
        Object scene = ClassObjectBuilder.createInstanceWithCovariance(scene_ctr, scene_ctr_with_args.getV2(), args, PrimitiveTypeConverter::convertConstructorArguments, 1);
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

    //root = element
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
                //System.out.println("Found instance setter prop: "+methodName);
                Class<?> propType = m.getParameters()[0].getType();
                //System.out.println("Property type: "+propType.getSimpleName());

                /* if Resource setter */
                if(entry.getValue().charAt(0) == '{'
                        && entry.getValue().charAt(entry.getValue().length() - 1) == '}'
                )
                {
                    String rkey = entry.getValue().substring(1, entry.getValue().length() - 1);
                    m.invoke(root, getResourceObject(root, rkey));
                }
                else{
                    Object propVal = PrimitiveTypeConverter.castTo(propType, entry.getValue());
                    if(propVal instanceof SizeRelationItem){
                        SizeRelationItem sz_item = (SizeRelationItem) propVal;
                        sz_item.setChild(root);
                        sz_item.setParent(this.objects.top());
                        sz_item.setPropName(methodName);
                        this.size_rels.add(sz_item);
                        continue;
                    }
                    m.invoke(root, propVal);
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
            Class<?> propType = m.getParameters()[1].getType();
            /* if Resource setter */
            if(val.charAt(0) == '{'
                    && val.charAt(val.length() - 1) == '}'
            )
            {
                String rkey = val.substring(1, val.length() - 1);
                m.invoke(null, root, getResourceObject(root, rkey));
            }
            else{
                Object propVal = PrimitiveTypeConverter.castTo(propType, val);
                m.invoke(null, root, propVal);
            }
            //System.out.println("Found static setter '"+setterName+"'");
        } catch (ClassNotFoundException | InvocationTargetException | IllegalArgumentException | IllegalAccessException e){
            System.out.println(e);
        }
    }

    private Object getResourceObject(Object owner, String rkey){
        Object r = this.res.getOrDefault(rkey, null);
        if(owner instanceof Style) //do not check Style_Resource if Resource owner is also Style.
            return r;
        if(r instanceof Style){ //Make Resource-String and return it to the style-attribute.
            StringBuilder sb = new StringBuilder();
            for(Map.Entry<String, String> e : ((Style) r).entrySet()){
                sb.append(e.getKey()).append(" : ").append(e.getValue()).append(";\n");
            }
            return sb.toString();
        }

        return r; //return Resource for owner.
    }

    //TODO: process collectable items.
    private boolean isCollection(ConstructorElement ctr_meta){
        if(ctr_meta.getParams() == null)
            return false;
        for(ParameterElement p : ctr_meta.getParams()){
            if(p.isCollectionType() || p instanceof GenericParameterElement){
                return true;
            }
        }
        return false;
    }

    private void createListContainer(ConstructorElement ctr_meta){
        System.out.println("createListContainer");
        ParameterElement cp = ctr_meta.getParams()
                .stream()
                .filter(p -> p.isCollectionType())
                .findFirst().orElse(null);
        if(cp instanceof GenericParameterElement){
            cp = (ParameterElement) ((GenericParameterElement) cp).getChildParameter(0); //extract first parameter
        }
        Class<?> type = null;
        String ptypename = cp.getType();
        ptypename = (ptypename.contains("[")) ? "[L" + ptypename.substring(0, ptypename.length() - 2) + ";"
                : "[L" + ptypename + ";";
        try {
            type = Class.forName(ptypename);
            ArrayList<Object> container = new ArrayList<>(); //create object for array parameter.

            Class<?> container_type = Class.forName(ctr_meta.getClassName());
            Constructor<?> container_type_ctr = container_type.getConstructor(type); //get ctr for complex type.
            ResourceKey rk = null;
            if(this.obj_attrs.getOrDefault("key", null) != null && (this.state == 10 || this.state == 41)){
                rk = new ResourceKey(this.obj_attrs.get("key"));
                rk.setPrevState(this.state);
                this.objects.push(rk);
                this.state = 210; //at Resource
            }
            else{
                rk = new ResourceKey(null);
                rk.setPrevState(this.state);
                this.objects.push(rk);
                this.state = 200;
            }
            this.objects.push(container_type_ctr);
            this.objects.push(container);
        }
        catch (ClassNotFoundException | NoSuchMethodException e)
        {}
    }

    public void tryApplySizeRelations(){
        //TODO: set relative size between parent and children nodes with ratio.
        Map<Object, List<SizeRelationItem>> sizes = null;
        sizes = size_rels.stream().collect(Collectors.groupingBy(SizeRelationItem::getParent));

        List<SizeRelationItem> ch = null;
        SizeRelationItem ch_i = null;
        int count = 0;
        int s = 0;
        int s_i = 0;
        double r = 0.0d;
        for(Object p : sizes.keySet()){
            ch = sizes.get(p);
            count = ch.size();
            s = 0;


            /* Compute common divider */
            for(int i = 0; i < count; i++){
                ch_i = ch.get(i);
                if(ch_i == null || ch_i.getParent() == null || ch_i.getChild() == null)
                    continue;
                if(!(ch_i.getParent() instanceof javafx.scene.Node) || !(ch_i.getChild() instanceof javafx.scene.Node))
                    continue;
                r = ch_i.getRatio();
                if(r == Math.floor(r) && !Double.isInfinite(r))
                    s += r;
            }

            // first compute relations less than 1 ( < 1.0)
            for(int i = 0; i < count; i++){
                ch_i = ch.get(i);
                if(ch_i == null || ch_i.getParent() == null || ch_i.getChild() == null)
                    continue;
                if(!(ch_i.getParent() instanceof javafx.scene.Node) || !(ch_i.getChild() instanceof javafx.scene.Node))
                    continue;

                r = ch_i.getRatio();

                if(r != 0 && r < 1) {
                    calculateRelativeSize(ch_i.getChild(), ch_i.getParent(), ch_i.getPropName(), r);
                }
                else if(r != 0 && r == Math.floor(r) && !Double.isInfinite(r)){
                    calculatePropositonSize(ch_i.getChild(), ch_i.getParent(), ch_i.getPropName(), r, s);
                }
            }
        }
    }

    private void calculatePropositonSize(Object ch, Object par, String propName, double r_i, int s){
        System.out.println("calculatePropositonSize() : "+propName);
        if(ch instanceof Region && par instanceof Region){
            Region p = (Region) par;
            Region c = (Region) ch;
            r_i = r_i / s;
            System.out.println("rel = "+r_i);

            //bind size to relation.
            if(propName.contains("prefWidth")) {
                c.prefWidthProperty().bind(p.widthProperty().multiply(r_i));
            }
            else if(propName.contains("prefHeight")) {
                c.prefHeightProperty().bind(p.heightProperty().multiply(r_i));
            }
            else if(propName.contains("spacing")){
                System.out.println("spacing"); //TODO: Add min,max,spacing.
            }
            System.out.println(c.prefHeightProperty().getValue());
        }
    }

    private void calculateRelativeSize(Object ch, Object par, String propName, double r){
        // Region type
        if(ch instanceof Region && par instanceof Region){
            Region p = (Region) par;
            Region c = (Region) ch;

            if(propName.contains("prefWidth"))
                c.prefWidthProperty().bind(p.prefWidthProperty().multiply(r));
            else if(propName.contains("prefHeight"))
                c.prefHeightProperty().bind(p.prefHeightProperty().multiply(r));
        }
    }
}