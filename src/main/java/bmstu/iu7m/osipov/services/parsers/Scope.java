package bmstu.iu7m.osipov.services.parsers;

import java.util.List;
import java.util.Set;

public class Scope {
    private final Set<String> start;
    private final String end;
    private final String body;
    private boolean isReduce;

    public Scope(Set<String> start, String end, String body){
        this.start = start;
        this.end = end;
        this.body = body;
        this.isReduce = false;
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

    public boolean isReduce() {
        return isReduce;
    }

    public void setReduce(boolean reduce) {
        isReduce = reduce;
    }

    @Override
    public String toString() {
        return "{" +
                "\"start\" : \"" + start + '\"' +
                ",\"end\" : \"" + end + '\"' +
                ",\"body\" : \"" + body + '\"' +
                ",\"reduce\" : \"" + isReduce + '\"' +
                 '}';
    }
}
