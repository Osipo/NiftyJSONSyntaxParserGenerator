package bmstu.iu7m.osipov.services.parsers.json;

import bmstu.iu7m.osipov.services.parsers.json.elements.JsonObject;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;

@Service
public interface JsonParserService {
    public JsonObject parse(String fileName);
    public JsonObject parse(File fl);
    public JsonObject parseStream(InputStream in);
}
