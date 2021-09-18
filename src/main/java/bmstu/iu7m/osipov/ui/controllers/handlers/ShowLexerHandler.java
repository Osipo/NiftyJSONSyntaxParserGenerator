package bmstu.iu7m.osipov.ui.controllers.handlers;

import bmstu.iu7m.osipov.ui.modals.ImageWindow;
import bmstu.iu7m.osipov.ui.models.SyntaxAnalyzer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ShowLexerHandler extends ShowAnalyzerPartsHandler<ActionEvent> implements ObserverEventHandler<ActionEvent> {

    public ShowLexerHandler(SyntaxAnalyzer sa, ImageWindow w){
        super(sa, w);
    }

    @Override
    public void handle(ActionEvent event) {
        File img = null;
        try {
            img = sa.getCurLexer().getImageFromDot();
        } catch (NullPointerException e) {
            System.out.println("Cannot show lexer as it has not been created!");
        }
        catch (IOException e){
            System.out.println("Cannot create file for lexer description. File is too big or inaccessible.");
        }
        if(img == null)
            return;

        try {
            imgWin.setImage(img);
        } catch (FileNotFoundException e) {
            System.out.println("Cannot make image. File '"+img.getAbsolutePath()+"' was not corrected");
        }
    }
}
