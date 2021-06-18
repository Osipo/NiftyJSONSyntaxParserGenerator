package bmstu.iu7m.osipov;

import bmstu.iu7m.osipov.configurations.ControllerBeanNames;
import bmstu.iu7m.osipov.ui.controllers.OutputTabController;
import bmstu.iu7m.osipov.ui.controllers.RootWindowController;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.*;

import java.nio.file.FileSystems;

@SpringBootApplication
@Lazy
public class Main extends AbstractJavaFxApplication {

    @Value("${ui.main.title}")
    private String mTitle;


    public static String PATH_SEPARATOR = FileSystems.getDefault().getSeparator();
    public static String PATH_SEPARATOR_ESC = "\\" + FileSystems.getDefault().getSeparator();

    @Autowired
    @Qualifier(ControllerBeanNames.ROOT_CTRL)
    private RootWindowController rctrl;

    @Autowired
    @Qualifier(ControllerBeanNames.TAB_OUTPUT_CTRL)
    private OutputTabController outputTabCtrl;

    public static void main(String[] args){
        launchApp(Main.class, args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(new Scene(rctrl.getView()));
        primaryStage.setTitle(mTitle);
        if(outputTabCtrl != null){
            System.out.println("All controllers are loaded.");
        }
        if(rctrl.getView() instanceof BorderPane){ /*check that root was correctly initialized */
            System.out.println("Root container is BorderPane");
        }
        primaryStage.setResizable(true);
        primaryStage.centerOnScreen();
        System.out.println("Path Separator: "+ Main.PATH_SEPARATOR);
        primaryStage.setOnCloseRequest(e ->{
            outputTabCtrl.closeThread();
            Platform.exit();
            System.exit(0);
        });

        rctrl.initDialogs(primaryStage);
        primaryStage.show();
    }
}
