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
import org.springframework.beans.factory.annotation.Autowired;

public class OutputTabView extends VBox {
    @FXML
    protected TextArea output_text;

    @Autowired
    protected UIComponentStore uiStore;

    public OutputTabView(){
        System.out.println("OutputTabView: constructor");
    }

    protected void initView(){
        System.out.println("OutputTabView > initView()");
        //BackgroundFill bF = new BackgroundFill(Color.RED, new CornerRadii(1), null);
        //this.setBackground(new Background(bF));
        this.output_text.prefHeightProperty().bind(this.prefHeightProperty());
        this.output_text.prefWidthProperty().bind(this.prefWidthProperty());
        this.output_text.setEditable(false);
        //this.output_text.setVisible(false);
    }

    protected void saveUIComponents() {
        uiStore.getComponents().add(new UIComponent(output_text));
    }
}
