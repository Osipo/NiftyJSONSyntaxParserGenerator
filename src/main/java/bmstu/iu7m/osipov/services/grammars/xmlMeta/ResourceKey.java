package bmstu.iu7m.osipov.services.grammars.xmlMeta;

public class ResourceKey {
    private final String key;

    private int prevState;

    public ResourceKey(String key){
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setPrevState(int prevState) {
        this.prevState = prevState;
    }

    public int getPrevState() {
        return prevState;
    }
}
