package bmstu.iu7m.osipov.ui.controllers;

import bmstu.iu7m.osipov.Main;
import bmstu.iu7m.osipov.services.files.FileLocatorService;
import bmstu.iu7m.osipov.services.files.FileRetrievalService;
import bmstu.iu7m.osipov.ui.models.TreeFilesModel;
import bmstu.iu7m.osipov.ui.models.entities.FileEntryItem;
import bmstu.iu7m.osipov.ui.views.TreeFilesView;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TreeItem;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;


public class TreeFilesController extends TreeFilesView {

    @Autowired
    private TreeFilesModel model;

    public TreeFilesController() {
        System.out.println("TreeFilesController: constructor");
    }

    @FXML
    public void initialize(){
        System.out.println("TreeFilesController: FXML Loaded.");
        super.initView();
    }

    public TreeFilesModel getModel(){
        return this.model;
    }

    @PostConstruct
    public void init(){
        System.out.println("Post Construct of TreeFilesController bean");
        super.saveUIComponents();
        if(model != null) {
            loadFiles(this.model.getFileEntriesIn(System.getProperty("user.dir") + Main.PATH_SEPARATOR));
        }
        else
            throw new IllegalStateException("Cannot initiate TreeFilesModel for TreeFilesController");

        model.setView(this);

        model.selectedItemProperty().bind(tree.getSelectionModel().selectedItemProperty());
        model.selectedOptionProperty().bind(o_group.selectedToggleProperty());
        model.textProperty().bind(searchInput.textProperty());

        //set action for search button
        search.setOnAction((event -> {
            if(model.getSelectedOption() == null){
                System.out.println("Select type of entries to search!");
                return;
            }
            RadioButton radioitem = ((RadioButton) model.getSelectedOption());
            String type = uiStore.toEnglish().get(radioitem.getId());

            TreeItem<FileEntryItem> pdir = model.getSelectedItem();
            if(pdir == null){
                System.out.println("Select directory where to search (or file) from TreeView!");
                return;
            }
            pdir = model.findFileAt(pdir, type, searchInput.getText());
            if(pdir !=  null)
                tree.getSelectionModel().select(pdir);
        }));
    }

}
