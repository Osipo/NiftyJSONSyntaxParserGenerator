package bmstu.iu7m.osipov.ui.controllers;


import bmstu.iu7m.osipov.configurations.ControllerBeanNames;
import bmstu.iu7m.osipov.configurations.FileDialogText;
import bmstu.iu7m.osipov.ui.controllers.handlers.OpenFileHandler;
import bmstu.iu7m.osipov.ui.locale.LanguageName;
import bmstu.iu7m.osipov.ui.controllers.handlers.BottomTabsSelectionHandler;
import bmstu.iu7m.osipov.ui.controllers.handlers.OpenFileDialogHandler;
import bmstu.iu7m.osipov.ui.controllers.handlers.UpdateTreeViewAndOpenFileDialogHandler;
import bmstu.iu7m.osipov.ui.models.entities.UIMenuItemComponent;
import bmstu.iu7m.osipov.ui.models.entities.UITextComponent;
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

    public RootWindowController(){
        System.out.println("RootWindowController: Constructor");
        selectedLang = new SimpleObjectProperty<>(this, "selectedLanguage", LanguageName.ENG);
    }

    /* This method starts after @PostConstruct init() method */
    /* Called from Main(). */
    /* Initialize all Window dialogs. */
    public void initDialogs(Stage s){
        System.out.println("Init all dialog windows.");

        //---------------------------------
        // init language (locale) for JFileChooser dialog
        //---------------------------------
        initFileDialogLanguage(uiStore.toEnglish());

        //----------------------------------
        // init handlers for Open and Close files.
        //----------------------------------

        //set openDialog handler
        UpdateTreeViewAndOpenFileDialogHandler ophdlr = new UpdateTreeViewAndOpenFileDialogHandler(s, editor_ctrl.getModel(), tree_ctrl.getModel());
        ophdlr.selectedItemProperty().bind(tree_ctrl.getModel().selectedItemProperty());
        ophdlr.selectedLanguageProperty().bind(this.selectedLang);

        OpenFileHandler tree_ophdlr = new OpenFileHandler(editor_ctrl.getModel());
        tree_ophdlr.selectedItemProperty().bind(tree_ctrl.getModel().selectedItemProperty());
        tree_ophdlr.selectedLanguageProperty().bind(this.selectedLang);

        this.hdlrs.getHandlers().put("openFileAndUpdateView", ophdlr);
        this.hdlrs.getHandlers().put("openFile", tree_ophdlr);
        m_file_open.addEventHandler(ActionEvent.ACTION, ophdlr);
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
//            UIDefaults defs = UIManager.getDefaults();
//            ArrayList keys = Collections.list(defs.keys());
//            for(Object k : keys){
//                if(defs.getString(k) != null && k instanceof String ){
//                    System.out.println(k + " - " + defs.getString(k));
//                }
//            }
            System.out.println(UIManager.get(FileDialogText.detailsAccessText));
            System.out.println(UIManager.get(FileDialogText.homeFolderAccessText));
            System.out.println(UIManager.get(FileDialogText.upFolderAccessText));
            System.out.println(UIManager.get(FileDialogText.listViewAccessText));
            System.out.println(UIManager.get(FileDialogText.newFolderAccessText));

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
