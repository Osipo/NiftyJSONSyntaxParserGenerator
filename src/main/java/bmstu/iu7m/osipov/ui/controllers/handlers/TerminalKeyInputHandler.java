package bmstu.iu7m.osipov.ui.controllers.handlers;

import bmstu.iu7m.osipov.ui.models.TerminalModel;
import bmstu.iu7m.osipov.utils.PathStringUtils;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import java.util.List;

public class TerminalKeyInputHandler implements EventHandler<KeyEvent> {

    protected TerminalModel termModel;

    public TerminalKeyInputHandler(TerminalModel termModel){
        this.termModel = termModel;
    }

    @Override
    public void handle(KeyEvent event) {
        String ch = event.getCharacter();
        String txt = this.termModel.getView().getTerminalText().getText();
        int cwd = this.termModel.getPosition();
        String c_line = null;

        //Enter button.
        if(event.getEventType() == KeyEvent.KEY_PRESSED && event.getCode() == KeyCode.ENTER){
            c_line = txt.substring(cwd);//skip CWD pointer.
            System.out.println("cmd line: "+ c_line);

            this.termModel.addToList(c_line);
            this.termModel.getView().getTerminalText().appendText("\n");

            List<String> cargs = PathStringUtils.splitPath(c_line, "-= ");
            System.out.print("Arguments: [");
            for(String s : cargs){
                System.out.print(s);
                System.out.print('|');
            }
            System.out.println("]");
            String cName = cargs.get(0);// command name is first argument.
            cargs.remove(0);//remove redundant command name.
            this.termModel.executeCommand(cName, cargs);
            this.termModel.getView().getTerminalText().appendText(this.termModel.getCWD());
            this.termModel.changePosition();
        }
        else if(event.getEventType() == KeyEvent.KEY_PRESSED
                && event.getCode() == KeyCode.BACK_SPACE
                && this.termModel.getView().getTerminalText().getText().length() == cwd){
            //EMPTY BODY. DO NOTHING.
        }
        else if(event.getEventType() == KeyEvent.KEY_PRESSED
                && event.getCode() == KeyCode.BACK_SPACE){
            this.termModel.getView().getTerminalText().setText(txt.substring(0, txt.length() - 1));
        }
        else if(event.getEventType() == KeyEvent.KEY_TYPED){
            this.termModel.getView().getTerminalText().appendText(ch);
        }
    }
}
