package bmstu.iu7m.osipov.ui.views;

import bmstu.iu7m.osipov.configurations.UIComponentIds;
import bmstu.iu7m.osipov.ui.models.entities.UITextComponent;
import bmstu.iu7m.osipov.ui.models.stores.UIComponentStore;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;

public class RightMenuView extends VBox {

    @FXML
    private VBox r_btns;
    @FXML
    private Button sh_lexer;
    @FXML
    private Button sh_parser;

    @Autowired
    protected UIComponentStore uiStore;

    public RightMenuView(){
        System.out.println("RightMenuView constructor call");
    }

    protected void initView(){
        System.out.println("RightMenuView > initView()");
    }

    protected void saveUIComponents(){
        this.uiStore.getComponents().put(UIComponentIds.ShowLexerAutomaton, new UITextComponent(sh_lexer));
        this.uiStore.getComponents().put(UIComponentIds.ShowParserAutomaton, new UITextComponent(sh_parser));
    }

    //--------------------------------
    // Public RightMenuView components
    //--------------------------------

    public Button getShowLexerButton(){
        return this.sh_lexer;
    }

    public Button getShowParserButton(){
        return this.sh_parser;
    }
}
