package bmstu.iu7m.osipov.ui.wins;

import bmstu.iu7m.osipov.configurations.UIComponentIds;
import bmstu.iu7m.osipov.ui.modals.Window;
import bmstu.iu7m.osipov.ui.models.stores.UIComponentStore;
import com.sun.rowset.internal.Row;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

public class MessageWindow extends Window {

    private Label msg;
    public MessageWindow(UIComponentStore store){this(null, store);}

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

        //----------------------------------------
        //--  Set columns and rows of root grid --
        //----------------------------------------
//        ColumnConstraints c1 = new ColumnConstraints();
//        ColumnConstraints c2 = new ColumnConstraints();
//        c1.setPercentWidth(50);
//        c2.setPercentWidth(50);
//        c1.setHalignment(HPos.CENTER);
//        c2.setHalignment(HPos.RIGHT);
//        c1.setHgrow(Priority.ALWAYS);
//        c2.setHgrow(Priority.ALWAYS);
//        root.getColumnConstraints().addAll(c1, c2);

        RowConstraints r1 = new RowConstraints();
        RowConstraints r2 = new RowConstraints();
        RowConstraints r3 = new RowConstraints();

        HBox hb_btns = new HBox();
        Button ok_btn = new Button("OK");
        ok_btn.addEventHandler(ActionEvent.ACTION, (e) -> {
            this.stage.close();
            this.isOpened = false;
        });
        ok_btn.setPrefSize(100, 50);
        hb_btns.setAlignment(Pos.BASELINE_CENTER);
        hb_btns.getChildren().add(ok_btn);
        root.add(hb_btns, 0, 2);

        HBox msg_body = new HBox();
        msg_body.setSpacing(20);
        msg_body.prefWidthProperty().bind(root.prefWidthProperty());
        this.msg = new Label("");
        msg_body.getChildren().add(msg);

        root.add(msg_body, 0, 1);

        this.setId(UIComponentIds.ShowMessageDialogTitled);
        stage.setScene(scene);
        stage.setTitle("Message");
    }

    public void showMessage(String msg){
        this.isOpened = true;
        this.msg.setText(msg);
        this.msg.setFont(Font.font("Tahoma", FontWeight.BLACK, FontPosture.REGULAR, 16));
        stage.show();
    }
}
