package bmstu.iu7m.osipov.ui.models.entities;

import javafx.beans.NamedArg;

public class DirectoryEntryItem extends FileEntryItem {
    public DirectoryEntryItem(@NamedArg("fname") String fname) {
        super(fname);
        isDir = true;
    }
}