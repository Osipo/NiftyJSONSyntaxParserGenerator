package bmstu.iu7m.osipov.ui.controllers.handlers;

import bmstu.iu7m.osipov.ui.modals.ImageWindow;
import bmstu.iu7m.osipov.ui.models.SyntaxAnalyzer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class ShowLexerHandler extends ShowAnalyzerPartsHandler implements EventHandler<ActionEvent> {

    public ShowLexerHandler(SyntaxAnalyzer sa, ImageWindow w){
        super(sa, w);
    }

    @Override
    public void handle(ActionEvent event) {

    }
}
