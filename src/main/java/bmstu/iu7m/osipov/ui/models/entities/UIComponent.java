package bmstu.iu7m.osipov.ui.models.entities;


import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;

public class UIComponent implements EventSubscriber {
    protected Node node;
    protected boolean readTextFromCss = false;

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

    @Override
    public <T extends Event> void addEventHandler(EventType<T> etype, EventHandler<T> handler){
        this.node.addEventHandler(etype, handler);
    }

    public void setReadTextFromCss(boolean appliedCss) {
        this.readTextFromCss = appliedCss;
    }

    public boolean isReadTextFromCss() {
        return this.readTextFromCss;
    }

    @Override
    public <T extends Event> void removeEventHandler(EventType<T> etype, EventHandler<T> handler){
        this.node.removeEventHandler(etype, handler);
    }
}
