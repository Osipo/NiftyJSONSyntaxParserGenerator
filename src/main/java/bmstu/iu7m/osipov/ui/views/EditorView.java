package bmstu.iu7m.osipov.ui.views;

import bmstu.iu7m.osipov.ui.models.entities.UIComponent;
import bmstu.iu7m.osipov.ui.models.stores.UIComponentStore;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.web.HTMLEditor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.CountDownLatch;

public class EditorView extends VBox {

    @FXML
    private VBox editor_menu;
    @FXML
    private HBox editor_menu_btns;
    @FXML
    protected Button save_btn;
    @FXML
    protected Button close_btn;
//    @FXML
//    protected TextArea editor;

    @FXML
    protected SwingNode swi_editor_wrapper;

    protected JTextPane editor;

    @Autowired
    protected UIComponentStore uiStore;

    protected CountDownLatch swi_awaiter;

    public EditorView(){
        System.out.println("EditorView: constructor");
        this.swi_awaiter = new CountDownLatch(1);
    }

    protected void initView(){
        System.out.println("Parent EditorView > initView()");
        editor_menu.prefWidthProperty().bind(this.prefWidthProperty());
        BackgroundFill bF = new BackgroundFill(Color.GREEN, new CornerRadii(1), null);
        editor_menu.setBackground(new Background(bF));
        swi_editor_wrapper.setTranslateY(editor_menu.getHeight());
        //editor.setTranslateY(editor_menu.getHeight());



        SwingUtilities.invokeLater(() ->{
            JTextPane editor = new JTextPane();
            JScrollPane escroll = new JScrollPane(editor);
            escroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            escroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            escroll.setPreferredSize(new Dimension((int)this.getPrefWidth(), 500));
            swi_editor_wrapper.setContent(escroll);
            this.editor = editor;
            System.out.println("JTextPane added");
            swi_awaiter.countDown();//signal that JTextPane is very.
        });
    }

    protected void saveUIComponents(){
        this.uiStore.getComponents().put("editor_menu", new UIComponent(editor_menu));
        //this.uiStore.getComponents().put("editor", new UIComponent(editor));
        this.uiStore.getComponents().put("swi_editor_wrapper", new UIComponent(swi_editor_wrapper));
        this.uiStore.getComponents().put("editor_menu_btns", new UIComponent(editor_menu_btns));
        this.uiStore.getComponents().put("save_btn", new UIComponent(save_btn));
        this.uiStore.getComponents().put("close_btn", new UIComponent(close_btn));
    }
}
