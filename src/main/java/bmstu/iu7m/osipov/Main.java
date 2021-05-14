package bmstu.iu7m.osipov;
import bmstu.iu7m.osipov.configurations.ControllerBeanNames;
import bmstu.iu7m.osipov.ui.controllers.RootWindowController;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Lazy;


@Lazy
@SpringBootApplication
public class Main extends AbstractJavaFxApplication{

    @Value("${ui.main.title}")
    private String mTitle;

    @Autowired
    @Qualifier(ControllerBeanNames.ROOT_CTRL)
    private RootWindowController rctrl;

    public static void main(String[] args){
        launchApp(Main.class, args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(new Scene(rctrl.getView()));
        primaryStage.setTitle(mTitle);
        if(rctrl.getView() instanceof BorderPane){ /*check that root was correctly initialized */
            System.out.println("Root container is BorderPane");
        }
        primaryStage.setResizable(true);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }
}
