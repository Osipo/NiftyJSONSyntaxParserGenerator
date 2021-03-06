package bmstu.iu7m.osipov.ui.controllers.handlers;

import bmstu.iu7m.osipov.Main;
import bmstu.iu7m.osipov.services.lexers.LanguageSymbol;
import bmstu.iu7m.osipov.services.lexers.Token;
import bmstu.iu7m.osipov.structures.trees.LinkedTree;
import bmstu.iu7m.osipov.ui.modals.ImageWindow;
import bmstu.iu7m.osipov.ui.models.ParserGeneratorModel;
import bmstu.iu7m.osipov.ui.models.TreeFilesModel;
import bmstu.iu7m.osipov.ui.models.entities.FileEntryItem;
import bmstu.iu7m.osipov.ui.models.entities.RegularFileEntryItem;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import javafx.event.ActionEvent;
import javafx.scene.control.TreeItem;

import java.io.File;
import java.io.IOException;

public class ParseFileHandler extends ParserGeneratorHandlers<ActionEvent> implements ObserverEventHandler<ActionEvent> {
    private ImageWindow w;
    public ParseFileHandler(ParserGeneratorModel m, TreeFilesModel treeModel, ImageWindow w) {
        super(m, treeModel);
        this.w = w;
    }

    @Override
    public void handle(ActionEvent event) {
        if(selected_item.get() == null || !(selected_item.get().getValue() instanceof RegularFileEntryItem)){
            System.out.println("Cannot parse. Specify regular text file!");
            return;
        }
        String fullName = selected_item.get().getValue().getFullFileName();
        System.out.println("Init parsing of file: " + fullName);
        if(this.model.getCurLexer() == null || this.model.getCurParser() == null){
            System.out.println("Cannot parse. First, create Parser with Lexer by selecting \"Make full Parser+Lexer\" option!");
            return;
        }
        TreeItem<FileEntryItem> pNode = selected_item.get().getParent();
        String pdir = pNode.getValue() == null ? "" : pNode.getValue().getFullFileName() + Main.PATH_SEPARATOR;

        LinkedTree<LanguageSymbol> tree = this.model.getCurParser().parse(fullName);
        System.out.println("Parsing finished.");
        try {
            File f = Graphviz.fromString(tree.toDot("ptree")).render(Format.PNG).toFile(new File(pdir+"Tree_SLR"));
            w.setImage(f);
        } catch (IOException e) {
            System.out.println("Cannot show Parsing Tree. File is too big or source input was not passed by parser.");
        }
    }
}