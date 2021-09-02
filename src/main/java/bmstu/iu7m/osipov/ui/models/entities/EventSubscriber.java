package bmstu.iu7m.osipov.ui.models.entities;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;

public interface EventSubscriber {
    public <T extends Event> void addEventHandler(EventType<T> etype, EventHandler<T> handler);
    public <T extends Event> void removeEventHandler(EventType<T> etype, EventHandler<T> handler);
}
