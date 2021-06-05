package bmstu.iu7m.osipov.ui.controllers.handlers;

import bmstu.iu7m.osipov.events.OpenFileActionEvent;
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
        OpenFileActionEvent nev = new OpenFileActionEvent(event);
        super.handle(nev);
        TreeItem<FileEntryItem> p = this.treeCtrlModel.getView().getTree().getRoot();
        if(nev.getOpenedFileFullName() != null){
            System.out.println("From UpdateTreeView:: "+nev.getOpenedFileFullName());
        }
    }

}
