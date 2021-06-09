package bmstu.iu7m.osipov.ui.locale;

import bmstu.iu7m.osipov.configurations.FileDialogText;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.Locale;

public class SimpleAcceptAllFileFilter extends FileFilter {
    public SimpleAcceptAllFileFilter() {
    }

    @Override
    public boolean accept(File f) {
        return true;
    }

    @Override
    public String getDescription() {
        return UIManager.getString(FileDialogText.allFilterText);
    }
}
