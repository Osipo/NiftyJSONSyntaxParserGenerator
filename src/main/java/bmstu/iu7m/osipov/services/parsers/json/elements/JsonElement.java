package bmstu.iu7m.osipov.services.parsers.json.elements;

import java.util.ArrayList;

public abstract class JsonElement<T> {
    public abstract T getValue();

    @Override
    public String toString(){
        return getValue().toString();
    }

}
