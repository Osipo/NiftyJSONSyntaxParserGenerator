package bmstu.iu7m.osipov.ui.views.callbacks;

import bmstu.iu7m.osipov.configurations.UIComponentIds;
import bmstu.iu7m.osipov.services.files.FileLocatorService;
import bmstu.iu7m.osipov.ui.controllers.handlers.*;
import bmstu.iu7m.osipov.ui.models.entities.FileEntryItem;
import bmstu.iu7m.osipov.ui.models.entities.UIMenuItemComponent;
import bmstu.iu7m.osipov.ui.models.stores.EventHandlersStore;
import bmstu.iu7m.osipov.ui.models.stores.UIComponentStore;
import bmstu.iu7m.osipov.ui.views.TextFieldTreeCell;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component("TextFieldCellCallback")
public class TextFieldTreeCellCallback implements Callback<TreeView<FileEntryItem>, TreeCell<FileEntryItem>> {

    @Autowired
    private FileLocatorService flocator;
    @Autowired
    private EventHandlersStore hdlrs;
    @Autowired
    private UIComponentStore uiStore;

    private ContextMenu fileMenu;

    private ContextMenu dirMenu;

    public TextFieldTreeCellCallback(){
        System.out.println("TextFieldCellCallback bean created");
    }

    @PostConstruct
    public void init(){
        if(flocator == null)
            System.out.println("no wiring at Callback");
        else
            System.out.println("FileLocator WIRED  at TextFieldTreeCellCallback");
    }

    @Override
    public TreeCell<FileEntryItem> call(TreeView<FileEntryItem> param) {
        TextFieldTreeCell cell = new TextFieldTreeCell(flocator, hdlrs, uiStore, this.fileMenu, this.dirMenu);
        return cell;
    }

    // called by RootWindowController at initDialogs(Stage s) method
    public void loadContextMenuForCells(){
        this.fileMenu = new ContextMenu();
        this.dirMenu = new ContextMenu();

        //Init Context Menu for Files
        MenuItem open = new MenuItem("Open");
        open.setId(UIComponentIds.TreeViewContextMenuOpen);
        MenuItem close = new MenuItem("Close");
        close.setId(UIComponentIds.TreeViewContextMenuClose);
        MenuItem lexgen = new MenuItem("Make lexer");
        lexgen.setId(UIComponentIds.TreeViewContextMenuLexerGen);
        MenuItem parsgen = new MenuItem("Make parser");
        parsgen.setId(UIComponentIds.TreeViewContextMenuParserGen);
        MenuItem par_file = new MenuItem("Parse file");
        par_file.setId(UIComponentIds.TreeViewContextMenuParseFile);

        UIMenuItemComponent c1_1 = new UIMenuItemComponent(open);
        UIMenuItemComponent c1_2 = new UIMenuItemComponent(close);
        UIMenuItemComponent c1_3 = new UIMenuItemComponent(lexgen);
        UIMenuItemComponent c1_4 = new UIMenuItemComponent(parsgen);
        UIMenuItemComponent c1_5 = new UIMenuItemComponent(par_file);

        //SET handlers to the items of menu.
        //BE SURE THAT EventHandlers have type parameter = ActionEvent!
        if(hdlrs != null && hdlrs.getHandlers().get("openFile") instanceof OpenFileHandler) {
            ((OpenFileHandler) hdlrs.getHandlers().get("openFile")).attachTo(ActionEvent.ACTION, c1_1);
        }
        if(hdlrs != null && hdlrs.getHandlers().get("closeFile") instanceof CloseFileHandler) {
            ((CloseFileHandler) hdlrs.getHandlers().get("closeFile")).attachTo(ActionEvent.ACTION, c1_2);
        }
        if(hdlrs != null && hdlrs.getHandlers().get("genLexer") instanceof CreateLexerHandler) {
            ((CreateLexerHandler) hdlrs.getHandlers().get("genLexer")).attachTo(ActionEvent.ACTION, c1_3);
        }
        if(hdlrs != null && hdlrs.getHandlers().get("genParser") instanceof CreateParserHandler) {
            ((CreateParserHandler) hdlrs.getHandlers().get("genParser")).attachTo(ActionEvent.ACTION, c1_4);
        }
        if(hdlrs != null && hdlrs.getHandlers().get("parseFile") instanceof ParseFileHandler) {
            ((ParseFileHandler) hdlrs.getHandlers().get("parseFile")).attachTo(ActionEvent.ACTION, c1_5);
        }

        fileMenu.getItems().add(open);
        fileMenu.getItems().add(lexgen);
        fileMenu.getItems().add(parsgen);
        fileMenu.getItems().add(par_file);
        fileMenu.getItems().add(close);

        //Init Context menu for Directories
        MenuItem dir_open = new MenuItem("Open");
        MenuItem dir_close = new MenuItem("Close");
        MenuItem cf = new MenuItem("Create file");
        MenuItem cdir = new MenuItem("Create dir");
        MenuItem com_pars = new MenuItem("Make common parser");

        dir_open.setId(UIComponentIds.TreeViewContextMenuDirOpen);
        dir_close.setId(UIComponentIds.TreeViewContextMenuDirClose);
        cf.setId(UIComponentIds.TreeViewContextMenuCreateFile);
        cdir.setId(UIComponentIds.TreeViewContextMenuCreateDir);
        com_pars.setId(UIComponentIds.TreeViewContextMenuCommonPrs);

        UIMenuItemComponent c2_1 = new UIMenuItemComponent(dir_open);
        UIMenuItemComponent c2_2 = new UIMenuItemComponent(dir_close);
        UIMenuItemComponent c2_3 = new UIMenuItemComponent(cf);
        UIMenuItemComponent c2_4 = new UIMenuItemComponent(cdir);
        UIMenuItemComponent c2_5 = new UIMenuItemComponent(com_pars);



        if(hdlrs != null && hdlrs.getHandlers().get("openFile") instanceof OpenFileHandler) {
            ((OpenFileHandler) hdlrs.getHandlers().get("openFile")).attachTo(ActionEvent.ACTION, c2_1);
        }
        if(hdlrs != null && hdlrs.getHandlers().get("closeFile") instanceof CloseFileHandler) {
            ((CloseFileHandler) hdlrs.getHandlers().get("closeFile")).attachTo(ActionEvent.ACTION, c2_2);
        }
        if(hdlrs != null && hdlrs.getHandlers().get("createFile") instanceof CreateFileHandler) {
            ((CreateFileHandler) hdlrs.getHandlers().get("createFile")).attachTo(ActionEvent.ACTION, c2_3);
        }
        if(hdlrs != null && hdlrs.getHandlers().get("createDir") instanceof CreateFileHandler) {
            ((CreateFileHandler) hdlrs.getHandlers().get("createDir")).attachTo(ActionEvent.ACTION, c2_4);
        }
        if(hdlrs != null && hdlrs.getHandlers().get("genCommonParser") instanceof CreateCommonParserHandler) {
            ((CreateCommonParserHandler) hdlrs.getHandlers().get("genCommonParser")).attachTo(ActionEvent.ACTION, c2_5);
        }

        dirMenu.getItems().add(dir_open);
        dirMenu.getItems().add(cf);
        dirMenu.getItems().add(cdir);
        dirMenu.getItems().add(com_pars);
        dirMenu.getItems().add(dir_close);

        this.uiStore.getComponents().put(UIComponentIds.TreeViewContextMenuOpen, c1_1);
        this.uiStore.getComponents().put(UIComponentIds.TreeViewContextMenuClose, c1_2);
        this.uiStore.getComponents().put(UIComponentIds.TreeViewContextMenuDirOpen, c2_1);
        this.uiStore.getComponents().put(UIComponentIds.TreeViewContextMenuDirClose, c2_2);
        this.uiStore.getComponents().put(UIComponentIds.TreeViewContextMenuCreateFile, c2_3);
        this.uiStore.getComponents().put(UIComponentIds.TreeViewContextMenuCreateDir, c2_4);
        this.uiStore.getComponents().put(UIComponentIds.TreeViewContextMenuCommonPrs, c2_5);
        this.uiStore.getComponents().put(UIComponentIds.TreeViewContextMenuLexerGen, c1_3);
        this.uiStore.getComponents().put(UIComponentIds.TreeViewContextMenuParserGen, c1_4);
        this.uiStore.getComponents().put(UIComponentIds.TreeViewContextMenuParseFile, c1_5);
        System.out.println("Context menu was built.");
    }
}
