package bmstu.iu7m.osipov.ui.views;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.VBox;

public class RootWindowView {

    @FXML
    private Parent root;
    @FXML
    private VBox top;
    @FXML
    private MenuBar fMenu;

    //left panel
    @FXML
    private VBox left;

    public RootWindowView(){
        System.out.println("Parent RootWindowView");
    }

    public Parent getView(){
        return this.root;
    }

    public void initView(){
        System.out.println("Parent RootWindowView > initView()");
        if(root != null)
            System.out.println("Parent type is: "+root.getClass().getName());
    }
}
