package bmstu.iu7m.osipov.ui.controllers.handlers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

public class ShowAndHideEventHandler implements EventHandler<ActionEvent> {

    private Pane parent;
    private Node child;

    public ShowAndHideEventHandler(Pane p, Node c){
        this.parent = p;
        this.child = c;
    }
    @Override
    public void handle(ActionEvent event) {
        if(parent.getChildren().size() == 1){
            parent.getChildren().add(0, child);
        }
        else{
            parent.getChildren().remove(child);
        }
    }
}
