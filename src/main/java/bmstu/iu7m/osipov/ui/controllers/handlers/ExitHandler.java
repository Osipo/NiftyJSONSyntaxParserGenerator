package bmstu.iu7m.osipov.ui.controllers.handlers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

public class ExitHandler extends ObserverBaseEventHandler<ActionEvent> implements ObserverEventHandler<ActionEvent> {

    private Stage st;

    public ExitHandler(Stage st){
        this.st = st;
    }

    @Override
    public void handle(ActionEvent event) {
        st.close();
    }
}
