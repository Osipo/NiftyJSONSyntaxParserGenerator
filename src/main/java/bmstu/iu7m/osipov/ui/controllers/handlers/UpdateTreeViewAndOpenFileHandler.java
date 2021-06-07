package bmstu.iu7m.osipov.ui.controllers.handlers;

import bmstu.iu7m.osipov.exceptions.WrongOrderOfArgumentsException;
import bmstu.iu7m.osipov.services.files.PathStringUtils;
import bmstu.iu7m.osipov.ui.models.EditorModel;
import bmstu.iu7m.osipov.ui.models.TreeFilesModel;
import bmstu.iu7m.osipov.ui.models.entities.FileEntryItem;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;
import javafx.stage.Stage;

public class UpdateTreeViewAndOpenFileHandler extends OpenFileHandler implements EventHandler<ActionEvent> {

    private TreeFilesModel treeCtrlModel;

    public UpdateTreeViewAndOpenFileHandler(Stage parent_win, EditorModel editorModel, TreeFilesModel treeCtrlModel){
        super(parent_win, editorModel);
        this.treeCtrlModel = treeCtrlModel;
    }

    @Override
    public void handle(ActionEvent event) {
        super.handle(event);
        TreeItem<FileEntryItem> p = this.treeCtrlModel.getView().getTree().getRoot();
        TreeItem<FileEntryItem> selected = null;
        if(this.editorModel.getEditedFileName() == null) {
            return;
        }
        String p1 = this.editorModel.getEditedFileName(); //p1 = selected file name
        String p2 = p.getValue().getFullFileName();//p2 = current root working dir.
        String subpath = null;
        try{
            subpath = PathStringUtils.getSubtraction(p1, p2);


            selected = this.treeCtrlModel.findFileByPath(p, subpath);
            this.treeCtrlModel.getView().getTree().getSelectionModel().select(selected);
        }
        catch (WrongOrderOfArgumentsException e){
            // File entry is out of the TreeView > create new root and add new TreeItems to view.

            //new root directory.
            String nrootDirName = PathStringUtils.getUnion(p1, p2);
            TreeItem<FileEntryItem> nrootDir = this.treeCtrlModel.getSpecificFileEntriesIn(nrootDirName, (x) -> !x.equals(p2));
            try {
                subpath = PathStringUtils.getSubtraction(p1, nrootDirName);
            }
            catch (WrongOrderOfArgumentsException e2){

            }
            this.treeCtrlModel.getView().getTree().setRoot(nrootDir);

            //adjust old root to the new one.
            nrootDir.getChildren().add(p);

            //add start searching at new hierarchy.
            selected = this.treeCtrlModel.findFileByPath(nrootDir, subpath);
            this.treeCtrlModel.getView().getTree().getSelectionModel().select(selected);
        }
    }
}
