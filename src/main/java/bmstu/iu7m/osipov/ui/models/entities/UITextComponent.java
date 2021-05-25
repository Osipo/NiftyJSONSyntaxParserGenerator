package bmstu.iu7m.osipov.ui.models.entities;

import javafx.scene.Node;
import javafx.scene.control.Labeled;

public class UITextComponent extends UIComponent {

    private Labeled textNode;
    public UITextComponent(Node node) {
        super(node);
        if(node instanceof Labeled)
            textNode = (Labeled) node;
    }

    public Labeled getTextNode(){
        return this.textNode;
    }
}
