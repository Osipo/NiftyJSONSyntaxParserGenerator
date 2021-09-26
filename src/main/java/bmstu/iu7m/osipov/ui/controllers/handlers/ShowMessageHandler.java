package bmstu.iu7m.osipov.ui.controllers.handlers;

import bmstu.iu7m.osipov.ui.wins.MessageWindow;
import javafx.event.Event;

public class ShowMessageHandler <T extends Event> extends ObserverBaseEventHandler<T>{

    private MessageWindow win;

    public ShowMessageHandler(MessageWindow w){
        this.win = w;
    }

    @Override
    public void handle(T event) {
        win.showMessage("Test message!");
    }
}