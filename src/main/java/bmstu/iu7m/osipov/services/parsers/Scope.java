package bmstu.iu7m.osipov.services.parsers;

import java.util.List;
import java.util.Set;

public class Scope {
    private final Set<String> start;
    private final String end;
    private final String body;

    public Scope(Set<String> start, String end, String body){
        this.start = start;
        this.end = end;
        this.body = body;
    }

    public Set<String> getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "{" +
                "\"start\" : \"" + start + '\"' +
                ",\"end\" : \"" + end + '\"' +
                ",\"body\" : \"" + body + '\"' +
                '}';
    }
}
