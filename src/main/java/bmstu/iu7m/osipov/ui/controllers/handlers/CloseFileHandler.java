package bmstu.iu7m.osipov.ui.controllers.handlers;

import bmstu.iu7m.osipov.ui.models.EditorModel;
import bmstu.iu7m.osipov.ui.models.entities.DirectoryEntryItem;
import bmstu.iu7m.osipov.ui.models.entities.FileEntryItem;
import bmstu.iu7m.osipov.ui.models.entities.RegularFileEntryItem;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;

public class CloseFileHandler extends ObserverBaseEventHandler<ActionEvent> implements ObserverEventHandler<ActionEvent> {


    protected final ObjectProperty<TreeItem<FileEntryItem>> selected_item;
    protected EditorModel editorModel;

    public CloseFileHandler(EditorModel model){
        this.editorModel = model;
        this.selected_item = new SimpleObjectProperty<>(this, "selectedItem", null);
    }

    @Override
    public void handle(ActionEvent event) {
        if(selected_item.get() != null
                && selected_item.get().getValue() instanceof RegularFileEntryItem
                && editorModel.getEditedFileName() != null
                && selected_item.get().getValue().getFullFileName().equals(editorModel.getEditedFileName())
        )
        {
            editorModel.clearFileContent();
        }
        else if(selected_item.get() != null
                && selected_item.get().getValue() instanceof DirectoryEntryItem
                && selected_item.get().isExpanded()
        )
        {
            selected_item.get().setExpanded(false);
        }
    }

    public final ObjectProperty<TreeItem<FileEntryItem>> selectedItemProperty(){
        return this.selected_item;
    }

    public final TreeItem<FileEntryItem> getSelectedItem(){
        return this.selected_item.get();
    }
}
