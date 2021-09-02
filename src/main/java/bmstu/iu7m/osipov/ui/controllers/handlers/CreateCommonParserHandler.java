package bmstu.iu7m.osipov.ui.controllers.handlers;

import bmstu.iu7m.osipov.Main;
import bmstu.iu7m.osipov.services.grammars.Grammar;
import bmstu.iu7m.osipov.services.grammars.JSONDCollectionGrammarBuilder;
import bmstu.iu7m.osipov.services.lexers.DFALexer;
import bmstu.iu7m.osipov.services.lexers.FALexerGenerator;
import bmstu.iu7m.osipov.services.parsers.LRAlgorithm;
import bmstu.iu7m.osipov.services.parsers.LRParser;
import bmstu.iu7m.osipov.services.parsers.Parser;
import bmstu.iu7m.osipov.services.parsers.json.elements.JsonObject;
import bmstu.iu7m.osipov.services.parsers.json.meta.JsonDocumentDescriptor;
import bmstu.iu7m.osipov.structures.automats.DFA;
import bmstu.iu7m.osipov.ui.models.ParserGeneratorModel;
import bmstu.iu7m.osipov.ui.models.TreeFilesModel;
import bmstu.iu7m.osipov.ui.models.entities.DirectoryEntryItem;
import bmstu.iu7m.osipov.ui.models.entities.FileEntryItem;
import bmstu.iu7m.osipov.ui.models.entities.RegularFileEntryItem;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;

import java.io.IOException;

public class CreateCommonParserHandler extends ParserGeneratorHandlers<ActionEvent> implements ObserverEventHandler<ActionEvent> {

    private JSONDCollectionGrammarBuilder g_builder;

    public CreateCommonParserHandler(ParserGeneratorModel m, TreeFilesModel treeModel) {
        super(m, treeModel);
        this.g_builder = new JSONDCollectionGrammarBuilder();
    }

    @Override
    public void handle(ActionEvent event) {
        if(selected_item.get() == null || !(selected_item.get().getValue() instanceof DirectoryEntryItem)){
            System.out.println("Cannot create Common Parser. You must specify directory with collections of JSON-documents");
            return;
        }

        TreeItem<FileEntryItem> pNode = selected_item.get();
        String pdir = pNode.getValue() == null ? "" : pNode.getValue().getFullFileName() + Main.PATH_SEPARATOR;


        System.out.println("Init Common Parser generating for collection at: " + pdir);
        FileEntryItem item = null;
        JsonObject ob_i = null;
        JsonDocumentDescriptor descriptor = new JsonDocumentDescriptor();
        for(TreeItem<FileEntryItem> ch : selected_item.get().getChildren()){
            item = ch.getValue();

            //Skip directories.
            if(!(item instanceof RegularFileEntryItem))
                continue;
            //Skip non-json files.
            if(!item.getFileName().endsWith(".json"))
                continue;

            ob_i = json_translator.parse(item.getFullFileName());

            //Skip non-valid json files.
            if(ob_i == null)
                continue;
            JsonDocumentDescriptor d_i = new JsonDocumentDescriptor();
            d_i.describe2(ob_i);
            descriptor.merge(d_i);
        }
        Grammar G = g_builder.getCommonGrammar(descriptor);
        fromGrammar(G, pdir);
    }

    private void fromGrammar(Grammar G, String pdir){
        System.out.println("Built grammar: ");
        System.out.println(G);
        FALexerGenerator lgen = new FALexerGenerator();
        DFALexer lexer = new DFALexer(new DFA(lgen.buildNFA(G)));
        try {
            lexer.getImagefromStr(pdir, "Lexer");
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.model.setCurLexer(lexer);
        this.model.setGrammar(G);
        Parser parser = null;
        if(this.model.getCurLexer() != null && this.model.getGrammar() != null)
            parser = new LRParser(this.model.getGrammar(), this.model.getCurLexer(), LRAlgorithm.SLR);
        this.model.setCurParser(parser);
    }
}
