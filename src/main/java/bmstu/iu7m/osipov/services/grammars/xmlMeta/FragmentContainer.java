package bmstu.iu7m.osipov.services.grammars.xmlMeta;

import javafx.collections.*;

public class FragmentContainer {
    private String prefix;
    ObservableList<Object> children;
    public FragmentContainer(String prefix){
        this.children = FXCollections.<Object>observableArrayList();
        this.prefix = prefix;
    }

    public String getPrefix(){
        return this.prefix;
    }

    public ObservableList<Object> getChildren() {
        return children;
    }
}