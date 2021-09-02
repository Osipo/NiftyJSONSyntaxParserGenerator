package bmstu.iu7m.osipov.ui.controllers.handlers;

import bmstu.iu7m.osipov.ui.models.entities.UIComponent;
import javafx.event.Event;
import javafx.event.EventType;

import java.util.ArrayList;
import java.util.List;

public abstract class ObserverBaseEventHandler<T extends Event> implements ObserverEventHandler<T> {
    private List<UIComponent> subscribers;

    public ObserverBaseEventHandler(){
        this.subscribers = new ArrayList<>(5);
    }


    @Override
    public void attachTo(EventType<T> etype, UIComponent c) {

        if(etype == null)
            throw new NullPointerException("Cannot select: type 'null' is illegal for eventType! ");
        if(c == null)
            throw new NullPointerException("Cannot addEventHandler() to null component!");

        this.subscribers.add(c);
        c.addEventHandler(etype, this);
    }

    @Override
    public void detachFrom(EventType<T> etype, UIComponent c) {
        if(etype == null)
            throw new NullPointerException("Cannot select: type 'null' is illegal for eventType! ");
        if(c == null)
            throw new NullPointerException("Cannot removeEventHandler() to null component!");

        this.subscribers.remove(c);
        c.removeEventHandler(etype, this);
    }

    @Override
    public void detachAllFrom(EventType<T> etype) {
        int l = subscribers.size();
        while(l > 0){
            subscribers.get(l - 1).removeEventHandler(etype, this);
            subscribers.remove(l - 1);
            l--;
        }
    }
}
