package bmstu.iu7m.osipov.ui.models.entities;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.control.MenuItem;

public class UIMenuItemComponent extends UIComponent {
    private MenuItem item;
    public UIMenuItemComponent(MenuItem item){
        super(null);
        this.item = item;
    }

    public MenuItem getItem(){
        return this.item;
    }

    @Override
    public String getType() {
        return this.item.getClass().getSimpleName();
    }

    @Override
    public void setStyle(String style) {
        this.item.setStyle(this.item.getStyle() + style);
    }

    @Override
    public <T extends Event> void addEventHandler(EventType<T> etype, EventHandler<T> handler){
        this.item.addEventHandler(etype, handler);
    }

    @Override
    public <T extends Event> void removeEventHandler(EventType<T> etype, EventHandler<T> handler){
        this.item.removeEventHandler(etype, handler);
    }
}
