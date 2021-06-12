package bmstu.iu7m.osipov.ui.controllers.handlers;

import bmstu.iu7m.osipov.Main;
import bmstu.iu7m.osipov.ui.modals.CreateFileDialog;
import bmstu.iu7m.osipov.ui.models.EditorModel;
import bmstu.iu7m.osipov.ui.models.entities.DirectoryEntryItem;
import bmstu.iu7m.osipov.ui.models.stores.UIComponentStore;
import bmstu.iu7m.osipov.utils.Dialogs;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.concurrent.Executors;

public class CreateFileHandler extends OpenFileHandler implements EventHandler<ActionEvent> {


    private boolean isDirectory;
    private CreateFileDialog dialog;
    public CreateFileHandler(EditorModel model, boolean isDirectory, CreateFileDialog dialog){
        super(model);
        this.isDirectory = isDirectory;
        this.dialog = dialog;
    }

    public CreateFileHandler(EditorModel model, CreateFileDialog dialog) {
        this(model, false, dialog);
    }

    @Override
    public void handle(ActionEvent event) {
        //if parent dir is selected and no active dialog yet.
        if(!this.dialog.isOpened() && selected_item.get() != null && selected_item.get().getValue() instanceof DirectoryEntryItem){
            Optional<String> fname = dialog.showAndWait();//dialog is opened now.
            if(fname.isPresent()) {
                System.out.println("New file name is \"" + fname.get() + "\"");

                //Parse to valid Path string.
                String fullName = selected_item.get().getValue().getFullFileName();
                if(!fullName.endsWith(Main.PATH_SEPARATOR))
                    fullName = fullName + Main.PATH_SEPARATOR;
                fullName = fullName + fname.get();
                if(isDirectory)
                    fullName = fullName + Main.PATH_SEPARATOR;

                File f = new File(fullName);
                System.out.println("Fullname: \""+fullName+"\"");
                try{
                    if(!isDirectory && f.createNewFile()){
                        System.out.println("New file was created.");
                        selected_item.get().setExpanded(true);
                        super.handle(event);
                    }
                    else if(isDirectory && f.mkdir()){
                        System.out.println("New directory was created.");
                        selected_item.get().setExpanded(true);
                        super.handle(event);
                    }
                    else{
                        //or show alert.
                        System.out.println("File with name: \""+fullName+"\" is already exists.");
                    }
                }
                catch (IOException e){
                    System.out.println("Cannot create file with name: \""+ fullName + "\"");
                }
            }
            // dialog was closed earlier. Set it to the other handlers as we finished process.
            this.dialog.setOpened(false);
        }
        else if(selected_item.get() == null){
            System.out.println("Select a directory where new file will be created!");
        }
        else{
            System.out.println("Dialog is still active now. Finish the operation!");
        }
    }
}
