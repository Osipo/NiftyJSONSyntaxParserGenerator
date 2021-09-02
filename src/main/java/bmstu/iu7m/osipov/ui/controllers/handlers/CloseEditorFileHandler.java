package bmstu.iu7m.osipov.ui.controllers.handlers;

import bmstu.iu7m.osipov.ui.models.EditorModel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class CloseEditorFileHandler extends CloseFileHandler implements ObserverEventHandler<ActionEvent> {
    public CloseEditorFileHandler(EditorModel model) {
        super(model);
    }

    @Override
    public void handle(ActionEvent e){
        if(editorModel.getEditedFileName() != null && editorModel.getEditedFileName().length() > 0){
            editorModel.clearFileContent();
        }
    }
}
