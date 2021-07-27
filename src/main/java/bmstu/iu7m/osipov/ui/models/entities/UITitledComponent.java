package bmstu.iu7m.osipov.ui.models.entities;

import javafx.css.Styleable;
import javafx.scene.Node;
import javafx.scene.control.Labeled;

public class UITitledComponent extends UIComponent {
    private TitledUIComponent titled;
    public UITitledComponent(TitledUIComponent titled) {
        super(null);
        this.titled = titled;
    }

    public TitledUIComponent getTitled(){
        return this.titled;
    }

    @Override
    public String getType() {
        return this.titled.getClass().getSimpleName();
    }

    @Override
    public void setStyle(String style) {

    }
}
