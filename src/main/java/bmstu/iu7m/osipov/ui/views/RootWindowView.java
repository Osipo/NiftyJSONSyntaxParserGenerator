package bmstu.iu7m.osipov.ui.views;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class RootWindowView {

    @FXML
    private Parent root;
    @FXML
    private VBox top;

    //low panel section
    @FXML
    private HBox bottom;
    @FXML
    protected ToggleButton bottom_term;
    @FXML
    protected ToggleButton bottom_out;

    //group for all buttons at bottom
    private ToggleGroup b_group;

    //application menu (on the top panel)
    @FXML
    private MenuBar fMenu;
    @FXML
    protected MenuItem eng_lang;
    @FXML
    protected MenuItem rus_lang;

    //left panel
    @FXML
    private VBox left;

    public RootWindowView(){
        System.out.println("Parent RootWindowView constructor");
        this.b_group = new ToggleGroup();
    }

    public Parent getView(){
        return this.root;
    }

    public void initView(){
        System.out.println("Parent RootWindowView > initView()");
        if(root != null)
            System.out.println("Parent type is: "+root.getClass().getName());
        left.prefWidthProperty().bind(top.prefWidthProperty().divide(4));
        bottom.prefWidthProperty().bind(top.prefWidthProperty());
        bottom_term.setToggleGroup(b_group);
        bottom_out.setToggleGroup(b_group);
    }
}
