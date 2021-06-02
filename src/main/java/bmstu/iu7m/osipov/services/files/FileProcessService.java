package bmstu.iu7m.osipov.services.files;

import javafx.scene.control.TextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.io.File;

@Service
public interface FileProcessService {
    void readFromFile(File f, TextArea ta);
    void readFromFile(String fullName, TextArea ta);
    void readFromFile(File f, RSyntaxTextArea t);
    void readFromFile(String fullName, RSyntaxTextArea t);
}
