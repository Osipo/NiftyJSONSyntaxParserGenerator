package bmstu.iu7m.osipov.ui.views;

import bmstu.iu7m.osipov.configurations.UIComponentIds;
import bmstu.iu7m.osipov.ui.models.entities.UIComponent;
import bmstu.iu7m.osipov.ui.models.entities.UIMenuItemComponent;
import bmstu.iu7m.osipov.ui.models.entities.UITextComponent;
import bmstu.iu7m.osipov.ui.models.stores.UIComponentStore;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;

public class RootWindowView {

    @FXML
    private Parent root;
    @FXML
    private VBox top;

    //bottom panel section
    @FXML
    protected VBox bottom;
    @FXML
    protected HBox bottom_btns;
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
    private Menu m_file;
    @FXML
    private Menu m_file_new;
    @FXML
    protected MenuItem m_file_new_tfile;
    @FXML
    protected MenuItem m_file_open;
    @FXML
    protected MenuItem m_file_close;
    @FXML
    protected MenuItem m_file_exit;

    @FXML
    private Menu m_prefs;
    @FXML
    private Menu m_prefs_lang;
    @FXML
    protected MenuItem m_prefs_lang_eng;
    @FXML
    protected MenuItem m_prefs_lang_rus;
    @FXML
    private Menu m_help;
    @FXML
    protected MenuItem m_help_about;

    //left panel
    @FXML
    private VBox left;

    @Autowired
    protected UIComponentStore uiStore;

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

    protected void saveUIComponents(){

        uiStore.getComponents().put("root", new UIComponent(root));
        uiStore.getComponents().put("top", new UIComponent(top));
        uiStore.getComponents().put("fMenu", new UIComponent(fMenu));

        uiStore.getComponents().put(UIComponentIds.FileMenu, new UIMenuItemComponent(m_file));
        uiStore.getComponents().put(UIComponentIds.FileNewMenu, new UIMenuItemComponent(m_file_new));
        uiStore.getComponents().put(UIComponentIds.FileNewTextFileMenu, new UIMenuItemComponent(m_file_new_tfile));
        uiStore.getComponents().put(UIComponentIds.FileOpenMenu, new UIMenuItemComponent(m_file_open));
        uiStore.getComponents().put(UIComponentIds.FileCloseMenu, new UIMenuItemComponent(m_file_close));
        uiStore.getComponents().put(UIComponentIds.FileExitMenu, new UIMenuItemComponent(m_file_exit));
        uiStore.getComponents().put(UIComponentIds.PreferencesMenu, new UIMenuItemComponent(m_prefs));
        uiStore.getComponents().put(UIComponentIds.PreferencesLanguagesMenu, new UIMenuItemComponent(m_prefs_lang));
        uiStore.getComponents().put(UIComponentIds.PreferencesLanguagesEngMenu, new UIMenuItemComponent(m_prefs_lang_eng));
        uiStore.getComponents().put(UIComponentIds.PreferencesLanguagesRusMenu, new UIMenuItemComponent(m_prefs_lang_rus));
        uiStore.getComponents().put(UIComponentIds.HelpMenu, new UIMenuItemComponent(m_help));
        uiStore.getComponents().put(UIComponentIds.HelpAboutMenu, new UIMenuItemComponent(m_help_about));

        uiStore.getComponents().put("bottom", new UIComponent(bottom));
        uiStore.getComponents().put("bottom_btns", new UIComponent(bottom_btns));
        uiStore.getComponents().put(UIComponentIds.BottomTerminal, new UITextComponent(bottom_term));
        uiStore.getComponents().put(UIComponentIds.BottomOutput, new UITextComponent(bottom_out));

        //15 + 5
        System.out.println(uiStore.getComponents().size());
    }
}
