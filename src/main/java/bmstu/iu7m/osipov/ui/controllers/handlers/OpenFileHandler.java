package bmstu.iu7m.osipov.ui.controllers.handlers;

import bmstu.iu7m.osipov.ui.locale.LanguageName;
import bmstu.iu7m.osipov.ui.models.EditorModel;
import bmstu.iu7m.osipov.ui.models.entities.FileEntryItem;
import bmstu.iu7m.osipov.ui.models.entities.RegularFileEntryItem;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;

import java.io.File;

public class OpenFileHandler implements EventHandler<ActionEvent> {

    protected final ObjectProperty<TreeItem<FileEntryItem>> selected_item;
    protected final ObjectProperty<LanguageName> selected_language;
    protected EditorModel editorModel;

    public OpenFileHandler(EditorModel model) {
        this.editorModel = model;
        this.selected_item = new SimpleObjectProperty<>(this, "selectedItem", null);
        this.selected_language = new SimpleObjectProperty<>(this, "selectedLanguage", null);
    }

    @Override
    public void handle(ActionEvent event) {
        if(selected_item.get() != null && selected_item.get().getValue() instanceof RegularFileEntryItem){
            File f = new File(selected_item.get().getValue().getFullFileName());
            editorModel.getFileContent(f);
            editorModel.setEditedFileName(f.getAbsolutePath());
        }
        else if(selected_item.get() == null){
            System.out.println("Select file to open.");
        }
    }

    public final ObjectProperty<TreeItem<FileEntryItem>> selectedItemProperty(){
        return this.selected_item;
    }

    public final TreeItem<FileEntryItem> getSelectedItem(){
        return this.selected_item.get();
    }

    public final ObjectProperty<LanguageName> selectedLanguageProperty(){
        return this.selected_language;
    }

    public final LanguageName getSelectedLanguage(){
        return this.selected_language.get();
    }
}
