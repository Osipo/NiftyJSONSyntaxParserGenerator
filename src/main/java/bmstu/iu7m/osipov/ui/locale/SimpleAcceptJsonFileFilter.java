package bmstu.iu7m.osipov.ui.locale;

import bmstu.iu7m.osipov.configurations.FileDialogText;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;

public class SimpleAcceptJsonFileFilter extends FileFilter {
    @Override
    public boolean accept(File f) {
        return (f != null && f.getName().endsWith(".json"))
                || (f != null && f.isDirectory());
    }

    @Override
    public String getDescription() {
        return UIManager.getString(FileDialogText.jsonFilterText);
    }
}
