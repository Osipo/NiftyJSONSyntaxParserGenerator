package bmstu.iu7m.osipov.services.files;

import org.springframework.stereotype.Service;

import java.io.File;

@Service
public interface FileProcessService {
    void readFromFile(File f);
    void readFromFile(String fullName);
}
