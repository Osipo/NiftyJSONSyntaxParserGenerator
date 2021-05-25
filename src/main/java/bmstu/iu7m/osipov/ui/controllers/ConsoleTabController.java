package bmstu.iu7m.osipov.ui.controllers;

import bmstu.iu7m.osipov.ui.views.ConsoleTabView;
import javafx.fxml.FXML;

import javax.annotation.PostConstruct;

public class ConsoleTabController extends ConsoleTabView {
    public ConsoleTabController(){
        System.out.println("ConsoleTabController: constructor");
    }

    @FXML
    public void initialize(){
        System.out.println("ConsoleTabController: FXML Loaded.");
        super.initView();
    }

    @PostConstruct
    public void init(){
        System.out.println("Post construct of bean ConsoleTabController");
    }
}
