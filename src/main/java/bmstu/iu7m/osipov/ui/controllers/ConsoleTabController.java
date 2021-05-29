package bmstu.iu7m.osipov.ui.controllers;

import bmstu.iu7m.osipov.ui.controllers.handlers.CommandShellHandler;
import bmstu.iu7m.osipov.ui.views.ConsoleTabView;
import javafx.fxml.FXML;
import javafx.scene.input.KeyEvent;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;

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
        super.saveUIComponents();
        /*
        Process childShell = null;
        try{
            ProcessBuilder pb = new ProcessBuilder("cmd");
            pb.directory(new File(System.getProperty("user.dir")));
            childShell = pb.start();
            console_text.addEventHandler(KeyEvent.KEY_TYPED, new CommandShellHandler(childShell, console_text));
        }
        catch (IOException e){
            System.out.println("Cannot create process for command shell.");
        }
         */
    }
}