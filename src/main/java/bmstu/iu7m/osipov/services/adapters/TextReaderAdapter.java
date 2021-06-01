package bmstu.iu7m.osipov.services.adapters;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
@Service
public interface TextReaderAdapter {
    String read(Reader reader) throws IOException;
}
