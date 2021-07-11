package bmstu.iu7m.osipov.ui.modals;

import bmstu.iu7m.osipov.ui.models.entities.TitledUIComponent;
import bmstu.iu7m.osipov.ui.models.stores.UIComponentStore;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public abstract class ModalWindow implements TitledUIComponent {

    protected String id;
    protected final Stage stage;
    protected UIComponentStore uiStore;
    protected boolean isOpened;

    public ModalWindow(UIComponentStore store){
        this(null, store);
    }

    public ModalWindow(Window parent, UIComponentStore store){
        this.stage = new Stage();
        this.uiStore = store;
        this.isOpened = false;
        stage.initOwner(parent);
        stage.initModality(Modality.WINDOW_MODAL);
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
