package bmstu.iu7m.osipov.services.grammars.directives;

import bmstu.iu7m.osipov.services.grammars.Grammar;
import bmstu.iu7m.osipov.services.grammars.xmlMeta.FragmentContainer;
import bmstu.iu7m.osipov.services.lexers.LanguageSymbol;
import bmstu.iu7m.osipov.services.lexers.Translation;
import bmstu.iu7m.osipov.structures.trees.LinkedNode;
import bmstu.iu7m.osipov.structures.trees.Node;
import bmstu.iu7m.osipov.utils.GrammarBuilderUtils;

import java.util.Map;

public class JavaFXExtraFragmentProcessorSDT extends JavaFXExtraXMLProcessorSDT implements SDTParser {

    public JavaFXExtraFragmentProcessorSDT(TypeElement typeProcessor,
                                           Map<String, LinkedNode<LanguageSymbol>> fragment_roots,
                                           Map<String, Object> resources
    )
    {
        super(typeProcessor);
        this.fragment_roots = fragment_roots;// added from actual parent (not from super)
        this.res = resources; //only res and fragment_roots are needed from parent. other fields are owned by child.
        this.state = 0;
    }


    /*
    *  Error = -1
    *  Start = 0.
    *  Finish = 4
    *  <Fragment> tag read -> 1 (process 1) (12, 24)  (states in parent class)
    *  16 -> read child of Fragment. (16, 32) (reference at Fragment to another Fragmet)
    *  20 -> read Fragment ref at Fragment. (20, 64)
    */
    @Override
    public void exec(Translation t, Node<LanguageSymbol> parent) {
        if (t == null || t.getArguments() == null || this.state == 4) {
            return;
        } else if (this.state == -1) {
            System.out.println("ParsingXMLFragmentError: Illegal fragment content.");
            restart();
            return;
        }
        String action = t.getActName();
        LinkedNode<LanguageSymbol> l_parent = (LinkedNode<LanguageSymbol>) parent;

        switch (action){
            case "putAttr":
            case "createObject": {
                super.exec(t, parent);
                break;
            }
            case "removePrefix": {
                String clTag = t.getArguments().getOrDefault("pref", null);
                clTag = GrammarBuilderUtils.replaceSymRefsAtArgument(l_parent, clTag);
                if(clTag.equalsIgnoreCase("Fragment") && this.state == 20)
                    this.state = 16;
                else if(clTag.equalsIgnoreCase("Fragment")){
                    this.objects.pop();
                    if(this.objects.isEmpty())
                        this.state = 4;
                }
                else if(this.state == 16){
                    this.objects.pop();
                }
                break;
            }
        }//end switch
    }//end method


    // All methods in Java are virtual. So then called super.exec() and it calls checkState()
    // this method will be executed.
    @Override
    protected void changeState(){
        if(this.state == -1)
            return;
        if(this.state == 0 && this.curName.equalsIgnoreCase("Fragment"))
            this.state = 1;
        else if ((this.state == 1 || this.state == 16) && this.curName.equalsIgnoreCase("Fragment"))
            this.state = 20;
        else if(this.state == 1)
            this.state = 16;
    }

    @Override
    protected void checkState() {
        switch (this.state){
            case 0: case -1:{ // fragment was not recognized at start or error.
                break;
            }
            case 1: { // Root <Fragment> recognized.
                String p = obj_attrs.getOrDefault("key", null);
                System.out.println(p);
                if(p == null || p.length() == 0)
                    break;
                FragmentContainer fr = new FragmentContainer(p);
                this.root = fr;
                this.objects.push(fr);
                return;
            }
            case 16: case 20:{ //fragment content or fragment ref.
                super.checkState(); //virtual method also preserves instance variable so it changes objects and state!
                return;
            }
        }//end switch
        this.state = -1;
    }//end method.
}