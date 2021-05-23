package bmstu.iu7m.osipov.services.files;

import org.springframework.stereotype.Service;

@Service
public interface FileRetrievalService {
    String findEntry(String parent, String type, String fname);
}