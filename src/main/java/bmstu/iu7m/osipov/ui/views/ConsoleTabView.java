package bmstu.iu7m.osipov.ui.views;

import bmstu.iu7m.osipov.ui.models.entities.UIComponent;
import bmstu.iu7m.osipov.ui.models.stores.UIComponentStore;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextFlow;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class ConsoleTabView extends VBox {

    @FXML
    protected TextArea console_text;

    @Autowired
    protected UIComponentStore uiStore;

    public ConsoleTabView(){
        System.out.println("ConsoleTabView: constructor");
    }

    protected void initView(){
        System.out.println("ConsoleTabView > initView()");
        BackgroundFill bF = new BackgroundFill(Color.BLUE, new CornerRadii(1), null);
        this.setBackground(new Background(bF));
        //this.console_text.prefHeightProperty().bind(this.prefHeightProperty());
        //this.console_text.prefWidthProperty().bind(this.prefWidthProperty());
        //this.console_text.setEditable(false);
        this.console_text.setVisible(false);
    }

    protected void saveUIComponents() {
        uiStore.getComponents().add(new UIComponent(console_text));
    }
}
