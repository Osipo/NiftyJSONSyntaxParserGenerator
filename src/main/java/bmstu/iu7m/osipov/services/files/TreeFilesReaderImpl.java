package bmstu.iu7m.osipov.services.files;

import bmstu.iu7m.osipov.ui.models.entities.FileEntryItem;
import javafx.scene.control.TreeItem;

public class TreeFilesReaderImpl implements TreeFilesReaderService {

    public TreeFilesReaderImpl(){
        System.out.println("TreeFilesReaderImpl constructor.");
    }

    @Override
    public TreeItem<FileEntryItem> getByNameIn(TreeItem<FileEntryItem> parent, String name) {
        if(parent == null || name == null || parent.getChildren().size() == 0) {
            System.out.printf("Parent \"%s\" is null or it is a leaf node.\n", parent.getValue().getFullFileName());
            return null;
        }

        for(TreeItem<FileEntryItem> ch : parent.getChildren()){
            if(ch.getValue() != null && ch.getValue().getFileName().equals(name))
                return ch;
        }
        System.out.printf("Cannot find file \"%s\" at \"%s\"\n", name, parent.getValue().getFullFileName());
        return null;
    }
}
