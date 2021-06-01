package bmstu.iu7m.osipov.ui.controllers;

import bmstu.iu7m.osipov.ui.models.EditorModel;
import bmstu.iu7m.osipov.ui.views.EditorView;
import javafx.fxml.FXML;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

public class EditorFilesController extends EditorView {

    @Autowired
    private EditorModel model;

    public EditorFilesController(){
        System.out.println("EditorFilesController: constructor");
    }

    public EditorModel getModel(){
        return this.model;
    }

    @FXML
    public void initialize(){
        super.initView();
    }

    @PostConstruct
    public void init(){
        System.out.println("Post construct of bean: EditorFilesController");
        super.saveUIComponents();
        if(model != null) {
            model.setOutput(editor);
            System.out.println("model created mmmmm");
        }
        else
            System.out.println("Model is null");
    }
}
