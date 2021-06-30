package bmstu.iu7m.osipov.services.terminal;

import bmstu.iu7m.osipov.Main;
import bmstu.iu7m.osipov.ui.models.TerminalModel;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Iterator;

public class ChangeCommand extends BaseCommand implements Command<String> {

    public ChangeCommand(TerminalModel model){
        this.model = model;
    }

    @Override
    public void execute(Collection<String> args) {
        Iterator<String> it =  args.iterator();
        while(it.hasNext()){
            String v1 = it.next();
            if(v1.equals("."))
                break;
            else if(v1.equals("..") && this.model.getCWD().indexOf(Main.PATH_SEPARATOR) == -1){
                break;
            }
            else if(v1.equals("..")){
                int li = this.model.getCWD().lastIndexOf(Main.PATH_SEPARATOR);
                this.model.setCWD(this.model.getCWD().substring(0, li) + ">");
                break;
            }

            Path np = Paths.get(this.model.getCWD().substring(0, this.model.getCWD().length() - 1), v1);
            if(!Files.exists(np)) {
                System.out.println("File \""+ np.toString()+"\" does not exists!");
            }
            this.model.setCWD(np.toString() + ">");
            break;
        }
    }
}
