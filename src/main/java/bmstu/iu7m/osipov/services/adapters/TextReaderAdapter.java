package bmstu.iu7m.osipov.services.adapters;

import java.io.IOException;
import java.io.Reader;

public interface TextReaderAdapter {
    String read(Reader reader) throws IOException;
}
