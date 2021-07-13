package bmstu.iu7m.osipov.ui.modals;

import bmstu.iu7m.osipov.ui.models.entities.TitledUIComponent;
import bmstu.iu7m.osipov.ui.models.entities.UITitledComponent;
import bmstu.iu7m.osipov.ui.models.stores.UIComponentStore;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Window;

import java.io.*;
import java.util.Objects;

public class ImageWindow extends ModalWindow implements TitledUIComponent {

    private final ImageView imgView;

    public ImageWindow(UIComponentStore store, String id){
        this(null, store, id);
    }

    public ImageWindow(Window parent, UIComponentStore store, String id){
        super(parent, store);
        this.id = id;
        StackPane root = new StackPane();
        Scene scene = new Scene(root);

        root.prefWidthProperty().bind(scene.widthProperty());
        root.prefHeightProperty().bind(scene.heightProperty());

        // add scrollable ImageView
        this.imgView = new ImageView();
        ScrollPane scrollable_img = new ScrollPane(imgView);
        scrollable_img.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollable_img.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        root.getChildren().add(scrollable_img);
        stage.setScene(scene);
        this.uiStore.getComponents().put(this.id, new UITitledComponent(this));
        stage.setMaximized(true);
    }

    public void setImage(File f) throws FileNotFoundException {
        if(isOpened)
            return;
        InputStream io = new FileInputStream(f);
        this.imgView.setImage(new Image(io));
        this.isOpened = true;
        stage.showAndWait();
        this.isOpened = false;
        try {
            io.close();
            f.delete();
        }catch (IOException e){
            System.out.println("ImageWindow: Cannot remove temporary img file on closing.");
        }
    }
}
