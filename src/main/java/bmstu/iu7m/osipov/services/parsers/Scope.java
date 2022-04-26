package bmstu.iu7m.osipov.services.parsers;

public class Scope {
    private final String start;
    private final String end;
    private final String body;

    public Scope(String start, String end, String body){
        this.start = start;
        this.end = end;
        this.body = body;
    }

    public String getStart() {
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
