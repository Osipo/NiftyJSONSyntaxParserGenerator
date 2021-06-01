package bmstu.iu7m.osipov.ui.models.stores;

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
    private Map<String, EventHandler> handlers;

    @Autowired
    public EventHandlersStore(@Qualifier("hdlrsMap") Map<String, EventHandler> handlers){
        this.handlers = handlers;
    }

    public Map<String, EventHandler> getHandlers() {
        return this.handlers;
    }
}
