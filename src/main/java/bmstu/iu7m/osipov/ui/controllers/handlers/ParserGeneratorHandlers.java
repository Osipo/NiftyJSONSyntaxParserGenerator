package bmstu.iu7m.osipov.ui.controllers.handlers;

import bmstu.iu7m.osipov.Main;
import bmstu.iu7m.osipov.exceptions.InvalidJsonGrammarException;
import bmstu.iu7m.osipov.services.grammars.Grammar;
import bmstu.iu7m.osipov.services.lexers.DFALexer;
import bmstu.iu7m.osipov.services.lexers.FALexerGenerator;
import bmstu.iu7m.osipov.services.lexers.RegexRPNParser;
import bmstu.iu7m.osipov.services.parsers.LRAlgorithm;
import bmstu.iu7m.osipov.services.parsers.LRParser;
import bmstu.iu7m.osipov.services.parsers.Parser;
import bmstu.iu7m.osipov.services.parsers.json.SimpleJsonParser;
import bmstu.iu7m.osipov.services.parsers.json.elements.JsonObject;
import bmstu.iu7m.osipov.structures.automats.DFA;
import bmstu.iu7m.osipov.ui.models.ParserGeneratorModel;
import bmstu.iu7m.osipov.ui.models.TreeFilesModel;
import bmstu.iu7m.osipov.ui.models.entities.FileEntryItem;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TreeItem;

import java.io.File;
import java.io.IOException;

public class ParserGeneratorHandlers {
    protected ParserGeneratorModel model;
    protected TreeFilesModel treeModel;
    protected final ObjectProperty<TreeItem<FileEntryItem>> selected_item;

    public ParserGeneratorHandlers(ParserGeneratorModel m, TreeFilesModel treeModel){
        this.model = m;
        this.treeModel = treeModel;
        this.selected_item = new SimpleObjectProperty<>(this, "selectedItem", null);
    }

    public final ObjectProperty<TreeItem<FileEntryItem>> selectedItemProperty(){
        return this.selected_item;
    }

    public final TreeItem<FileEntryItem> getSelectedItem(){
        return this.selected_item.get();
    }

    protected void initLexer(){
        File f = new File(selected_item.get().getValue().getFullFileName());
        System.out.println("Grammar file: "+f.getName());
        TreeItem<FileEntryItem> pNode = selected_item.get().getParent();
        String pdir = pNode.getValue() == null ? "" : pNode.getValue().getFullFileName() + Main.PATH_SEPARATOR;
        System.out.println("From directory: "+pdir);

        RegexRPNParser rpn = new RegexRPNParser();
        SimpleJsonParser parser = new SimpleJsonParser();
        JsonObject ob = parser.parse(f);
        if(ob == null){
            System.out.println("Cannot parse to JSON-tree elements.");
            return;
        }
        Grammar g = null;
        try{
            g = new Grammar(ob);
        } catch (InvalidJsonGrammarException ex){
            System.out.println(ex.getMessage());
            return;
        }
        System.out.println(g);
        FALexerGenerator lgen = new FALexerGenerator();
        DFALexer lexer = new DFALexer(new DFA(lgen.buildNFA(g)));
//        try {
//            lexer.getImagefromStr(pdir, "Lexer");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        this.model.setCurLexer(lexer);
        this.model.setGrammar(g);
    }
}
