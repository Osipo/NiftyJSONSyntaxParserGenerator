package bmstu.iu7m.osipov.services.files;

import bmstu.iu7m.osipov.structures.lists.LinkedStack;
import bmstu.iu7m.osipov.ui.models.entities.DirectoryEntryItem;
import bmstu.iu7m.osipov.ui.models.entities.FileEntryItem;
import bmstu.iu7m.osipov.ui.models.entities.RegularFileEntryItem;
import javafx.scene.control.TreeItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileRetrievalServiceImpl implements FileRetrievalService {

    public FileRetrievalServiceImpl(){
        System.out.println("FileRetrievalServiceImpl constructor call");
    }

    @Autowired
    private FileLocatorService flocator;

    @Override
    public TreeItem<FileEntryItem> findEntry(TreeItem<FileEntryItem> parent, String type, String fname) {
        if(parent == null || type == null || fname == null)
            return null;

        FileType t = FileType.valueOf(type);
        String actual = null;
        LinkedStack<TreeItem<FileEntryItem>> entries = new LinkedStack<>();
        entries.push(parent);
        while(!entries.isEmpty()){
            parent = entries.top();
            entries.pop();
            actual = parent.getValue().getFileName();
            if(parent.getValue() instanceof RegularFileEntryItem &&
                (t == FileType.All || t == FileType.Files) && actual.equals(fname))
            {
                    return parent;
            }
            /* else if it is a directory that we are looking for */
            else if(parent.getValue() instanceof DirectoryEntryItem &&
                    (t == FileType.All || t == FileType.Directories) && actual.equals(fname))
            {
                return parent;
            }
            /* or else it is just a directory */
            else if(parent.getValue() instanceof DirectoryEntryItem){
                if(!parent.isExpanded() && parent.isLeaf())
                    flocator.addEntriesTo(parent);
                for(TreeItem<FileEntryItem> ch : parent.getChildren())
                    entries.push(ch);
            }
        }
        return null;
    }
}
