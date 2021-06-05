package bmstu.iu7m.osipov.ui.views;

import bmstu.iu7m.osipov.services.files.FileLocatorService;
import bmstu.iu7m.osipov.ui.models.entities.DirectoryEntryItem;
import bmstu.iu7m.osipov.ui.models.entities.FileEntryItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.input.KeyCode;

public class TextFieldTreeCell extends TreeCell<FileEntryItem> {

    private TextField textField;
    private ContextMenu menu = new ContextMenu();

    private FileLocatorService fileLocator;

    public TextFieldTreeCell(FileLocatorService flocator) {
        this.fileLocator = flocator;
        if(this.fileLocator == null)
            System.out.println("NOT WIRED");

        MenuItem item = new MenuItem("Open");
        //System.out.println("TreeCell created");
        menu.getItems().add(item);


        super.setOnMouseClicked(event -> {

            //IF IS DIRECTORY AND IT IS SELECTED.
            if(event.getClickCount() == 1  && getItem() != null && getItem() instanceof DirectoryEntryItem){
                System.out.println(getItem().getFullFileName());

                //HERE ADD ITEMS OF DIRECTORY IF IT IS LEAF.
                if(getTreeItem().isLeaf()){
                    getTreeItem().getChildren().addAll(
                            fileLocator.getFileEntriesIn(getItem().getFullFileName())
                                .getChildren()
                    );
                }
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
                setContextMenu(menu);
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
