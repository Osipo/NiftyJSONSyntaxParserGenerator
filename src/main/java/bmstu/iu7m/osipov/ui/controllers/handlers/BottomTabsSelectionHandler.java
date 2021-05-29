package bmstu.iu7m.osipov.ui.controllers.handlers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

public class BottomTabsSelectionHandler implements EventHandler<ActionEvent> {

    private Pane parent;
    private Node child;

    public BottomTabsSelectionHandler(Pane p, Node c){
        this.parent = p;
        this.child = c;
    }
    @Override
    public void handle(ActionEvent event) {
        if(parent.getChildren().size() == 1){ /* No active tabs yet. */
            parent.getChildren().add(0, child);
        }
        else if(parent.getChildren().get(0).getId().equals(child.getId())){/* the same active tab */
            parent.getChildren().remove(child);
        }
        else{/* new active tab was selected */
            parent.getChildren().remove(0);
            parent.getChildren().add(0, child);
        }
    }
}
