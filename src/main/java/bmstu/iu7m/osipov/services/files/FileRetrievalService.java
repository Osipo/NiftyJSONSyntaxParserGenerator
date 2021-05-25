package bmstu.iu7m.osipov.services.files;

import bmstu.iu7m.osipov.ui.models.entities.FileEntryItem;
import javafx.scene.control.TreeItem;
import org.springframework.stereotype.Service;

@Service
public interface FileRetrievalService {
    TreeItem<FileEntryItem> findEntry(TreeItem<FileEntryItem> parent, String type, String fname);
}