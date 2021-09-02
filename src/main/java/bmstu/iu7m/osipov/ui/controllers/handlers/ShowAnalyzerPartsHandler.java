package bmstu.iu7m.osipov.ui.controllers.handlers;

import bmstu.iu7m.osipov.ui.modals.ImageWindow;
import bmstu.iu7m.osipov.ui.models.SyntaxAnalyzer;
import javafx.event.ActionEvent;
import javafx.event.Event;

public abstract class ShowAnalyzerPartsHandler<T extends Event> extends ObserverBaseEventHandler<T>  {
    protected SyntaxAnalyzer sa;

    protected ImageWindow imgWin;

    public ShowAnalyzerPartsHandler(SyntaxAnalyzer sa, ImageWindow imgWin){
        this.sa = sa;
        this.imgWin = imgWin;
    }
}
