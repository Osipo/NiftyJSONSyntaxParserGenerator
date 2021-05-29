package bmstu.iu7m.osipov.ui.controllers;


import bmstu.iu7m.osipov.configurations.ControllerBeanNames;
import bmstu.iu7m.osipov.services.langs.LanguageName;
import bmstu.iu7m.osipov.ui.controllers.handlers.ShowAndHideEventHandler;
import bmstu.iu7m.osipov.ui.models.entities.UIMenuItemComponent;
import bmstu.iu7m.osipov.ui.models.entities.UITextComponent;
import bmstu.iu7m.osipov.ui.views.RootWindowView;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;

public class RootWindowController extends RootWindowView {

    private LanguageName selectedLang;

    @Autowired
    private ApplicationContext appContext;

    public RootWindowController(){
        System.out.println("RootWindowController: Constructor");
        selectedLang = LanguageName.ENG;
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

            uiStore.getComponents().stream().filter(x -> x instanceof UITextComponent)
                    .map(x -> ((UITextComponent)x).getTextNode()).forEach(x -> x.setText(uiStore.toEnglish().get(x.getId())));

            uiStore.getComponents().stream().filter(x -> x instanceof UIMenuItemComponent)
                    .map(x -> ((UIMenuItemComponent)x).getItem()).forEach(x -> x.setText(uiStore.toEnglish().get(x.getId())));

            selectedLang = LanguageName.ENG;

        });

        m_prefs_lang_rus.setOnAction(event -> {
            if(selectedLang == LanguageName.RU)
                return;
            uiStore.getComponents().stream().filter(x -> x instanceof UITextComponent)
                    .map(x -> ((UITextComponent)x).getTextNode()).forEach(x -> x.setText(uiStore.toRussian().get(x.getId())));

            uiStore.getComponents().stream().filter(x -> x instanceof UIMenuItemComponent)
                    .map(x -> ((UIMenuItemComponent)x).getItem()).forEach(x -> x.setText(uiStore.toRussian().get(x.getId())));

            selectedLang = LanguageName.RU;
        });

        Node tab = (Node) appContext.getBean(ControllerBeanNames.TAB_CONSOLE_CTRL);
        Node otab = (Node) appContext.getBean(ControllerBeanNames.TAB_OUTPUT_CTRL);
        bottom_term.addEventHandler(ActionEvent.ACTION, new ShowAndHideEventHandler(bottom, tab));
        bottom_out.addEventHandler(ActionEvent.ACTION, new ShowAndHideEventHandler(bottom, otab));
    }
}
