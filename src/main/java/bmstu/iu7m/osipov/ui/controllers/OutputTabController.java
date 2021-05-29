package bmstu.iu7m.osipov.ui.controllers;

import bmstu.iu7m.osipov.ui.views.OutputTabView;
import javafx.fxml.FXML;

import javax.annotation.PostConstruct;

public class OutputTabController extends OutputTabView {
    public OutputTabController(){
        System.out.println("OutputTabController: constructor");
    }

    @FXML
    public void initialize(){
        System.out.println("OutputTabController: FXML Loaded.");
        super.initView();
    }

    @PostConstruct
    public void init(){
        System.out.println("Post construct of bean OutputTabController");
        super.saveUIComponents();
    }
}
