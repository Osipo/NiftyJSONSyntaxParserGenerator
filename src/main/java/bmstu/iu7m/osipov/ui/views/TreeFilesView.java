package bmstu.iu7m.osipov.ui.views;

import bmstu.iu7m.osipov.ui.models.entities.FileEntryItem;
import bmstu.iu7m.osipov.ui.models.entities.UITextComponent;
import bmstu.iu7m.osipov.ui.models.stores.UIComponentStore;
import bmstu.iu7m.osipov.ui.views.callbacks.TextFieldTreeCellCallback;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.springframework.beans.factory.annotation.Autowired;

public class TreeFilesView extends VBox {

    //root container
    @FXML
    private VBox tfmenu;

    //Section menu
    @FXML
    private HBox menu;
    @FXML
    protected TextField searchInput;
    @FXML
    protected Button search;

    //Section menu options
    @FXML
    private HBox tfmenu_bottom;
    @FXML
    private Label menu_options_label;
    @FXML
    private HBox menu_options;
    @FXML
    private RadioButton o_all;
    @FXML
    private RadioButton o_dirs;
    @FXML
    private RadioButton o_files;

    @Autowired
    private TextFieldTreeCellCallback tc_callback;

    @FXML
    protected TreeView<FileEntryItem> tree;

    //Group for radio buttons from menu options
    protected ToggleGroup o_group;

    @Autowired
    protected UIComponentStore uiStore;

    public TreeFilesView(){
        o_group = new ToggleGroup();
        System.out.println("Parent TreeFilesView");
    }

    /* JavaFX components are loaded. Layout them */
    protected void initView(){
        System.out.println("Parent TreeFilesView > initView()");

        menu.prefWidthProperty().bind(tfmenu.widthProperty());
        menu.prefHeightProperty().bind(tfmenu.heightProperty().multiply(0.5));
        BackgroundFill bF = new BackgroundFill(Color.BLACK, new CornerRadii(1), null);
        menu.setBackground(new Background(bF));
        menu.setTranslateY(tfmenu.getTranslateY());

        tfmenu_bottom.setTranslateY(tfmenu_bottom.getTranslateY() + menu.getHeight());
        tfmenu_bottom.prefWidthProperty().bind(tfmenu.prefWidthProperty());
        menu_options.translateYProperty().bind(tfmenu_bottom.translateYProperty());
        menu_options.prefWidthProperty().bind(tfmenu_bottom.prefWidthProperty());
        menu_options.prefHeightProperty().bind(tfmenu_bottom.prefHeightProperty());

        o_all.setToggleGroup(o_group);
        o_dirs.setToggleGroup(o_group);
        o_files.setToggleGroup(o_group);
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

    protected void saveUIComponents(){
        uiStore.getComponents().add(new UITextComponent(o_all));
        uiStore.getComponents().add(new UITextComponent(o_dirs));
        uiStore.getComponents().add(new UITextComponent(o_files));
        uiStore.getComponents().add(new UITextComponent(search));
        uiStore.getComponents().add(new UITextComponent(menu_options_label));
    }
}