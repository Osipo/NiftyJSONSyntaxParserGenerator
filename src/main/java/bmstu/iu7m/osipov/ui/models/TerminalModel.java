package bmstu.iu7m.osipov.ui.models;

import bmstu.iu7m.osipov.Main;
import bmstu.iu7m.osipov.services.terminal.ChangeCommand;
import bmstu.iu7m.osipov.services.terminal.Command;
import bmstu.iu7m.osipov.ui.views.ConsoleTabView;

import java.util.*;

public class TerminalModel {
    private String cur_dir;
    private ConsoleTabView view;
    private List<String> history;
    private Map<String, Command> shell;
    private int position;


    public TerminalModel(){
        this.cur_dir = Main.CWD + ">";
        this.history = new LinkedList<String>();
        this.position = this.cur_dir.length();
        this.shell = new HashMap<>();
        this.shell.put("cd", new ChangeCommand(this));
    }

    public void setView(ConsoleTabView v){
        this.view = v;
    }

    public void setCWD(String cwd){
        this.cur_dir = cwd;
    }

    public ConsoleTabView getView(){
        return this.view;
    }

    public String getCWD(){
        return this.cur_dir;
    }

    public void addToList(String cmd){
        this.history.add(cmd);
    }

    public void changePosition(){
        this.position = this.view.getTerminalText().getText().length();
    }

    public int getPosition() {
        return position;
    }

    public String getLastCommand(){
        if(this.history.size() > 0)
            return this.history.get(this.history.size() - 1);
        else
            return null;
    }

    public void executeCommand(String cmd, Collection<String> args){
        Command C = shell.getOrDefault(cmd, null);
        if(C != null)
            C.execute(args);
    }
}
