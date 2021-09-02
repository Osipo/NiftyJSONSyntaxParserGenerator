package bmstu.iu7m.osipov.ui.controllers.handlers;

import bmstu.iu7m.osipov.Main;
import bmstu.iu7m.osipov.configurations.ImageNames;
import bmstu.iu7m.osipov.configurations.ResourcesConfiguration;
import bmstu.iu7m.osipov.ui.modals.CreateFileDialog;
import bmstu.iu7m.osipov.ui.models.EditorModel;
import bmstu.iu7m.osipov.ui.models.TreeFilesModel;
import bmstu.iu7m.osipov.ui.models.entities.DirectoryEntryItem;
import bmstu.iu7m.osipov.ui.models.entities.FileEntryItem;
import bmstu.iu7m.osipov.ui.models.entities.RegularFileEntryItem;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class CreateFileHandler extends OpenFileHandler implements ObserverEventHandler<ActionEvent> {

    private CreateFileDialog dialog;// singleton modal window.

    protected TreeFilesModel treeModel;
    protected final boolean isDirectory;

    public CreateFileHandler(EditorModel model, TreeFilesModel treeModel, boolean isDirectory, CreateFileDialog dialog){
        super(model);
        this.treeModel = treeModel;
        this.isDirectory = isDirectory;
        this.dialog = dialog;
    }

    public CreateFileHandler(EditorModel model, TreeFilesModel treeModel, CreateFileDialog dialog) {
        this(model, treeModel, false, dialog);
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

                File f = new File(fullName);
                System.out.println("Fullname: \""+fullName+"\"");
                try{
                    if(!isDirectory && f.createNewFile()){
                        System.out.println("New file was created.");
                        RegularFileEntryItem nentry = new RegularFileEntryItem(fname.get());
                        nentry.setFullFileName(fullName);
                        TreeItem<FileEntryItem> nitem = new TreeItem<>(nentry);
                        nitem.setGraphic(
                                new ImageView(
                                        ResourcesConfiguration.getImgs().get(ImageNames.IMG_FILE)
                                )
                        );
                        selected_item.get().getChildren().add(nitem);
                        selected_item.get().setExpanded(true);
                        treeModel.getView().getTree().getSelectionModel().clearSelection();
                        treeModel.getView().getTree().getSelectionModel().select(nitem);
                        super.handle(event);
                    }
                    else if(isDirectory && f.mkdir()){
                        System.out.println("New directory was created.");
                        DirectoryEntryItem nentry = new DirectoryEntryItem(fname.get());
                        nentry.setFullFileName(fullName);
                        TreeItem<FileEntryItem> nitem = new TreeItem<>(nentry);
                        nitem.setGraphic(
                                new ImageView(
                                        ResourcesConfiguration.getImgs().get(ImageNames.IMG_DIR)
                                )
                        );
                        selected_item.get().getChildren().add(nitem);
                        selected_item.get().setExpanded(true);
                        treeModel.getView().getTree().getSelectionModel().clearSelection();
                        treeModel.getView().getTree().getSelectionModel().select(nitem);
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
