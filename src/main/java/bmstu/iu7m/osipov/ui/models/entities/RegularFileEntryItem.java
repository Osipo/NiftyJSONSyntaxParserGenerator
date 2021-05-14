package bmstu.iu7m.osipov.ui.models.entities;

import javafx.beans.NamedArg;

public class RegularFileEntryItem extends FileEntryItem {
    public RegularFileEntryItem(@NamedArg("fname") String fname) {
        super(fname);
        isDir = false;
    }
}
