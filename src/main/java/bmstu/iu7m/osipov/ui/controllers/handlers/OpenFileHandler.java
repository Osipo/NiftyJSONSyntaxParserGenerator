package bmstu.iu7m.osipov.ui.controllers.handlers;

import bmstu.iu7m.osipov.ui.controllers.RootWindowController;
import bmstu.iu7m.osipov.ui.models.entities.FileEntryItem;
import com.sun.deploy.uitoolkit.impl.fx.ui.FXMessageDialog;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class OpenFileHandler implements EventHandler<ActionEvent> {

    private ObjectProperty<TreeItem<FileEntryItem>> selected_item;
    private Stage parent_win;
    public OpenFileHandler(Stage parent_win){
        this.parent_win = parent_win;
        this.selected_item = new SimpleObjectProperty<>(this, "selectedItem", null);
    }

    public ObjectProperty<TreeItem<FileEntryItem>> selectedItemProperty(){
        return this.selected_item;
    }

    public final TreeItem<FileEntryItem> getSelectedItem(){
        return this.selected_item.get();
    }

    @Override
    public void handle(ActionEvent event) {
        FileChooser dialog = new FileChooser();
        if(selected_item.get() != null)
            dialog.setInitialDirectory(new File(selected_item.get().getValue().getFullFileName()));
        File f = dialog.showOpenDialog(parent_win);
        if(f != null){
            Alert mbox = new Alert(Alert.AlertType.INFORMATION);
            mbox.setContentText("Selected: "+f.getAbsolutePath());
            mbox.show();
        }
    }
}
