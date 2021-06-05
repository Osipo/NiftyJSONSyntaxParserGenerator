package bmstu.iu7m.osipov.ui.controllers;


import bmstu.iu7m.osipov.configurations.ControllerBeanNames;
import bmstu.iu7m.osipov.services.LanguageName;
import bmstu.iu7m.osipov.ui.controllers.handlers.BottomTabsSelectionHandler;
import bmstu.iu7m.osipov.ui.controllers.handlers.OpenFileHandler;
import bmstu.iu7m.osipov.ui.controllers.handlers.UpdateTreeViewAndOpenFileHandler;
import bmstu.iu7m.osipov.ui.models.entities.UIMenuItemComponent;
import bmstu.iu7m.osipov.ui.models.entities.UITextComponent;
import bmstu.iu7m.osipov.ui.models.stores.EventHandlersStore;
import bmstu.iu7m.osipov.ui.views.RootWindowView;
import com.codepoetics.protonpack.maps.MapStream;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;

public class RootWindowController extends RootWindowView {

    private LanguageName selectedLang;

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
        selectedLang = LanguageName.ENG;
    }

    /* This method starts after @PostConstruct init() method */
    /* Called from Main(). */
    /* Initialize all Window dialogs. */
    public void initDialogs(Stage s){
        System.out.println("Init all dialog windows.");

        //set openDialog handler
        UpdateTreeViewAndOpenFileHandler ophdlr = new UpdateTreeViewAndOpenFileHandler(s, editor_ctrl.getModel(), tree_ctrl.getModel());
        ophdlr.selectedItemProperty().bind(tree_ctrl.getModel().selectedItemProperty());
        this.hdlrs.getHandlers().put("openFile", ophdlr);
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
            if(selectedLang == LanguageName.ENG)
                return;

            MapStream.of(uiStore.getComponents()).filter(x -> x.getValue() instanceof UITextComponent)
                    .map(x -> ((UITextComponent)x.getValue()).getTextNode()).forEach(x -> x.setText(uiStore.toEnglish().get(x.getId())));

            MapStream.of(uiStore.getComponents()).filter(x -> x.getValue() instanceof UIMenuItemComponent)
                    .map(x -> ((UIMenuItemComponent)x.getValue()).getItem()).forEach(x -> x.setText(uiStore.toEnglish().get(x.getId())));

            selectedLang = LanguageName.ENG;

        });

        m_prefs_lang_rus.setOnAction(event -> {
            if(selectedLang == LanguageName.RU)
                return;
            MapStream.of(uiStore.getComponents()).filter(x -> x.getValue() instanceof UITextComponent)
                    .map(x -> ((UITextComponent)x.getValue()).getTextNode()).forEach(x -> x.setText(uiStore.toRussian().get(x.getId())));

            MapStream.of(uiStore.getComponents()).filter(x -> x.getValue() instanceof UIMenuItemComponent)
                    .map(x -> ((UIMenuItemComponent)x.getValue()).getItem()).forEach(x -> x.setText(uiStore.toRussian().get(x.getId())));

            selectedLang = LanguageName.RU;
        });

        Node tab = (Node) appContext.getBean(ControllerBeanNames.TAB_CONSOLE_CTRL);
        Node otab = (Node) appContext.getBean(ControllerBeanNames.TAB_OUTPUT_CTRL);
        bottom_term.addEventHandler(ActionEvent.ACTION, new BottomTabsSelectionHandler(bottom, tab));
        bottom_out.addEventHandler(ActionEvent.ACTION, new BottomTabsSelectionHandler(bottom, otab));
    }
}
