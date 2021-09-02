package bmstu.iu7m.osipov.ui.controllers.handlers;

import bmstu.iu7m.osipov.Main;
import bmstu.iu7m.osipov.exceptions.InvalidJsonGrammarException;
import bmstu.iu7m.osipov.services.grammars.Grammar;
import bmstu.iu7m.osipov.services.lexers.DFALexer;
import bmstu.iu7m.osipov.services.lexers.FALexerGenerator;
import bmstu.iu7m.osipov.services.lexers.RegexRPNParser;
import bmstu.iu7m.osipov.services.parsers.json.elements.JsonObject;
import bmstu.iu7m.osipov.structures.automats.DFA;
import bmstu.iu7m.osipov.ui.models.ParserGeneratorModel;
import bmstu.iu7m.osipov.ui.models.TreeFilesModel;
import bmstu.iu7m.osipov.ui.models.entities.FileEntryItem;
import bmstu.iu7m.osipov.ui.models.entities.RegularFileEntryItem;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;

import java.io.File;
import java.io.IOException;

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
