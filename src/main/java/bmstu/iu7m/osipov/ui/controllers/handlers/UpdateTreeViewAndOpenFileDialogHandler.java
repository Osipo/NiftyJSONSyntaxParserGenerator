package bmstu.iu7m.osipov.ui.controllers.handlers;

import bmstu.iu7m.osipov.Main;
import bmstu.iu7m.osipov.events.OpenFileActionEvent;
import bmstu.iu7m.osipov.exceptions.WrongOrderOfArgumentsException;
import bmstu.iu7m.osipov.services.files.PathStringUtils;
import bmstu.iu7m.osipov.ui.models.EditorModel;
import bmstu.iu7m.osipov.ui.models.TreeFilesModel;
import bmstu.iu7m.osipov.ui.models.entities.FileEntryItem;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;
import javafx.stage.Stage;

public class UpdateTreeViewAndOpenFileDialogHandler extends OpenFileDialogHandler implements EventHandler<ActionEvent> {

    private TreeFilesModel treeCtrlModel;

    public UpdateTreeViewAndOpenFileDialogHandler(Stage parent_win, EditorModel editorModel, TreeFilesModel treeCtrlModel){
        super(parent_win, editorModel);
        this.treeCtrlModel = treeCtrlModel;
        setChild(this);
    }

    @Override
    public void handle(ActionEvent event) {
        OpenFileActionEvent action = null;
        if(event instanceof OpenFileActionEvent){
            action = (OpenFileActionEvent) event;
        }
        else
            action = new OpenFileActionEvent(event);
        super.handle(action);
        if(action.isCanceled() && action.isSelected()){
            System.out.println("OpenFileDialog > selected cancel action.");
            this.dialogFlag.set(false);// allow open a new dialog as it was previously closed.
            return;
        }
        else if(action.isCanceled()) {
            System.out.println("Dialog is still active! Open file or decline this action!");
            return;
        }
        TreeItem<FileEntryItem> root = this.treeCtrlModel.getView().getTree().getRoot();
        TreeItem<FileEntryItem> selected = null;
        String p1 = this.editorModel.getEditedFileName(); //p1 = selected file name
        String p2 = root.getValue().getFullFileName();//p2 = current root working dir.
        String subpath = null;
        try{
            subpath = PathStringUtils.getSubtraction(p1, p2);
            selected = this.treeCtrlModel.findFileByPath(root, subpath);
            //this.treeCtrlModel.getView().getTree().getSelectionModel().clearSelection();
            this.treeCtrlModel.getView().getTree().getSelectionModel().select(selected);
        }
        // p1 > p2 BUT selected == null.
        // AS subpath part of that p1 is not belongs to the root! (out of the TreeView)
        catch (NullPointerException nf){
            System.out.println("Cannot find file at path: "+p1);
            System.out.println("Subpath is: "+subpath);
            if(subpath != null && subpath.length() > 0){
                setNewTreeHierarchy(root, p1, subpath);
            }
        }
        catch (WrongOrderOfArgumentsException e){
            // File entry is out of the TreeView > create new root and add new TreeItems to view.
            setNewTreeHierarchy(root, p1, null);
        }
        finally {
            System.out.println("Dialog process is finished.");
            this.dialogFlag.set(false);
        }
    }


    private void setNewTreeHierarchy(TreeItem<FileEntryItem> root, String selFile, String subpath){
        TreeItem<FileEntryItem> selected = null;
        String oldRoot = root.getValue().getFullFileName();

        //new root directory.
        String nrootDirName = PathStringUtils.getUnion(selFile, oldRoot);

        //append path separator for new root directory.
        if(!nrootDirName.endsWith(Main.PATH_SEPARATOR)) {
            nrootDirName = nrootDirName + Main.PATH_SEPARATOR;
        }
        String orootDirName = null;

        TreeItem<FileEntryItem> nrootDir = this.treeCtrlModel.getSpecificFileEntriesIn(nrootDirName,
                (x) -> {
                    return !x.equals(oldRoot);
        });

        try {
            if(subpath == null || subpath.length() == 0)
                subpath = PathStringUtils.getSubtraction(selFile, nrootDirName);
            orootDirName = PathStringUtils.getSubtraction(oldRoot, nrootDirName);
        }
        catch (WrongOrderOfArgumentsException e2){

        }
        this.treeCtrlModel.getView().getTree().getSelectionModel().clearSelection();
        this.treeCtrlModel.getView().getTree().setRoot(nrootDir);
        root.getValue().setFileName(orootDirName);


        //adjust old root to the new hierarchy
        //as old root is just a part of the new tree
        //we must expand its name to the path.
        this.treeCtrlModel.findFileByPath(nrootDir, orootDirName);

        //and start searching at new hierarchy.
        selected = this.treeCtrlModel.findFileByPath(nrootDir, subpath);
        this.treeCtrlModel.getView().getTree().getSelectionModel().select(selected);
    }
}
