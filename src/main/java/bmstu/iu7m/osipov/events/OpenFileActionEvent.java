package bmstu.iu7m.osipov.events;

import javafx.event.ActionEvent;
import javafx.event.EventType;

public class OpenFileActionEvent extends ActionEvent {
    public OpenFileActionEvent(ActionEvent ev){
        super();
        this.source = ev.getSource();
        this.target = ev.getTarget();
        this.eventType = ev.getEventType();
    }

    private String openedFileFullName;

    public void setOpenedFile(String fullName){
        this.openedFileFullName = fullName;
    }

    public String getOpenedFileFullName(){
        return this.openedFileFullName;
    }
}
