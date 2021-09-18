package bmstu.iu7m.osipov.ui.wins;

import bmstu.iu7m.osipov.configurations.UIComponentIds;
import bmstu.iu7m.osipov.ui.modals.Window;
import bmstu.iu7m.osipov.ui.models.stores.UIComponentStore;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class MessageWindow extends Window {

    private Label msg;
    public MessageWindow(UIComponentStore store){super(null, store);}

    public MessageWindow(javafx.stage.Window parent, UIComponentStore store) {
        super(parent, store);

        GridPane root = new GridPane();
        root.setVgap(10);
        Scene scene = new Scene(root);
        root.prefWidthProperty().bind(scene.widthProperty());
        root.prefHeightProperty().bind(scene.heightProperty());
        stage.setMinWidth(250);
        stage.setMaxWidth(500);
        stage.setMinHeight(100);
        stage.setMaxHeight(150);

        HBox msg_body = new HBox();
        this.msg = new Label("");
        msg_body.getChildren().add(msg);
        root.add(msg_body, 0, 0);

        this.setId(UIComponentIds.ShowMessageDialogTitled);
        stage.setScene(scene);
        stage.setTitle("Message");
    }

    public void showMessage(String msg){
        this.isOpened = true;
        this.msg.setText(msg);
        stage.show();
    }
}
