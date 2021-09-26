package bmstu.iu7m.osipov.ui.controllers;

import bmstu.iu7m.osipov.ui.controllers.handlers.ShowMessageHandler;
import bmstu.iu7m.osipov.ui.views.RightMenuView;
import bmstu.iu7m.osipov.ui.wins.MessageWindow;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;

import javax.annotation.PostConstruct;

public class RightMenuController extends RightMenuView {
    public RightMenuController(){
        System.out.println("RightMenuController constructor call");
    }

    @FXML
    public void initialize() {
        System.out.println("RightMenuController: FXML Loaded.");
        super.initView();
    }

    @PostConstruct
    public void init(){
        System.out.println("Post construct of RightMenuController bean.");
        super.saveUIComponents();
    }

    public void test_window(){
        MessageWindow w = new MessageWindow(this.uiStore); //when creating stages u must in
        ShowMessageHandler<ActionEvent> sh_win = new ShowMessageHandler<>(w);
        sh_win.attachTo(ActionEvent.ACTION, this.uiStore.getComponents().get("test_btn"));
    }
}
