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

    public String getType(){
        return this.node.getClass().getSimpleName();
    }

    public void setStyle(String style){
        this.node.setStyle(this.node.getStyle() + style);
    }
}
