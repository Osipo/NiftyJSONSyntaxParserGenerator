package bmstu.iu7m.osipov.ui.views.callbacks;

import bmstu.iu7m.osipov.configurations.UIComponentIds;
import bmstu.iu7m.osipov.services.files.FileLocatorService;
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

    public void loadContextMenuForCells(){
        this.fileMenu = new ContextMenu();
        this.dirMenu = new ContextMenu();

        //Init Context Menu for Files
        MenuItem open = new MenuItem("Open");
        open.setId(UIComponentIds.TreeViewContextMenuOpen);
        MenuItem close = new MenuItem("Close");
        close.setId(UIComponentIds.TreeViewContextMenuClose);

        //SET handlers to the items of menu.
        //BE SURE THAT EventHandlers have type parameter = ActionEvent!
        if(hdlrs != null && hdlrs.getHandlers().get("openFile") != null)
            open.addEventHandler(ActionEvent.ACTION, hdlrs.getHandlers().get("openFile"));
        if(hdlrs != null && hdlrs.getHandlers().get("closeFile") != null)
            close.addEventHandler(ActionEvent.ACTION, hdlrs.getHandlers().get("closeFile"));

        fileMenu.getItems().add(open);
        fileMenu.getItems().add(close);

        //Init Context menu for Directories
        MenuItem dir_open = new MenuItem("Open");
        dir_open.setId(UIComponentIds.TreeViewContextMenuDirOpen);
        MenuItem dir_close = new MenuItem("Close");
        dir_close.setId(UIComponentIds.TreeViewContextMenuDirClose);
        MenuItem cf = new MenuItem("Create file");
        MenuItem cdir = new MenuItem("Create dir");
        cf.setId(UIComponentIds.TreeViewContextMenuCreateFile);
        cdir.setId(UIComponentIds.TreeViewContextMenuCreateDir);


        if(hdlrs != null && hdlrs.getHandlers().get("openFile") != null)
            dir_open.addEventHandler(ActionEvent.ACTION, hdlrs.getHandlers().get("openFile"));
        if(hdlrs != null && hdlrs.getHandlers().get("closeFile") != null)
            dir_close.addEventHandler(ActionEvent.ACTION, hdlrs.getHandlers().get("closeFile"));
        if(hdlrs != null && hdlrs.getHandlers().get("createFile") != null)
            cf.addEventHandler(ActionEvent.ACTION, hdlrs.getHandlers().get("createFile"));
        if(hdlrs != null && hdlrs.getHandlers().get("createDir") != null)
            cdir.addEventHandler(ActionEvent.ACTION, hdlrs.getHandlers().get("createDir"));

        dirMenu.getItems().add(dir_open);
        dirMenu.getItems().add(cf);
        dirMenu.getItems().add(cdir);
        dirMenu.getItems().add(dir_close);

        this.uiStore.getComponents().put(UIComponentIds.TreeViewContextMenuOpen, new UIMenuItemComponent(open));
        this.uiStore.getComponents().put(UIComponentIds.TreeViewContextMenuClose, new UIMenuItemComponent(close));
        this.uiStore.getComponents().put(UIComponentIds.TreeViewContextMenuDirOpen, new UIMenuItemComponent(dir_open));
        this.uiStore.getComponents().put(UIComponentIds.TreeViewContextMenuDirClose, new UIMenuItemComponent(dir_close));
        this.uiStore.getComponents().put(UIComponentIds.TreeViewContextMenuCreateFile, new UIMenuItemComponent(cf));
        this.uiStore.getComponents().put(UIComponentIds.TreeViewContextMenuCreateDir, new UIMenuItemComponent(cdir));
        System.out.println("Context menu was built.");
    }
}
