package bmstu.iu7m.osipov.ui.views;

import bmstu.iu7m.osipov.ui.models.entities.UIComponent;
import bmstu.iu7m.osipov.ui.models.stores.UIComponentStore;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.web.HTMLEditor;
import org.springframework.beans.factory.annotation.Autowired;

public class EditorView extends VBox {

    @FXML
    private VBox editor_menu;
    @FXML
    private HBox editor_menu_btns;
    @FXML
    protected Button save_btn;
    @FXML
    protected Button close_btn;
    @FXML
    protected TextArea editor;

    @Autowired
    protected UIComponentStore uiStore;

    public EditorView(){
        System.out.println("EditorView: constructor");
    }

    protected void initView(){
        System.out.println("Parent EditorView > initView()");
        editor_menu.prefWidthProperty().bind(this.prefWidthProperty());
        BackgroundFill bF = new BackgroundFill(Color.GREEN, new CornerRadii(1), null);
        editor_menu.setBackground(new Background(bF));
        editor.setTranslateY(editor.getHeight());
    }

    protected void saveUIComponents(){
        this.uiStore.getComponents().put("editor_menu", new UIComponent(editor_menu));
        this.uiStore.getComponents().put("editor", new UIComponent(editor));
        this.uiStore.getComponents().put("editor_menu_btns", new UIComponent(editor_menu_btns));
        this.uiStore.getComponents().put("save_btn", new UIComponent(save_btn));
        this.uiStore.getComponents().put("close_btn", new UIComponent(close_btn));
    }
}
