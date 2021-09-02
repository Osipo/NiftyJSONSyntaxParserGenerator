package bmstu.iu7m.osipov.ui.controllers;


import bmstu.iu7m.osipov.configurations.ControllerBeanNames;
import bmstu.iu7m.osipov.configurations.FileDialogText;
import bmstu.iu7m.osipov.configurations.UIComponentIds;
import bmstu.iu7m.osipov.ui.controllers.handlers.*;
import bmstu.iu7m.osipov.ui.locale.LanguageName;
import bmstu.iu7m.osipov.ui.modals.CreateFileDialog;
import bmstu.iu7m.osipov.ui.modals.ImageWindow;
import bmstu.iu7m.osipov.ui.models.ParserGeneratorModel;
import bmstu.iu7m.osipov.ui.models.entities.TitledUIComponent;
import bmstu.iu7m.osipov.ui.models.entities.UIMenuItemComponent;
import bmstu.iu7m.osipov.ui.models.entities.UITextComponent;
import bmstu.iu7m.osipov.ui.models.entities.UITitledComponent;
import bmstu.iu7m.osipov.ui.models.stores.EventHandlersStore;
import bmstu.iu7m.osipov.ui.views.RootWindowView;
import com.codepoetics.protonpack.maps.MapStream;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import javax.annotation.PostConstruct;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class RootWindowController extends RootWindowView {

    private final ObjectProperty<LanguageName> selectedLang;

    @Autowired
    private ApplicationContext appContext;

    @Autowired
    private EventHandlersStore hdlrs;

    @FXML
    private TreeFilesController tree_ctrl;
    @FXML
    private EditorFilesController editor_ctrl;
    @FXML
    private RightMenuController right_ctrl;

    public RootWindowController(){
        System.out.println("RootWindowController: Constructor");
        selectedLang = new SimpleObjectProperty<>(this, "selectedLanguage", LanguageName.ENG);
    }

    /* This method starts after @PostConstruct init() method */
    /* Called from Main start(Stage pStage) method */
    /* Initialize all Window dialogs. */
    public void initDialogs(Stage s){
        System.out.println("Init all dialog windows.");

        //----------------------------------
        // init handlers for Open and Close files.
        //----------------------------------

        //set openDialog handler
        UpdateTreeViewAndOpenFileDialogHandler ophdlr = new UpdateTreeViewAndOpenFileDialogHandler(editor_ctrl.getModel(), tree_ctrl.getModel());
        ophdlr.selectedItemProperty().bind(tree_ctrl.getModel().selectedItemProperty());
        ophdlr.selectedLanguageProperty().bind(this.selectedLang);

        OpenFileHandler tree_ophdlr = new OpenFileHandler(editor_ctrl.getModel());
        tree_ophdlr.selectedItemProperty().bind(tree_ctrl.getModel().selectedItemProperty());

        //set closeFile handler
        CloseFileHandler clshdlr = new CloseFileHandler(editor_ctrl.getModel());
        clshdlr.selectedItemProperty().bind(tree_ctrl.getModel().selectedItemProperty());

        CloseEditorFileHandler editor_clshdlr = new CloseEditorFileHandler(editor_ctrl.getModel());

        // Init Save and Create handlers.
        SaveFileHandler svhdlr = new SaveFileHandler(editor_ctrl.getModel());

        //Create file modal window dialog.
        CreateFileDialog crFileDlg = new CreateFileDialog(this.uiStore);

        //set createFile handler
        CreateFileHandler tree_crthdlr_f = new CreateFileHandler(editor_ctrl.getModel(), tree_ctrl.getModel(), crFileDlg);
        tree_crthdlr_f.selectedItemProperty().bind(tree_ctrl.getModel().selectedItemProperty());

        CreateFileHandler tree_crthdlr_dir = new CreateFileHandler(editor_ctrl.getModel(), tree_ctrl.getModel(), true, crFileDlg);
        tree_crthdlr_dir.selectedItemProperty().bind(tree_ctrl.getModel().selectedItemProperty());

        //------------------------------------
        // Parser Generator's handlers
        //------------------------------------
        ParserGeneratorModel genModel = new ParserGeneratorModel();
        ImageWindow ptree_win = new ImageWindow(this.uiStore, UIComponentIds.ShowParingTreeTitle);

        CreateLexerHandler tree_lexer_hdlr = new CreateLexerHandler(genModel, tree_ctrl.getModel());
        CreateParserHandler tree_parser_hdlr = new CreateParserHandler(genModel, tree_ctrl.getModel());
        CreateCommonParserHandler tree_cparser_hdlr = new CreateCommonParserHandler(genModel, tree_ctrl.getModel());
        ParseFileHandler tree_parse_hdlr = new ParseFileHandler(genModel, tree_ctrl.getModel(), ptree_win);
        tree_lexer_hdlr.selectedItemProperty().bind(tree_ctrl.getModel().selectedItemProperty());
        tree_parser_hdlr.selectedItemProperty().bind(tree_ctrl.getModel().selectedItemProperty());
        tree_parse_hdlr.selectedItemProperty().bind(tree_ctrl.getModel().selectedItemProperty());
        tree_cparser_hdlr.selectedItemProperty().bind(tree_ctrl.getModel().selectedItemProperty());



        // Right controller handlers
        ImageWindow lexer_show_win = new ImageWindow(this.uiStore, UIComponentIds.ShowLexerTitle);
        ImageWindow parser_show_win = new ImageWindow(this.uiStore, UIComponentIds.ShowParserTitle);

        ShowLexerHandler right_ctrl_sh_lexer = new ShowLexerHandler(genModel, lexer_show_win);
        ShowParserHandler right_ctrl_sh_parser = new ShowParserHandler(genModel, parser_show_win);

        //Add to right controller btns
        right_ctrl_sh_lexer.attachTo(ActionEvent.ACTION, uiStore.getComponents().get(UIComponentIds.ShowLexerAutomaton));
        right_ctrl_sh_parser.attachTo(ActionEvent.ACTION, uiStore.getComponents().get(UIComponentIds.ShowParserAutomaton));

        //right_ctrl.getShowLexerButton().addEventHandler(ActionEvent.ACTION, right_ctrl_sh_lexer);
        //right_ctrl.getShowParserButton().addEventHandler(ActionEvent.ACTION, right_ctrl_sh_parser);



        // add handlers and save them to the HandlersStore
        //m_file_open.addEventHandler(ActionEvent.ACTION, ophdlr);
        ophdlr.attachTo(ActionEvent.ACTION, uiStore.getComponents().get(UIComponentIds.FileOpenMenu));

        //open and close for editor.
        //TODO: Replace component.addEventHandler with eventHandler.attachTo(etype, component)

        editor_ctrl.getModel().getView().getCloseButton().addEventHandler(ActionEvent.ACTION, editor_clshdlr);
        editor_ctrl.getModel().getView().getSaveButton().addEventHandler(ActionEvent.ACTION, svhdlr);

        this.m_file_close.addEventHandler(ActionEvent.ACTION, clshdlr);
        this.m_file_new_tfile.addEventHandler(ActionEvent.ACTION, tree_crthdlr_f);
        this.m_file_new_dir.addEventHandler(ActionEvent.ACTION, tree_crthdlr_dir);

        this.hdlrs.getHandlers().put("openFileAndUpdateView", ophdlr);
        this.hdlrs.getHandlers().put("openFile", tree_ophdlr);
        this.hdlrs.getHandlers().put("saveFile", svhdlr);
        this.hdlrs.getHandlers().put("closeFile", clshdlr);
        this.hdlrs.getHandlers().put("closeEditorFile", editor_clshdlr);
        this.hdlrs.getHandlers().put("createFile", tree_crthdlr_f);
        this.hdlrs.getHandlers().put("createDir", tree_crthdlr_dir);
        this.hdlrs.getHandlers().put("genLexer", tree_lexer_hdlr);
        this.hdlrs.getHandlers().put("genParser", tree_parser_hdlr);
        this.hdlrs.getHandlers().put("genCommonParser", tree_cparser_hdlr);
        this.hdlrs.getHandlers().put("parseFile", tree_parse_hdlr);
        this.hdlrs.getHandlers().put("showLexer", right_ctrl_sh_lexer);
        this.hdlrs.getHandlers().put("showLRParser", right_ctrl_sh_parser);

        this.tree_ctrl.getCallBackFunction().loadContextMenuForCells();

        //Set default language to English forcefully by switching it to Russian
        // AND then switching it again to English with fire 'click' event on m_prefs_lang_eng MenuItem.
        selectedLang.set(LanguageName.RU);// choose another language to pass handler.
        m_prefs_lang_eng.fire(); //and fire English_item click.


    }

    /* All JavaFX Components are loaded but beans are not wired yet.*/
    @FXML
    public void initialize() {
        System.out.println("RootWindowController: FXML Loaded.");
        super.initView();
    }

    /* All beans are wired. DI completed. */
    @PostConstruct
    public void init(){
        System.out.println("Post Construct of RootWindowController bean");
        super.saveUIComponents();
        m_prefs_lang_eng.setOnAction(event -> {
            if(selectedLang.get() == LanguageName.ENG)
                return;

            MapStream.of(uiStore.getComponents()).filter(x -> x.getValue() instanceof UITextComponent)
                    .map(x -> ((UITextComponent)x.getValue()).getTextNode()).forEach(x -> x.setText(uiStore.toEnglish().get(x.getId())));

            MapStream.of(uiStore.getComponents()).filter(x -> x.getValue() instanceof UIMenuItemComponent)
                    .map(x -> ((UIMenuItemComponent)x.getValue()).getItem()).forEach(x -> x.setText(uiStore.toEnglish().get(x.getId())));
            MapStream.of(uiStore.getComponents()).filter(x -> x.getValue() instanceof UITitledComponent)
                    .map(x -> ((UITitledComponent) x.getValue()).getTitled()).forEach(x -> x.setTitle(uiStore.toEnglish().get(x.getId())));

            selectedLang.set(LanguageName.ENG);
            initFileDialogLanguage(uiStore.toEnglish());
        });

        m_prefs_lang_rus.setOnAction(event -> {
            if(selectedLang.get() == LanguageName.RU)
                return;
            MapStream.of(uiStore.getComponents()).filter(x -> x.getValue() instanceof UITextComponent)
                    .map(x -> ((UITextComponent)x.getValue()).getTextNode()).forEach(x -> x.setText(uiStore.toRussian().get(x.getId())));

            MapStream.of(uiStore.getComponents()).filter(x -> x.getValue() instanceof UIMenuItemComponent)
                    .map(x -> ((UIMenuItemComponent)x.getValue()).getItem()).forEach(x -> x.setText(uiStore.toRussian().get(x.getId())));
            MapStream.of(uiStore.getComponents()).filter(x -> x.getValue() instanceof UITitledComponent)
                    .map(x -> ((UITitledComponent) x.getValue()).getTitled()).forEach(x -> x.setTitle(uiStore.toRussian().get(x.getId())));

            selectedLang.set(LanguageName.RU);
            initFileDialogLanguage(uiStore.toRussian());
        });

        Node tab = (Node) appContext.getBean(ControllerBeanNames.TAB_CONSOLE_CTRL);
        Node otab = (Node) appContext.getBean(ControllerBeanNames.TAB_OUTPUT_CTRL);
        bottom_term.addEventHandler(ActionEvent.ACTION, new BottomTabsSelectionHandler(bottom, tab));
        bottom_out.addEventHandler(ActionEvent.ACTION, new BottomTabsSelectionHandler(bottom, otab));
    }

    private void initFileDialogLanguage(Map<String, String> tran){
        SwingUtilities.invokeLater(() -> {
//            System.out.println(UIManager.get(FileDialogText.detailsAccessText));
//            System.out.println(UIManager.get(FileDialogText.homeFolderAccessText));
//            System.out.println(UIManager.get(FileDialogText.upFolderAccessText));
//            System.out.println(UIManager.get(FileDialogText.listViewAccessText));
//            System.out.println(UIManager.get(FileDialogText.newFolderAccessText));

            UIManager.put(FileDialogText.lookInText, tran.get(FileDialogText.lookInText));
            UIManager.put(FileDialogText.fileNameText, tran.get(FileDialogText.fileNameText));
            UIManager.put(FileDialogText.fileTypeText, tran.get(FileDialogText.fileTypeText));
            UIManager.put(FileDialogText.upFolderTooltipText, tran.get(FileDialogText.upFolderTooltipText));
            UIManager.put(FileDialogText.homeFolderTooltipText, tran.get(FileDialogText.homeFolderTooltipText));
            UIManager.put(FileDialogText.listViewTooltipText, tran.get(FileDialogText.listViewTooltipText));
            UIManager.put(FileDialogText.detailsTooltipText, tran.get(FileDialogText.detailsTooltipText));
            UIManager.put(FileDialogText.newFolderTooltipText, tran.get(FileDialogText.newFolderTooltipText));
            UIManager.put(FileDialogText.openTitleText, tran.get(FileDialogText.openTitleText));
            UIManager.put(FileDialogText.cancelText, tran.get(FileDialogText.cancelText));
            UIManager.put(FileDialogText.openText, tran.get(FileDialogText.openText));
            UIManager.put(FileDialogText.cancelTooltipText, tran.get(FileDialogText.cancelTooltipText));
            UIManager.put(FileDialogText.allFilterText, tran.get(FileDialogText.allFilterText));
            UIManager.put(FileDialogText.jsonFilterText, tran.get(FileDialogText.jsonFilterText));

            UIManager.put(FileDialogText.fileNameHeaderText, tran.get(FileDialogText.fileNameHeaderText));
            UIManager.put(FileDialogText.fileTypeHeaderText, tran.get(FileDialogText.fileTypeHeaderText));
            UIManager.put(FileDialogText.fileSizeHeaderText, tran.get(FileDialogText.fileSizeHeaderText));
            UIManager.put(FileDialogText.fileDateHeaderText, tran.get(FileDialogText.fileDateHeaderText));
        });
    }
}
