package bmstu.iu7m.osipov.services.files;

import javafx.scene.control.TextArea;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;

public class ReadWriteFileProcess implements FileProcessService {

    public void readFromFile(File f, JTextPane t){
        if(f == null || f.isDirectory())
            return;
        CountDownLatch awaiter = new CountDownLatch(1);
        SwingUtilities.invokeLater(() ->{
            t.setText("");
            try(BufferedReader reader = Files.newBufferedReader(Paths.get(f.getAbsolutePath()) ) ){
                String line = null;
                while((line = reader.readLine()) != null){
                    t.getDocument().insertString(t.getDocument().getLength(),line+"\n", t.getCharacterAttributes());
                }
                awaiter.countDown();
            }
            catch (InvalidPathException e){
                System.out.println("Cannot resolve path: "+f.getAbsolutePath());
                awaiter.countDown();
            }
            catch (IOException e){
                System.out.println("Cannot open file by specified path: "+f.getAbsolutePath());
                awaiter.countDown();
            } catch (BadLocationException e) {
                System.out.println("Illegal position at document of JTextPane element");
                awaiter.countDown();
            }
        });
        try {
            awaiter.await();
        }catch (InterruptedException ex){
            System.out.println("Cannot interrupt GUI_FX_Thread: cannot wait Swing GUI AWT Thread (readFromFile() method)");
        }
    }

    @Override
    public void readFromFile(File f, TextArea t) {
        if(f == null || f.isDirectory())
            return;

        t.setText("");
        try(BufferedReader reader = Files.newBufferedReader(Paths.get(f.getAbsolutePath()) ) ){
            String line = null;
            while((line = reader.readLine()) != null){
                t.appendText(line+"\n");
            }
        }catch (InvalidPathException e){
            System.out.println("Cannot resolve path: "+f.getAbsolutePath());
        }
        catch (IOException e){
            System.out.println("Cannot open file by specified path: "+f.getAbsolutePath());
        }
    }

    @Override
    public void readFromFile(String fullName, TextArea t) {
        readFromFile(new File(fullName), t);
    }

    public void readFromFile(String fullName, JTextPane t){ readFromFile(new File(fullName), t);}
}
