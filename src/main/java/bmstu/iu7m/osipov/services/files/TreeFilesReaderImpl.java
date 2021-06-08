package bmstu.iu7m.osipov.services.files;

import bmstu.iu7m.osipov.Main;
import bmstu.iu7m.osipov.structures.lists.LinkedStack;
import bmstu.iu7m.osipov.ui.models.entities.DirectoryEntryItem;
import bmstu.iu7m.osipov.ui.models.entities.FileEntryItem;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.FileSystems;
import java.util.Arrays;
import java.util.List;

public class TreeFilesReaderImpl implements TreeFilesReaderService {

    @Autowired
    private FileLocatorService fService;

    public TreeFilesReaderImpl(){
        System.out.println("TreeFilesReaderImpl constructor.");
    }

    @Override
    public TreeItem<FileEntryItem> getByPathIn(TreeItem<FileEntryItem> parent, String path){
        if(parent == null || parent.getValue() == null || path == null || parent.getChildren().size() == 0) {
            System.out.printf("Parent \"%s\" is null or it is a leaf node.\n", parent.getValue().getFullFileName());
            return null;
        }
        System.out.println("Subpath is: "+path);
        List<String> dirs = PathStringUtils.splitPath(path);
        int i = 0;
        TreeItem<FileEntryItem> cur = parent;
        while(i != dirs.size() && cur != null){
            cur = getByNameIn(cur, dirs.get(i));
            if(cur.getValue() instanceof DirectoryEntryItem && cur.isLeaf()){
                cur.getChildren().addAll(
                        fService.getFileEntriesIn(cur.getValue().getFullFileName())
                                .getChildren()
                );
                cur.setExpanded(true);
            }
            i++;
        }
        return cur;
    }

    @Override
    public TreeItem<FileEntryItem> getByNameIn(TreeItem<FileEntryItem> parent, String name) {
        if(parent == null || parent.getValue() == null || name == null || parent.getChildren().size() == 0) {
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
