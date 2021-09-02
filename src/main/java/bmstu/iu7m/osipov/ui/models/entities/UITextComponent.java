package bmstu.iu7m.osipov.ui.models.entities;

import javafx.scene.Node;
import javafx.scene.control.Labeled;

public class UITextComponent extends UIComponent {

    private Labeled textNode;
    public UITextComponent(Labeled node) {
        super(node);
        this.textNode = node;
    }

    public Labeled getTextNode(){
        return this.textNode;
    }

    @Override
    public String getType() {
        return this.textNode.getClass().getSimpleName();
    }

    @Override
    public void setStyle(String style) {
        this.textNode.setStyle(this.textNode.getStyle() + style);
    }
}
