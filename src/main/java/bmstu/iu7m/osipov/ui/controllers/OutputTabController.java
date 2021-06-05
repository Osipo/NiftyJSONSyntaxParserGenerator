package bmstu.iu7m.osipov.ui.controllers;

import bmstu.iu7m.osipov.threads.ConsoleReaderThread;
import bmstu.iu7m.osipov.ui.views.OutputTabView;
import javafx.fxml.FXML;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;

public class OutputTabController extends OutputTabView {

    private final PipedInputStream pin1;
    private final PipedInputStream pin2;
    private ConsoleReaderThread outputThread;

    public OutputTabController(){
        System.out.println("OutputTabController: constructor");
        this.pin1 = new PipedInputStream();
        this.pin2 = new PipedInputStream();
    }

    @FXML
    public void initialize(){
        System.out.println("OutputTabController: FXML Loaded.");
        super.initView();
    }

    @PostConstruct
    public void init(){
        System.out.println("Post construct of bean OutputTabController");
        super.saveUIComponents();
        initSystemOutput();
    }

    private void initSystemOutput(){
        try {
            PipedOutputStream pout = new PipedOutputStream(pin1);
            PipedOutputStream perr = new PipedOutputStream(pin2);
            System.setOut(new PrintStream(pout, true));
            System.setErr(new PrintStream(perr, true));
            
        }catch (IOException e){
            System.out.println("Cannot create pipes: IOException caught");
        }catch (SecurityException se){
            System.out.println("Cannot create pipes: SecurityException caught");
        }
        this.outputThread = new ConsoleReaderThread(pin1, pin2, output_text);
    }

    //close all threads on exit.
    public synchronized void closeThread()
    {
        System.out.println("Message: Stage is closed.");
        this.outputThread.setExit(true);
        notifyAll();
        try {
            this.outputThread.getReader().join(1000L); this.pin1.close();
        }
        catch (Exception e) {

        }
        try {
            this.outputThread.getReader2().join(1000L); this.pin2.close();
        }
        catch (Exception e) {

        }
        System.exit(0);
    }
}