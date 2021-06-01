package bmstu.iu7m.osipov.services.files;

import javafx.scene.control.TextArea;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.io.File;

@Service
public interface FileProcessService {
    void readFromFile(File f, TextArea ta);
    void readFromFile(String fullName, TextArea ta);
    void readFromFile(File f, JTextPane t);
    void readFromFile(String fullName, JTextPane t);
}
