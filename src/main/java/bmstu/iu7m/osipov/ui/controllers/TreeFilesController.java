package bmstu.iu7m.osipov.ui.controllers;

import bmstu.iu7m.osipov.services.files.FileLocatorService;
import bmstu.iu7m.osipov.ui.views.TreeFilesView;
import javafx.fxml.FXML;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;


public class TreeFilesController extends TreeFilesView {

    @Autowired
    private FileLocatorService fservice;

    public TreeFilesController() {
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
    }

}
