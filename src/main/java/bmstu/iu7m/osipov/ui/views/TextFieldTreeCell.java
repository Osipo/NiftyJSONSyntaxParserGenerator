package bmstu.iu7m.osipov.ui.views;

import bmstu.iu7m.osipov.configurations.UIComponentIds;
import bmstu.iu7m.osipov.services.files.FileLocatorService;
import bmstu.iu7m.osipov.ui.models.entities.*;
import bmstu.iu7m.osipov.ui.models.stores.EventHandlersStore;
import bmstu.iu7m.osipov.ui.models.stores.UIComponentStore;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;

public class TextFieldTreeCell extends TreeCell<FileEntryItem> {

    private TextField textField;

    private ContextMenu fileMenu;

    private ContextMenu dirMenu;

    private EventHandlersStore hdlrs;

    private UIComponentStore uiStore;

    private FileLocatorService fileLocator;

    public void initToolTips(){
        this.setTooltip(new Tooltip(getItem().getFullFileName()));
    }

    public TextFieldTreeCell(FileLocatorService flocator,
                             EventHandlersStore hdlrs,
                             UIComponentStore uiStore,
                             ContextMenu fmenu,
                             ContextMenu dirMenu) {
        this.hdlrs = hdlrs;
        this.uiStore = uiStore;
        this.fileMenu = fmenu;
        this.dirMenu = dirMenu;
        this.setContextMenu(fmenu);
        this.fileLocator = flocator;
        if(this.fileLocator == null)
            System.out.println("NOT WIRED");

        //System.out.println("TreeCell created");


        super.setOnMouseClicked(event -> {

            //IF IS DIRECTORY AND IT IS SELECTED.
            if(event.getClickCount() == 1  && getItem() != null && getItem() instanceof DirectoryEntryItem){
                System.out.println(getItem().getFullFileName());

                //HERE ADD ITEMS OF DIRECTORY IF IT IS LEAF
                if(getTreeItem().isLeaf()){
                    getTreeItem().getChildren().addAll(
                            fileLocator.getFileEntriesIn(getItem().getFullFileName())
                                .getChildren()
                    );
                }
                //IF LEFT CLICK THEN expand the directory.
                if(event.getButton() == MouseButton.PRIMARY)
                    getTreeItem().setExpanded(!getTreeItem().isExpanded());
            }
//            //ELSE IF FILE SELECTED
//            else if(event.getClickCount() == 1 && getItem() != null){
//                System.out.println(getItem().getFullFileName());
//            }
            getTreeView().getSelectionModel().select(getTreeItem());
        });
    }


    @Override
    public void startEdit() {
        super.startEdit();

        if (textField == null) {
            createTextField();
        }
        setText(null);
        setGraphic(textField);
        textField.selectAll();
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setText((String) getItem().toString());
        setGraphic(getTreeItem().getGraphic());
    }

    @Override
    public void updateItem(FileEntryItem item, boolean empty) {
        super.updateItem(item, empty);
        if(item instanceof DirectoryEntryItem){
            this.setContextMenu(dirMenu);
        }
        else if(item instanceof RegularFileEntryItem){
            this.setContextMenu(fileMenu);
        }
        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (isEditing()) {
                if (textField != null) {
                    textField.setText(getString());
                }
                setText(null);
                setGraphic(textField);
            } else {
                setText(getString());
                setGraphic(getTreeItem().getGraphic());
            }
        }
    }

    private void createTextField() {
        textField = new TextField(getString());
        textField.setOnKeyReleased(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                getItem().setFileName(textField.getText());
                commitEdit(getItem());
            } else if (keyEvent.getCode() == KeyCode.ESCAPE) {
                cancelEdit();
            }
        });
    }


    private String getString() {
        return getItem() == null ? "" : getItem().toString();
    }
}
