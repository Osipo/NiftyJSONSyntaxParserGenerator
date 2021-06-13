package bmstu.iu7m.osipov.ui.models;

import bmstu.iu7m.osipov.services.files.FileProcessService;
import bmstu.iu7m.osipov.ui.views.EditorView;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;

public class EditorModel {

    @Autowired
    private FileProcessService rwservice;

    private StringProperty editedFileName;

    private EditorView view;

    public EditorModel(){
        System.out.println("EditorModel constructor call.");
        this.editedFileName = new SimpleStringProperty(this,"editedFileName", null);
    }

    public void setView(EditorView view){
        this.view = view;
    }

    public EditorView getView(){
        return this.view;
    }

    public final void setEditedFileName(String fname){
        this.editedFileName.set(fname);
    }

    public StringProperty editedFileNameProperty(){
        return this.editedFileName;
    }

    public final String getEditedFileName(){
        return this.editedFileName.get();
    }

    public void getFileContent(File f){
        System.out.println("Extract content of selected file");
        rwservice.readFromFile(f, view.getEditor());
    }

    public void updateFile(){
        try {
            rwservice.writeToFile(this.editedFileName.get(), view.getEditor());
        }catch (FileNotFoundException e){
            System.out.println("Non-existed file removed from EditorView");
            this.view.getEditor().setText("");
            this.editedFileName.set("");
        }
    }

    public void clearFileContent(){
        System.out.println("Closing file: \""+editedFileName.get()+"\"");
        this.view.getEditor().setText("");
        this.editedFileName.set("");
    }
}