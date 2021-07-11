package bmstu.iu7m.osipov.ui.controllers;

import bmstu.iu7m.osipov.ui.views.RightMenuView;
import javafx.fxml.FXML;

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


}
