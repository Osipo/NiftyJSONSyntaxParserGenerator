package bmstu.iu7m.osipov.ui.controllers;

import bmstu.iu7m.osipov.ui.controllers.handlers.CommandShellHandler;
import bmstu.iu7m.osipov.ui.controllers.handlers.TerminalKeyInputHandler;
import bmstu.iu7m.osipov.ui.models.TerminalModel;
import bmstu.iu7m.osipov.ui.models.stores.EventHandlersStore;
import bmstu.iu7m.osipov.ui.views.ConsoleTabView;
import javafx.fxml.FXML;
import javafx.scene.input.KeyEvent;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;

public class ConsoleTabController extends ConsoleTabView {

    @Autowired
    protected EventHandlersStore hdlrs;

    protected TerminalModel model;

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
        this.model = new TerminalModel();
        this.model.setView(this);
        this.console_text.appendText(this.model.getCWD());

        TerminalKeyInputHandler in_hdlr = new TerminalKeyInputHandler(this.model);
        TerminalKeyInputHandler ctrl_hdlr = new TerminalKeyInputHandler(this.model);

        in_hdlr.attachTo(KeyEvent.KEY_TYPED, uiStore.getComponents().get("console_text"));
        ctrl_hdlr.attachTo(KeyEvent.KEY_PRESSED, uiStore.getComponents().get("console_text"));

        this.hdlrs.getHandlers().put("termInput", in_hdlr);
        this.hdlrs.getHandlers().put("termCtrl", ctrl_hdlr);
    }
}