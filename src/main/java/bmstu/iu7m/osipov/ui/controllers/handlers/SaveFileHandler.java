package bmstu.iu7m.osipov.ui.controllers.handlers;

import bmstu.iu7m.osipov.ui.models.EditorModel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class SaveFileHandler extends ObserverBaseEventHandler<ActionEvent> implements ObserverEventHandler<ActionEvent> {

    protected EditorModel editorModel;

    public SaveFileHandler(EditorModel m){
        this.editorModel = m;
    }

    @Override
    public void handle(ActionEvent event) {
        System.out.println("SaveFile action");
        if(editorModel.getEditedFileName() != null && editorModel.getEditedFileName().length() > 0){
            editorModel.updateFile();
        }
    }
}
