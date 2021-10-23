package bmstu.iu7m.osipov.ui.controllers.handlers;

import bmstu.iu7m.osipov.ui.models.ParserGeneratorModel;
import bmstu.iu7m.osipov.ui.models.TreeFilesModel;
import bmstu.iu7m.osipov.ui.models.entities.RegularFileEntryItem;
import javafx.event.ActionEvent;

public class CreateLexerHandler extends ParserGeneratorHandlers<ActionEvent> implements ObserverEventHandler<ActionEvent> {
    public CreateLexerHandler(ParserGeneratorModel m, TreeFilesModel treeModel) {
        super(m, treeModel);
    }

    @Override
    public void handle(ActionEvent event) {
        System.out.println("Init Lexer creation.");
        //for regular files (NON-dirs)
        if(selected_item.get() != null && selected_item.get().getValue() instanceof RegularFileEntryItem){
            initLexer();
        }
        else{
            System.out.println("Cannot create Lexer. You must specify a regular file of Grammar's description.");
        }
    }
}
