package bmstu.iu7m.osipov.ui.modals;

import bmstu.iu7m.osipov.ui.models.entities.TitledUIComponent;
import bmstu.iu7m.osipov.ui.models.stores.UIComponentStore;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public abstract class ModalWindow extends bmstu.iu7m.osipov.ui.modals.Window implements TitledUIComponent {

    public ModalWindow(UIComponentStore store){
        this(null, store);
    }

    public ModalWindow(Window parent, UIComponentStore store){
        super(parent, store);
        stage.initModality(Modality.WINDOW_MODAL); //override modality.
    }

    public void setOpened(boolean opened) {
        isOpened = opened;
    }

    public boolean isOpened(){
        return this.isOpened;
    }

    //----------------------------------
    // TitledUIComponent implementation
    //----------------------------------
    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getTitle() {
        return stage.getTitle();
    }

    @Override
    public void setTitle(String title) {
        this.stage.setTitle(title);
    }
}
