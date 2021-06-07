package bmstu.iu7m.osipov.services.files;

import bmstu.iu7m.osipov.ui.models.entities.FileEntryItem;
import javafx.scene.control.TreeItem;
import org.springframework.stereotype.Service;

@Service
public interface TreeFilesReaderService {
    /**
     *
     * @param parent the root node where we start searching
     * @param name the name of the file that is in the child of the parent node.
     * @return child of the root node where name is presented or else null.
     */
    TreeItem<FileEntryItem> getByNameIn(TreeItem<FileEntryItem> parent, String name);

    /**
     * Unlike than getByNameIn this procedure traverse the tree more than 1 step.
     * @param parent the root node where we start searching
     * @param path the name of the file that is in the child of the some node.
     * @return node with name of that file (last substring follows after path separator) or null.
     */
    TreeItem<FileEntryItem> getByPathIn(TreeItem<FileEntryItem> parent, String path);
}
