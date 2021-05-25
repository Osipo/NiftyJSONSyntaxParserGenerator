package bmstu.iu7m.osipov.ui.models.entities;


import javafx.scene.Node;

public class UIComponent  {
    protected Node node;
    public UIComponent(Node node){
        this.node = node;
    }

    public Node getNode(){
        return node;
    }
}
