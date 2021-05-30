package bmstu.iu7m.osipov.ui.controllers.handlers;

import bmstu.iu7m.osipov.services.adapters.TextReaderAdapter;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.*;

public class CommandShellHandler implements EventHandler<KeyEvent>, Runnable {

    private TextArea cmd_text;

    private BufferedReader cmd_reader;

    private BufferedWriter cmd_writer;

    private boolean exit = false;

    public CommandShellHandler(Process cmd, TextArea cmd_text){
        this.cmd_text = cmd_text;
        InputStream in = cmd.getInputStream();
        OutputStream out = cmd.getOutputStream();
        try {
            cmd_reader = new BufferedReader(new InputStreamReader(in, "gb2312"));
            cmd_writer = new BufferedWriter(new OutputStreamWriter(out, "gb2312"));
        }catch (UnsupportedEncodingException e){
            System.out.println("Cannot find encoding for charset: gb2312");
            System.out.println("Try to use default encoding");
            cmd_reader = new BufferedReader(new InputStreamReader(in));
            cmd_writer = new BufferedWriter(new OutputStreamWriter(out));
        }
        Thread th = new Thread(this);
        th.start();
    }

    @Override
    public void run() {
        String i_line = null;
        String o_line = null;

        while(!exit) {
            try {
                while ((i_line = cmd_reader.readLine()) != null) {
                    System.out.println(i_line);
                    setText(i_line, ConsoleTextDirection.FROM_CMD);
                }
                setText(System.getProperty("user.dir")+">", ConsoleTextDirection.FROM_CMD);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

    }

    private void setText(String txt, ConsoleTextDirection dir){
        Platform.runLater(() ->{
            if(dir == ConsoleTextDirection.FROM_CMD)
                cmd_text.appendText(txt+"\n");
            else{
                try {
                    cmd_writer.write(txt, 0, txt.length());
                    cmd_text.appendText(txt);

                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        });
    }

    @Override
    public void handle(KeyEvent event) {
        KeyCode k = event.getCode();
        String txt = event.getCharacter();
        setText(txt, ConsoleTextDirection.TO_CMD);
    }
}
