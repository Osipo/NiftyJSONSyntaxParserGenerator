package bmstu.iu7m.osipov.services.files;

import javafx.scene.control.TextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Position;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;

public class ReadWriteFileProcess implements FileProcessService {

    public ReadWriteFileProcess(){
        System.out.println("ReadWriteFileProcess constructor");
    }

    public void readFromFile(File f, RSyntaxTextArea t){
        if(f == null || f.isDirectory())
            return;
        CountDownLatch awaiter = new CountDownLatch(1);
        SwingUtilities.invokeLater(() ->{
            t.setText("");
            try(BufferedReader reader = Files.newBufferedReader(Paths.get(f.getAbsolutePath()) ) ){
                String line = null;
                while((line = reader.readLine()) != null){
                    t.insert(line + "\n", t.getDocument().getLength());
                    //t.getDocument().insertString(t.getDocument().getLength(),line+"\n");
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

    public void readFromFile(String fullName, RSyntaxTextArea t){ readFromFile(new File(fullName), t);}

    @Override
    public void writeToFile(String fullName, RSyntaxTextArea t) throws FileNotFoundException {
        RandomAccessFile f = null;
        FileChannel channel = null;
        FileLock lock = null;
        if(fullName == null)
            throw new FileNotFoundException("File can not be found as its name was not specified.");
        try {
            f = new RandomAccessFile(fullName, "rw");
        } catch (FileNotFoundException ex){
            System.out.printf("File \"%s\" does not exists\n", fullName);
            throw ex;
        }
        try{
            channel = f.getChannel();
            lock = channel.tryLock();
        } catch (OverlappingFileLockException | IOException ex2) {
            System.out.println("File is editing now. Cannot write content.");
            try{
                f.close();
                if(channel != null)
                    channel.close();
            } catch (IOException ex3){
                System.out.println("Cannot close FileChannel.");
            }
            return;
        }
        int pos = 0;
        int eof = t.getDocument().getLength();
        String line = null;
        while(pos <= eof){
            try {
                line = t.getDocument().getText(pos, eof);
            }catch (BadLocationException ex){
                break;
            }
            pos += line.length();
            ByteBuffer buf = ByteBuffer.allocate(line.length());
            buf.put(line.getBytes());
            buf.flip();
            try {
                channel.write(buf);
            }catch (IOException ex4){break;}
        }
        try{
            f.close();
            channel.close();
        } catch (IOException ex3){
            System.out.println("Cannot close FileChannel.");
        }
    }
}
