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
import org.fife.ui.rsyntaxtextarea.*;
import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;
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

    @FXML
    protected SwingNode swi_editor_wrapper;

    protected RSyntaxTextArea editor;

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
            RSyntaxTextArea editor = new RSyntaxTextArea();
            editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JSON);
            SyntaxScheme scheme = editor.getSyntaxScheme();
            scheme.getStyle(Token.LITERAL_BOOLEAN).foreground = java.awt.Color.BLUE;
            scheme.getStyle(Token.LITERAL_STRING_DOUBLE_QUOTE).foreground = new java.awt.Color(42, 213, 93);
            scheme.getStyle(Token.LITERAL_NUMBER_DECIMAL_INT).foreground = new java.awt.Color(32, 181, 255);
            scheme.getStyle(Token.LITERAL_NUMBER_FLOAT).foreground = new java.awt.Color(32, 181, 255);
            scheme.getStyle(Token.NULL).foreground = java.awt.Color.BLUE;
            editor.setCodeFoldingEnabled(true);
            editor.setAntiAliasingEnabled(true);
            editor.setAutoscrolls(true);
            editor.revalidate();
            RTextScrollPane escroll = new RTextScrollPane(editor);
            escroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            escroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            escroll.setPreferredSize(new Dimension((int)this.getPrefWidth(), 500));
            swi_editor_wrapper.setContent(escroll);
            this.editor = editor;
            System.out.println("RSyntaxTextArea added");
            swi_awaiter.countDown();//signal that RSyntaxTextArea is very.
        });
    }

    protected void saveUIComponents(){
        this.uiStore.getComponents().put("editor_menu", new UIComponent(editor_menu));
        this.uiStore.getComponents().put("swi_editor_wrapper", new UIComponent(swi_editor_wrapper));
        this.uiStore.getComponents().put("editor_menu_btns", new UIComponent(editor_menu_btns));
        this.uiStore.getComponents().put("save_btn", new UIComponent(save_btn));
        this.uiStore.getComponents().put("close_btn", new UIComponent(close_btn));
    }

    //-----------------------------------
    //public EditorView components
    //-----------------------------------
    public RSyntaxTextArea getEditor(){
        return this.editor;
    }
}
