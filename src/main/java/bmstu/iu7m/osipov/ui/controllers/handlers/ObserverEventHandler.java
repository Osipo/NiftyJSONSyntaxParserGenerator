package bmstu.iu7m.osipov.ui.controllers.handlers;

import bmstu.iu7m.osipov.ui.models.entities.UIComponent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;

public interface ObserverEventHandler<T extends Event> extends EventHandler<T> {
    void attachTo(EventType<T> etype, UIComponent c);
    void detachFrom(EventType<T> etype, UIComponent c);
    void detachAllFrom(EventType<T> event);
}
