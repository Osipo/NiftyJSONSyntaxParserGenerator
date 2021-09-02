package bmstu.iu7m.osipov.ui.models.entities;

import javafx.css.Styleable;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.control.Labeled;

public class UITitledComponent extends UIComponent {
    private TitledUIComponent titled;
    public UITitledComponent(TitledUIComponent titled) {
        super(null);
        this.titled = titled;
    }

    public TitledUIComponent getTitled(){
        return this.titled;
    }

    @Override
    public String getType() {
        return this.titled.getClass().getSimpleName();
    }

    @Override
    public void setStyle(String style) {
        throw new UnsupportedOperationException("TitledComponents (aka windows) have no style.");
    }

    @Override
    public <T extends Event> void addEventHandler(EventType<T> etype, EventHandler<T> handler){
        throw new UnsupportedOperationException("TitledComponents (aka windows) have no events.");
    }

    @Override
    public <T extends Event> void removeEventHandler(EventType<T> etype, EventHandler<T> handler){
        this.node.removeEventHandler(etype, handler);
    }
}
