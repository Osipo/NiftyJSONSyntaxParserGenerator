package bmstu.iu7m.osipov.ui.controllers;

import bmstu.iu7m.osipov.ui.models.EditorModel;
import bmstu.iu7m.osipov.ui.views.EditorView;
import javafx.fxml.FXML;
import javafx.scene.control.Tooltip;
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
        //wait for JTextPane initialization.
        try {
            swi_awaiter.await();
        }catch (InterruptedException ex){
            System.out.println("Cannot interrupt GUI_FX_Thread: cannot wait Swing GUI AWT Thread (to init JTextPane)");
        }
        super.saveUIComponents();
        if(model != null) {
            model.setOutput(editor);
            editor_file_name.textProperty().bind(model.editedFileNameProperty());
            Tooltip t = new Tooltip();
            t.textProperty().bind(editor_file_name.textProperty());
            editor_file_name.setTooltip(t);
            System.out.println("EditorModel was set");
        }

        else
            System.out.println("Model is null");
    }
}
