package bmstu.iu7m.osipov.ui.controllers.handlers;

import bmstu.iu7m.osipov.ui.modals.ImageWindow;
import bmstu.iu7m.osipov.ui.models.SyntaxAnalyzer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ShowParserHandler extends ShowAnalyzerPartsHandler<ActionEvent> implements ObserverEventHandler<ActionEvent> {

    public ShowParserHandler(SyntaxAnalyzer sa, ImageWindow w){
        super(sa, w);
    }

    @Override
    public void handle(ActionEvent event) {
        File img = null;
        try {
            img = sa.getCurParser().getImage();
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
        if(img == null)
            return;

        try {
            imgWin.setImage(img);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
