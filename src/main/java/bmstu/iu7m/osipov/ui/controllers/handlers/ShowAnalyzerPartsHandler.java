package bmstu.iu7m.osipov.ui.controllers.handlers;

import bmstu.iu7m.osipov.ui.modals.ImageWindow;
import bmstu.iu7m.osipov.ui.models.SyntaxAnalyzer;

public class ShowAnalyzerPartsHandler {
    protected SyntaxAnalyzer sa;

    protected ImageWindow imgWin;

    public ShowAnalyzerPartsHandler(SyntaxAnalyzer sa, ImageWindow imgWin){
        this.sa = sa;
        this.imgWin = imgWin;
    }
}
