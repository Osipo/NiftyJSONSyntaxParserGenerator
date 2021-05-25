package bmstu.iu7m.osipov.ui.models;

import bmstu.iu7m.osipov.ui.models.entities.FileEntryItem;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.TreeItem;

public class TreeFilesModel {
    private ObjectProperty<Toggle> selected_option;
    private ObjectProperty<TreeItem<FileEntryItem>> selected_item;
    private StringProperty text;

    public TreeFilesModel(){
        this.selected_item = new SimpleObjectProperty<>(this, "selectedItem", null);
        this.selected_option = new SimpleObjectProperty<>(this,"selectedOption", null);
        this.text = new SimpleStringProperty(this, "text",null);

        /* just show changes */
        this.selected_item.addListener(((observable, oldValue, newValue) -> {
            System.out.println("Model_changed_item: "+newValue.getValue().getFullFileName());
        }));
        this.selected_option.addListener(((observable, oldValue, newValue) -> {
            RadioButton btn = (RadioButton) newValue;
            System.out.println("Model_changed_option: "+btn.getText());
        }));
        this.text.addListener(((observable, oldValue, newValue) -> {
            System.out.println("Model_changed_text: "+newValue);
        }));
    }

    public ObjectProperty<TreeItem<FileEntryItem>> selectedItemProperty(){
        return this.selected_item;
    }

    public final TreeItem<FileEntryItem> getSelectedItem(){
        return this.selected_item.get();
    }

    public ObjectProperty<Toggle> selectedOptionProperty(){
        return this.selected_option;
    }

    public final Toggle getSelectedOption(){
        return this.selected_option.get();
    }

    public StringProperty textProperty(){
        return this.text;
    }

    public final String getText(){
        return this.text.get();
    }

}
