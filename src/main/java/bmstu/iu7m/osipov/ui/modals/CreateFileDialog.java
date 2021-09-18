package bmstu.iu7m.osipov.ui.modals;

import bmstu.iu7m.osipov.configurations.FileDialogText;
import bmstu.iu7m.osipov.configurations.UIComponentIds;
import bmstu.iu7m.osipov.ui.models.entities.TitledUIComponent;
import bmstu.iu7m.osipov.ui.models.entities.UITextComponent;
import bmstu.iu7m.osipov.ui.models.entities.UITitledComponent;
import bmstu.iu7m.osipov.ui.models.stores.UIComponentStore;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.Optional;

public class CreateFileDialog extends ModalWindow implements TitledUIComponent {

    private String name;

    public CreateFileDialog(UIComponentStore store){
        this(null, store);
    }

    public CreateFileDialog(Window parent, UIComponentStore uiStore){
        super(parent, uiStore);
        GridPane root = new GridPane();
        root.setVgap(10);
        Label label = new Label("File name: ");
        TextField fname = new TextField();

        HBox btns = new HBox();
        btns.prefWidthProperty().bind(root.prefWidthProperty());
        btns.prefHeightProperty().bind(root.prefHeightProperty());
        btns.setSpacing(20);
        btns.setAlignment(Pos.BOTTOM_CENTER);
        Button ok = new Button("Create");
        Button cancel = new Button("Cancel");
        ok.setOnAction(event -> {this.name = fname.getText(); stage.close();});
        cancel.setOnAction(event -> {stage.close();});

        root.add(label, 0,0);
        root.add(fname,1,0);
        btns.getChildren().addAll(ok, cancel);
        root.add(btns, 0, 1, 2,1);

        Scene scene = new Scene(root);
        root.prefWidthProperty().bind(scene.widthProperty());
        root.prefHeightProperty().bind(scene.heightProperty());
        stage.setMinWidth(250);
        stage.setMaxWidth(500);
        stage.setMinHeight(100);
        stage.setMaxHeight(150);
        stage.setScene(scene);


        label.setId(UIComponentIds.CreateFileDialogLabel);
        ok.setId(UIComponentIds.CreateFileDialogOkText);
        cancel.setId(UIComponentIds.CreateFileDialogCancelText);
        this.setId(UIComponentIds.CreateFileDialogTitled);
        this.uiStore.getComponents().put(UIComponentIds.CreateFileDialogLabel, new UITextComponent(label));
        this.uiStore.getComponents().put(UIComponentIds.CreateFileDialogOkText, new UITextComponent(ok));
        this.uiStore.getComponents().put(UIComponentIds.CreateFileDialogCancelText, new UITextComponent(cancel));
        this.uiStore.getComponents().put(UIComponentIds.CreateFileDialogTitled, new UITitledComponent(this));
        stage.setTitle("Create new file");
    }

    public Optional<String> showAndWait(){
        this.name = null;
        this.isOpened = true;
        stage.showAndWait();
        return Optional.ofNullable(this.name);
    }
}
