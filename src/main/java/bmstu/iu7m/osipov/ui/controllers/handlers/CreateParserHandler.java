package bmstu.iu7m.osipov.ui.controllers.handlers;

import bmstu.iu7m.osipov.services.parsers.LRAlgorithm;
import bmstu.iu7m.osipov.services.parsers.LRParser;
import bmstu.iu7m.osipov.services.parsers.Parser;
import bmstu.iu7m.osipov.ui.models.ParserGeneratorModel;
import bmstu.iu7m.osipov.ui.models.TreeFilesModel;
import bmstu.iu7m.osipov.ui.models.entities.RegularFileEntryItem;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.io.File;

public class CreateParserHandler extends ParserGeneratorHandlers<ActionEvent> implements ObserverEventHandler<ActionEvent> {

    private boolean ignore_cur_lexer = false;

    public CreateParserHandler(ParserGeneratorModel m, TreeFilesModel treeModel, boolean ignore_cur_lexer) {
        super(m, treeModel);
        this.ignore_cur_lexer = ignore_cur_lexer;
    }



    @Override
    public void handle(ActionEvent event) {
        System.out.println("Init parser creation");
        //for regular files (NON-dirs)
        if(selected_item.get() != null && selected_item.get().getValue() instanceof RegularFileEntryItem){
            if(this.model.getCurLexer() == null || ignore_cur_lexer)
                initLexer();
            Parser parser = null;
            if(this.model.getCurLexer() != null && this.model.getGrammar() != null)
                 parser = new LRParser(this.model.getGrammar(), this.model.getCurLexer(), LRAlgorithm.SLR);
            this.model.setCurParser(parser);
        }
        else{
            System.out.println("Cannot create Parser. You must specify file of Grammar's description.");
        }
    }
}
