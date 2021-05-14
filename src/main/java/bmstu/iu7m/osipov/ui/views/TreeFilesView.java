package bmstu.iu7m.osipov.ui.views;

import bmstu.iu7m.osipov.ui.models.entities.FileEntryItem;
import bmstu.iu7m.osipov.ui.views.callbacks.TextFieldTreeCellCallback;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.springframework.beans.factory.annotation.Autowired;

public class TreeFilesView extends VBox {
    @FXML
    private HBox tfmenu;
    @FXML
    private HBox r1;
    @FXML
    private TextField searchInput;
    @FXML
    private Button search;

    @Autowired
    private TextFieldTreeCellCallback tc_callback;

    @FXML
    private TreeView<FileEntryItem> tree;

    public TreeFilesView(){
        System.out.println("Parent TreeFilesView");
    }

    /* JavaFX components are loaded. Layout them */
    protected void initView(){
        r1.prefWidthProperty().bind(tfmenu.widthProperty());
        r1.prefHeightProperty().bind(tfmenu.heightProperty().multiply(0.75));
        BackgroundFill bF = new BackgroundFill(Color.BLACK, new CornerRadii(1), null);
        r1.setBackground(new Background(bF));

        System.out.println("Parent TreeFilesView > initView()");
        tree.setTranslateY(tfmenu.getTranslateY() + tfmenu.getHeight());
    }

    protected void loadFiles(TreeItem<FileEntryItem> root){
        if(root == null){
            System.out.println("File entries not found.");
            return;
        }
        this.tree.setRoot(root);
        this.tree.setCellFactory(tc_callback);//creates TreeCells ONLY AFTER scene.show() method!
        this.tree.setEditable(true);
    }
}