package bmstu.iu7m.osipov.services.adapters;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class TextReaderAdapterImpl implements TextReaderAdapter {

    public TextReaderAdapterImpl(){
        System.out.println("TextReaderAdapterImpl constructor.");
    }
    @Override
    public String read(Reader reader) throws IOException {
        BufferedReader r1 = null;
        InputStreamReader r2 = null;
        StringBuilder sb = new StringBuilder();
        if(reader instanceof BufferedReader) {
            r1 = (BufferedReader) reader;
            sb.append(r1.readLine());
        }
        else if(reader instanceof InputStreamReader){
            r2 = (InputStreamReader) reader;
            sb.append(r2.read());
        }
        return sb.toString();
    }
}
