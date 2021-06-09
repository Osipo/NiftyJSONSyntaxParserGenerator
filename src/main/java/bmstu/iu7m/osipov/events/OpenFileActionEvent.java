package bmstu.iu7m.osipov.events;

import javafx.event.ActionEvent;

public class OpenFileActionEvent extends ActionEvent {

    private boolean canceled;
    private boolean selected;
    public OpenFileActionEvent(ActionEvent ev){
        super();
        this.source = ev.getSource();
        this.target = ev.getTarget();
        this.consumed = ev.isConsumed();
        this.canceled = false;
        this.selected = false;
    }

    public void setCanceled(boolean c){
        this.canceled = c;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }
}
