package bmstu.iu7m.osipov.services.files;

import bmstu.iu7m.osipov.ui.models.entities.FileEntryItem;
import javafx.scene.control.TreeItem;
import org.springframework.stereotype.Service;

@Service
public interface FileLocatorService {
    public TreeItem<FileEntryItem> getAllFileEntriesFrom(String pdir);
    public TreeItem<FileEntryItem> getFileEntriesIn(String pdir);
    public void addEntriesTo(TreeItem<FileEntryItem> pdir);

    public TreeItem<FileEntryItem> getFileEntriesTo(String dest);
}
