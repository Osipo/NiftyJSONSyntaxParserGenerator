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

        //SET handlers to the items of menu.
        //BE SURE THAT EventHandlers have type parameter = ActionEvent!
        if(hdlrs != null && hdlrs.getHandlers().get("openFile") instanceof OpenFileHandler)
            open.addEventHandler(ActionEvent.ACTION, hdlrs.getHandlers().get("openFile"));
        if(hdlrs != null && hdlrs.getHandlers().get("closeFile") instanceof CloseFileHandler)
            close.addEventHandler(ActionEvent.ACTION, hdlrs.getHandlers().get("closeFile"));
        if(hdlrs != null && hdlrs.getHandlers().get("genLexer") instanceof CreateLexerHandler)
            lexgen.addEventHandler(ActionEvent.ACTION, hdlrs.getHandlers().get("genLexer"));
        if(hdlrs != null && hdlrs.getHandlers().get("genParser") instanceof CreateParserHandler)
            parsgen.addEventHandler(ActionEvent.ACTION, hdlrs.getHandlers().get("genParser"));
        if(hdlrs != null && hdlrs.getHandlers().get("parseFile") instanceof ParseFileHandler)
            par_file.addEventHandler(ActionEvent.ACTION, hdlrs.getHandlers().get("parseFile"));

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



        if(hdlrs != null && hdlrs.getHandlers().get("openFile") instanceof OpenFileHandler)
            dir_open.addEventHandler(ActionEvent.ACTION, hdlrs.getHandlers().get("openFile"));
        if(hdlrs != null && hdlrs.getHandlers().get("closeFile") instanceof CloseFileHandler)
            dir_close.addEventHandler(ActionEvent.ACTION, hdlrs.getHandlers().get("closeFile"));
        if(hdlrs != null && hdlrs.getHandlers().get("createFile") instanceof CreateFileHandler)
            cf.addEventHandler(ActionEvent.ACTION, hdlrs.getHandlers().get("createFile"));
        if(hdlrs != null && hdlrs.getHandlers().get("createDir") instanceof CreateFileHandler)
            cdir.addEventHandler(ActionEvent.ACTION, hdlrs.getHandlers().get("createDir"));
        if(hdlrs != null && hdlrs.getHandlers().get("genCommonParser") instanceof CreateCommonParserHandler)
            com_pars.addEventHandler(ActionEvent.ACTION, hdlrs.getHandlers().get("genCommonParser"));

        dirMenu.getItems().add(dir_open);
        dirMenu.getItems().add(cf);
        dirMenu.getItems().add(cdir);
        dirMenu.getItems().add(com_pars);
        dirMenu.getItems().add(dir_close);

        this.uiStore.getComponents().put(UIComponentIds.TreeViewContextMenuOpen, new UIMenuItemComponent(open));
        this.uiStore.getComponents().put(UIComponentIds.TreeViewContextMenuClose, new UIMenuItemComponent(close));
        this.uiStore.getComponents().put(UIComponentIds.TreeViewContextMenuDirOpen, new UIMenuItemComponent(dir_open));
        this.uiStore.getComponents().put(UIComponentIds.TreeViewContextMenuDirClose, new UIMenuItemComponent(dir_close));
        this.uiStore.getComponents().put(UIComponentIds.TreeViewContextMenuCreateFile, new UIMenuItemComponent(cf));
        this.uiStore.getComponents().put(UIComponentIds.TreeViewContextMenuCreateDir, new UIMenuItemComponent(cdir));
        this.uiStore.getComponents().put(UIComponentIds.TreeViewContextMenuCommonPrs, new UIMenuItemComponent(com_pars));
        this.uiStore.getComponents().put(UIComponentIds.TreeViewContextMenuLexerGen, new UIMenuItemComponent(lexgen));
        this.uiStore.getComponents().put(UIComponentIds.TreeViewContextMenuParserGen, new UIMenuItemComponent(parsgen));
        this.uiStore.getComponents().put(UIComponentIds.TreeViewContextMenuParseFile, new UIMenuItemComponent(par_file));
        System.out.println("Context menu was built.");
    }
}
