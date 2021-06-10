package bmstu.iu7m.osipov.ui.views.callbacks;

import bmstu.iu7m.osipov.configurations.UIComponentIds;
import bmstu.iu7m.osipov.services.files.FileLocatorService;
import bmstu.iu7m.osipov.ui.controllers.handlers.OpenFileHandler;
import bmstu.iu7m.osipov.ui.models.entities.FileEntryItem;
import bmstu.iu7m.osipov.ui.models.entities.UIMenuItemComponent;
import bmstu.iu7m.osipov.ui.models.stores.EventHandlersStore;
import bmstu.iu7m.osipov.ui.models.stores.UIComponentStore;
import bmstu.iu7m.osipov.ui.views.TextFieldTreeCell;
import javafx.event.ActionEvent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
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

    private ContextMenu menu;

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
        TextFieldTreeCell cell = new TextFieldTreeCell(flocator, hdlrs, uiStore, menu);
        return cell;
    }

    public void loadContextMenuForCells(){
        this.menu = new ContextMenu();
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

        menu.getItems().add(open);
        menu.getItems().add(close);
        this.uiStore.getComponents().put(UIComponentIds.TreeViewContextMenuOpen, new UIMenuItemComponent(open));
        this.uiStore.getComponents().put(UIComponentIds.TreeViewContextMenuClose, new UIMenuItemComponent(close));
    }
}
