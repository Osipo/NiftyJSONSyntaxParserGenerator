package bmstu.iu7m.osipov.ui.models.entities;

import javafx.scene.control.MenuItem;

public class UIMenuItemComponent extends UIComponent {
    private MenuItem item;
    public UIMenuItemComponent(MenuItem item){
        super(null);
        this.item = item;
    }

    public MenuItem getItem(){
        return this.item;
    }
}
