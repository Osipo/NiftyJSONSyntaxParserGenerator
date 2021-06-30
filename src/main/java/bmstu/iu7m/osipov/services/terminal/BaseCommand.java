package bmstu.iu7m.osipov.services.terminal;

import bmstu.iu7m.osipov.ui.models.TerminalModel;

import java.util.Collection;

public abstract class BaseCommand implements Command<String> {

    protected TerminalModel model;

    @Override
    public void execute(Collection<String> args) {

    }

    public String getType(){
        return getClass().getName();
    }
}
