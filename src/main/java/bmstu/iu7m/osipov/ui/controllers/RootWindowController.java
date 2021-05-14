package bmstu.iu7m.osipov.ui.controllers;


import bmstu.iu7m.osipov.services.files.FileLocatorService;
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

public class RootWindowController extends RootWindowView {

    public RootWindowController(){
        System.out.println("RootWindowController: Constructor");
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
        //fMenu.prefWidthProperty().bind(top.widthProperty());
    }
}
