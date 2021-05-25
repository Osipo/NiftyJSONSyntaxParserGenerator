package bmstu.iu7m.osipov.ui.controllers;


import bmstu.iu7m.osipov.services.files.FileLocatorService;
import bmstu.iu7m.osipov.services.langs.LanguageName;
import bmstu.iu7m.osipov.ui.models.entities.UIComponent;
import bmstu.iu7m.osipov.ui.models.entities.UIMenuItemComponent;
import bmstu.iu7m.osipov.ui.models.entities.UITextComponent;
import bmstu.iu7m.osipov.ui.views.RootWindowView;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Iterator;

public class RootWindowController extends RootWindowView {

    private LanguageName selectedLang;
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
                    .map(x -> ((UITextComponent)x).getTextNode()).forEach(x -> x.setText(uiStore.russianToEnglish().get(x.getText())));

            uiStore.getComponents().stream().filter(x -> x instanceof UIMenuItemComponent)
                    .map(x -> ((UIMenuItemComponent)x).getItem()).forEach(x -> x.setText(uiStore.russianToEnglish().get(x.getText())));

            selectedLang = LanguageName.ENG;
            /*
            Iterator<UIComponent> elems = uiStore.getComponents().iterator();
            UIComponent elem = null;
            String k = null;
            while(elems.hasNext()){
                elem = elems.next();
                if(elem instanceof UITextComponent){
                    k = ((UITextComponent) elem).getTextNode().getText();
                    ((UITextComponent) elem).getTextNode().setText(uiStore.englishToRussian().get(k));
                }
                else if(elem instanceof UIMenuItemComponent){

                }
            }*/
        });
        m_prefs_lang_rus.setOnAction(event -> {
            if(selectedLang == LanguageName.RU)
                return;
            uiStore.getComponents().stream().filter(x -> x instanceof UITextComponent)
                    .map(x -> ((UITextComponent)x).getTextNode()).forEach(x -> x.setText(uiStore.englishToRussian().get(x.getText())));

            uiStore.getComponents().stream().filter(x -> x instanceof UIMenuItemComponent)
                    .map(x -> ((UIMenuItemComponent)x).getItem()).forEach(x -> x.setText(uiStore.englishToRussian().get(x.getText())));

            selectedLang = LanguageName.RU;
            /*
            Iterator<UIComponent> elems = uiStore.getComponents().iterator();
            UIComponent elem = null;
            while(elems.hasNext()){
                elem = elems.next();
                if(elem instanceof UITextComponent){

                }
                else if(elem instanceof UIMenuItemComponent){

                }
            }*/
        });
        //fMenu.prefWidthProperty().bind(top.widthProperty());
    }
}
