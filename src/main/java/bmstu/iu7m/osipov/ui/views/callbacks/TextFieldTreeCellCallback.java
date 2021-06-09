package bmstu.iu7m.osipov.ui.views.callbacks;

import bmstu.iu7m.osipov.services.files.FileLocatorService;
import bmstu.iu7m.osipov.ui.models.entities.FileEntryItem;
import bmstu.iu7m.osipov.ui.models.stores.EventHandlersStore;
import bmstu.iu7m.osipov.ui.views.TextFieldTreeCell;
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
        TextFieldTreeCell cell = new TextFieldTreeCell(flocator, hdlrs);
        return cell;
    }
}
