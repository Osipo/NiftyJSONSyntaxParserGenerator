package bmstu.iu7m.osipov.threads;

import javafx.scene.control.TextArea;

import java.io.IOException;
import java.io.PipedInputStream;

public class ConsoleReaderThread implements Runnable {

    private final PipedInputStream pipeIn;
    private final PipedInputStream pipeIn2;
    Thread errorThrower;
    private Thread reader;
    private Thread reader2;
    private volatile boolean quit;
    private final TextArea output;

    public ConsoleReaderThread(PipedInputStream p1, PipedInputStream p2, TextArea output){
        this.pipeIn = p1;
        this.pipeIn2 = p2;
        this.output = output;
        this.quit = false;

        //launch reader of System.out
        this.reader = new Thread(this);
        this.reader.setDaemon(true);
        this.reader.start();

        //launch reader of System.err
        this.reader2 = new Thread(this);
        this.reader2.setDaemon(true);
        this.reader2.start();

        //launch reader of throw Exception output
        this.errorThrower = new Thread(this);
        this.errorThrower.setDaemon(true);
        this.errorThrower.start();
    }

    public void setExit(boolean val){
        this.quit = val;
    }

    public Thread getReader(){
        return this.reader;
    }

    public Thread getReader2(){
        return this.reader2;
    }

    public Thread getErrorThrower(){
        return this.errorThrower;
    }

    @Override
    public synchronized void run() {
        try {
            while (Thread.currentThread() == this.reader) {
                try{
                    wait(100L);
                } catch (InterruptedException ex){

                }
                if (this.pipeIn.available() != 0)
                {
                    String input = readLine(this.pipeIn); //reading console output stream from System.out
                    this.output.appendText(input);
                }
                if(this.quit) return;
            }
            while (Thread.currentThread() == this.reader2) {
                try{
                    wait(100L);
                } catch (InterruptedException ex){

                }
                if (this.pipeIn2.available() != 0)
                {
                    String input = readLine(this.pipeIn2); //reading console output stream from System.err
                    this.output.appendText(input);
                }
                if(this.quit) return;
            }
        } catch (Exception e){}
        if(Thread.currentThread() == this.errorThrower){
            try{
                wait(800L);
            }catch (InterruptedException ex){

            }
            System.out.println("******System Console started successfully******");
        }
    }

    public synchronized String readLine(PipedInputStream in) throws IOException {
        String input = "";
        do {
            int available = in.available();
            if (available == 0) break;
            byte[] b = new byte[available];
            in.read(b);
            input = input + new String(b, 0, b.length);
        } while ((!input.endsWith("\n")) && (!input.endsWith("\r\n")) && (!this.quit));
        return input;
    }
}
