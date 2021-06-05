package bmstu.iu7m.osipov.ui.controllers.handlers;

import bmstu.iu7m.osipov.events.OpenFileActionEvent;
import bmstu.iu7m.osipov.services.files.PathUtils;
import bmstu.iu7m.osipov.ui.models.EditorModel;
import bmstu.iu7m.osipov.ui.models.TreeFilesModel;
import bmstu.iu7m.osipov.ui.models.entities.FileEntryItem;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.File;

public class OpenFileHandler implements EventHandler<ActionEvent> {

    protected ObjectProperty<TreeItem<FileEntryItem>> selected_item;
    protected Stage parent_win;
    protected EditorModel editorModel;
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
        if(selected_item.get() != null)
            dialog.setInitialDirectory(new File(selected_item.get().getValue().getFullFileName()));
        File f = dialog.showOpenDialog(parent_win);
        if(f != null){
            Alert mbox = new Alert(Alert.AlertType.INFORMATION);
            mbox.setContentText("Selected: "+f.getAbsolutePath());
            mbox.show();
            editorModel.getFileContent(f);
            editorModel.setEditedFileName(f.getAbsolutePath());
            if(event instanceof OpenFileActionEvent){
                OpenFileActionEvent ninfo = (OpenFileActionEvent) event;
                ninfo.setOpenedFile(f.getAbsolutePath());
            }
        }
    }
}
