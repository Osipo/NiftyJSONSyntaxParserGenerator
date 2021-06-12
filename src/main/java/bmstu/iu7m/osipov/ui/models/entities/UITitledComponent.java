package bmstu.iu7m.osipov.ui.models.entities;

import javafx.scene.Node;
import javafx.scene.control.Labeled;

public class UITitledComponent extends UIComponent{
    private TitledUIComponent titled;
    public UITitledComponent(TitledUIComponent titled) {
        super(null);
        this.titled = titled;
    }

    public TitledUIComponent getTitled(){
        return this.titled;
    }
}
