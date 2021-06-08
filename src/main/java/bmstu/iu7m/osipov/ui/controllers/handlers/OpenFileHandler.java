package bmstu.iu7m.osipov.ui.controllers.handlers;

import bmstu.iu7m.osipov.events.OpenFileActionEvent;
import bmstu.iu7m.osipov.ui.models.EditorModel;
import bmstu.iu7m.osipov.ui.models.entities.DirectoryEntryItem;
import bmstu.iu7m.osipov.ui.models.entities.FileEntryItem;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class OpenFileHandler implements EventHandler<ActionEvent> {

    protected ObjectProperty<TreeItem<FileEntryItem>> selected_item;
    protected Stage parent_win;
    protected EditorModel editorModel;
    private String oldSelectedDir = null;
    public OpenFileHandler(Stage parent_win, EditorModel editorModel){
        this.parent_win = parent_win;
        this.editorModel = editorModel;
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
        if(selected_item.get() != null && selected_item.get().getValue() instanceof DirectoryEntryItem) {
            this.oldSelectedDir = selected_item.get().getValue().getFullFileName();
            dialog.setInitialDirectory(new File(this.oldSelectedDir));
        }
        else if(this.oldSelectedDir != null){
            dialog.setInitialDirectory(new File(this.oldSelectedDir));
        }
        File f = dialog.showOpenDialog(parent_win);

        //If we picked a new (unopened) file.
        if(f != null &&
                (editorModel.getEditedFileName() == null
                        || !editorModel.getEditedFileName().equals(f.getAbsolutePath())
                )
        )
        {
            Alert mbox = new Alert(Alert.AlertType.INFORMATION);
            mbox.setContentText("Selected: "+f.getAbsolutePath());
            mbox.show();
            editorModel.getFileContent(f);
            editorModel.setEditedFileName(f.getAbsolutePath());
        }
        else if(event instanceof OpenFileActionEvent){
            ((OpenFileActionEvent) event).setCanceled(true);
        }
    }
}
