package bmstu.iu7m.osipov.ui.models;

import bmstu.iu7m.osipov.services.files.FileLocatorService;
import bmstu.iu7m.osipov.services.files.FileRetrievalService;
import bmstu.iu7m.osipov.services.files.TreeFilesReaderService;
import bmstu.iu7m.osipov.ui.models.entities.FileEntryItem;
import bmstu.iu7m.osipov.ui.views.TreeFilesView;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.TreeItem;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

public class TreeFilesModel {
    private ObjectProperty<TreeItem<FileEntryItem>> selected_item;// current selected item on TreeView
    private ObjectProperty<Toggle> selected_option;//current selected radio on top from TreeView.
    private StringProperty text;// text of TextField at top from TreeView.


    @Autowired
    private FileLocatorService fservice;

    @Autowired
    private FileRetrievalService searchService;

    @Autowired
    private TreeFilesReaderService treeScannerService;

    private TreeFilesView view;

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

    public final void setSelectedItem(TreeItem<FileEntryItem> selected){
        this.selected_item.set(selected);
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

    public void setView(TreeFilesView v){
        this.view = v;
    }

    public TreeFilesView getView(){
        return this.view;
    }

    //--------------------------------------
    //Business logic services
    //--------------------------------------
    public TreeItem<FileEntryItem> getFileEntriesIn(String pdir){
        return this.fservice.getFileEntriesIn(pdir);
    }

    public TreeItem<FileEntryItem> findFileAt(TreeItem<FileEntryItem> pentry, String type, String fname){
        return this.searchService.findEntry(pentry, type, fname);
    }

}
