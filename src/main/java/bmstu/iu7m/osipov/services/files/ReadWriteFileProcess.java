package bmstu.iu7m.osipov.services.files;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

public class ReadWriteFileProcess implements FileProcessService {

    @Override
    public void readFromFile(File f) {
        if(f == null || f.isDirectory())
            return;

        try(BufferedReader reader = Files.newBufferedReader(Paths.get(f.getAbsolutePath()) ) ){
            String line = null;
            while((line = reader.readLine()) != null){
                System.out.println(line);
            }
        }catch (InvalidPathException e){
            System.out.println("Cannot resolve path: "+f.getAbsolutePath());
        }
        catch (IOException e){
            System.out.println("Cannot open file by specified path: "+f.getAbsolutePath());
        }
    }

    @Override
    public void readFromFile(String fullName) {
        readFromFile(new File(fullName));
    }
}
