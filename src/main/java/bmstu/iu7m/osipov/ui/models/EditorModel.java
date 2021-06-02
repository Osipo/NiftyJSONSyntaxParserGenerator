package bmstu.iu7m.osipov.ui.models;

import bmstu.iu7m.osipov.services.files.FileProcessService;
import javafx.scene.control.TextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.io.File;

public class EditorModel {

    @Autowired
    private FileProcessService rwservice;

    private RSyntaxTextArea txt_pane;

    public void setOutput(RSyntaxTextArea txt_pane){this.txt_pane = txt_pane;}

    public void getFileContent(File f){
        System.out.println("Extract content of selected file");
        rwservice.readFromFile(f, txt_pane);
    }
}