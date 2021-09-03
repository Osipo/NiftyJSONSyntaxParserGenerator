package bmstu.iu7m.osipov.ui.models.stores;

import bmstu.iu7m.osipov.ui.controllers.handlers.ObserverEventHandler;
import javafx.event.Event;
import javafx.event.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("hdlrsStore")
@DependsOn({"hdlrsMap"})
public class EventHandlersStore {
    private Map<String, ObserverEventHandler> handlers;

    @Autowired
    public EventHandlersStore(@Qualifier("hdlrsMap") Map<String, ObserverEventHandler> handlers){
        this.handlers = handlers;
        System.out.println("EventHandlersStore constructor call.");
    }

    public Map<String, ObserverEventHandler> getHandlers() {
        return this.handlers;
    }
}
