package bmstu.iu7m.osipov.ui.controllers.handlers;

import bmstu.iu7m.osipov.ui.models.SyntaxAnalyzer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class ShowParserHandler extends ShowAnalyzerPartsHandler implements EventHandler<ActionEvent> {

    public ShowParserHandler(SyntaxAnalyzer sa){
        super(sa);
    }

    @Override
    public void handle(ActionEvent event) {
        //System.out.println("Test show parser");
    }
}
