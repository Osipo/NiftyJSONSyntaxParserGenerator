package bmstu.iu7m.osipov.ui.models;

import bmstu.iu7m.osipov.services.files.FileProcessService;
import javafx.scene.control.TextArea;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public class EditorModel {

    @Autowired
    private FileProcessService rwservice;

    private TextArea output;


    public void setOutput(TextArea textArea){
        this.output = textArea;
    }

    public void getFileContent(){
        System.out.println("Extract content of selected file");
    }
}