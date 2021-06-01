package bmstu.iu7m.osipov.configurations;

import bmstu.iu7m.osipov.services.files.FileLocatorService;
import bmstu.iu7m.osipov.services.files.FileRetrievalService;
import bmstu.iu7m.osipov.ui.controllers.*;
import bmstu.iu7m.osipov.ui.factories.SpringBeanBuilderFactory;
import bmstu.iu7m.osipov.ui.models.stores.UIComponentStore;
import bmstu.iu7m.osipov.ui.views.callbacks.TextFieldTreeCellCallback;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CountDownLatch;

@Configuration
@ComponentScan(basePackages = {"bmstu.iu7m.osipov.ui.stores", "bmstu.iu7m.osipov.ui"})
@Import({ServicesConfiguration.class, ModelsConfiguration.class})
@DependsOn({"uiStore", "imgMap", "hdlrsStore", "TextFieldCellCallback"})
public class ControllerConfiguration {
    private SpringBeanBuilderFactory fxFactory;

    private ApplicationContext context;

    @Autowired
    public ControllerConfiguration(ApplicationContext ctx) throws InterruptedException {
        this.context = ctx;
        fxFactory = new SpringBeanBuilderFactory(context, new JavaFXBuilderFactory());
        System.out.println("Config controllers was created.");
        final CountDownLatch latch = new CountDownLatch(1);

        /* wait until JavaFX will be initialized */
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new JFXPanel(); // initializes JavaFX environment
                latch.countDown();
            }
        });
        latch.await();

        //TODO: SET mock behavior!
    }

    @Bean(name = ControllerBeanNames.ROOT_CTRL)
    @DependsOn({
            ControllerBeanNames.TREE_FILES_CTRL,
            ControllerBeanNames.TAB_CONSOLE_CTRL,
            ControllerBeanNames.TAB_OUTPUT_CTRL,
            ControllerBeanNames.EDITOR_CTRL
    })
    public RootWindowController getRootController() throws IOException {
        System.out.println("Load root window...");
        return loadRootView(ControllerBeanFXML.ROOT_FXML);
    }

    /* Mocking injected services */
    /*
    @MockBean
    FileLocatorService service;
    @MockBean
    FileRetrievalService frservice;
     */

    @MockBean(name = "TextFieldCellCallback")
    private TextFieldTreeCellCallback tc_callback;

    @Bean(name = ControllerBeanNames.TREE_FILES_CTRL)
    //@DependsOn({"imgMap", "TextFieldCellCallback", "uiStore"})
    @ConditionalOnMissingBean(TreeFilesController.class)
    public TreeFilesController treeFilesController() throws IOException {
        return (TreeFilesController) loadController(ControllerBeanFXML.TREE_FILES_FXML, TreeFilesController.class);
    }


    @Bean(name = ControllerBeanNames.TAB_CONSOLE_CTRL)
    //@DependsOn({"uiStore"})
    public ConsoleTabController consoleController() throws IOException {
        return (ConsoleTabController) loadController(ControllerBeanFXML.TAB_CONSOLE_FXML, ConsoleTabController.class);
    }

    @Bean(name = ControllerBeanNames.TAB_OUTPUT_CTRL)
    //@DependsOn({"uiStore"})
    public OutputTabController outputController() throws IOException {
        return (OutputTabController) loadController(ControllerBeanFXML.TAB_OUTPUT_FXML, OutputTabController.class);
    }

    @Bean(name = ControllerBeanNames.EDITOR_CTRL)
    //@DependsOn({"uiStore"})
    public EditorFilesController editorController() throws IOException {
        return (EditorFilesController) loadController(ControllerBeanFXML.EDITOR_FXML, EditorFilesController.class);
    }


    protected Object loadController(String url, Class<?> type) throws IOException {
        InputStream fxmlStream = null;
        Constructor<?> ctr = null;
        try {
            ctr = type.getConstructor();
            Object cobj = ctr.newInstance();/* create object of controller */
            fxmlStream = getClass().getClassLoader().getResourceAsStream(url);
            FXMLLoader loader = new FXMLLoader();
            loader.setRoot(cobj);
            loader.setController(cobj);
            loader.setBuilderFactory(fxFactory);
            loader.load(fxmlStream);
            return cobj;
        }
        catch (NoSuchMethodException e){
            System.out.println("Type "+type.getName()+"\n has no public .ctor without params!");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } finally {
            if (fxmlStream != null) {
                fxmlStream.close();
            }
        }
        return null;
    }

    protected RootWindowController loadRootView(String url) throws IOException {
        InputStream fxmlStream = null;
        try {
            fxmlStream = getClass().getClassLoader().getResourceAsStream(url);
            FXMLLoader loader = new FXMLLoader();
            loader.setBuilderFactory(fxFactory);
            RootWindowController c = new RootWindowController();
            loader.setController(c);
            loader.load(fxmlStream);
            return c;
        } finally {
            if (fxmlStream != null) {
                fxmlStream.close();
            }
        }
    }
}
