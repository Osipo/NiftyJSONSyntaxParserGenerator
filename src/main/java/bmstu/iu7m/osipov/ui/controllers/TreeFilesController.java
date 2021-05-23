package bmstu.iu7m.osipov.ui.controllers;

import bmstu.iu7m.osipov.services.files.FileLocatorService;
import bmstu.iu7m.osipov.services.files.FileRetrievalService;
import bmstu.iu7m.osipov.ui.models.TreeFilesModel;
import bmstu.iu7m.osipov.ui.views.TreeFilesView;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;


public class TreeFilesController extends TreeFilesView {

    @Autowired
    private FileLocatorService fservice;

    @Autowired
    private FileRetrievalService searchService;

    private TreeFilesModel model;

    public TreeFilesController() {
        this.model = new TreeFilesModel();
        System.out.println("TreeFilesController: constructor");
    }

    @FXML
    public void initialize(){
        System.out.println("TreeFilesController: FXML Loaded.");
        super.initView();
    }

    @PostConstruct
    public void init(){
        System.out.println("Post Construct of TreeFilesController bean");
        if(fservice != null) {
            System.out.println(fservice.getClass().getName());
            //loadFiles(this.fservice.getFileEntriesTo(System.getProperty("user.dir")));
            //loadFiles(this.fservice.getAllFileEntriesFrom(System.getProperty("user.dir")));
            loadFiles(this.fservice.getFileEntriesIn(System.getProperty("user.dir")));
        }
        model.selectedItemProperty().bind(tree.getSelectionModel().selectedItemProperty());
        model.selectedOptionProperty().bind(o_group.selectedToggleProperty());
        model.textProperty().bind(searchInput.textProperty());

        search.setOnAction((event -> {
            //String type = ((RadioButton) model.getSelectedOption()).getText();
            //String pdir = model.getSelectedItem().getValue().getFullFileName();
            //String f = searchService.findEntry(pdir, type, searchInput.getText());
            //f = DirUtils.getIntersection(pdir, f);

        }));
    }

}
