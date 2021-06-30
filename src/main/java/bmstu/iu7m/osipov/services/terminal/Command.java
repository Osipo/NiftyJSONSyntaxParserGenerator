package bmstu.iu7m.osipov.services.terminal;

import java.util.Collection;

public interface Command<T> {
    public void execute(Collection<T> args);
}
