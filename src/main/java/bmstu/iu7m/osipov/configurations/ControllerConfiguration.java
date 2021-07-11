package bmstu.iu7m.osipov.configurations;
import bmstu.iu7m.osipov.ui.controllers.*;
import bmstu.iu7m.osipov.ui.factories.SpringBeanBuilderFactory;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@Configuration
@DependsOn({"ModelsConfiguration_all"})
public class ControllerConfiguration {

    private SpringBeanBuilderFactory fxFactory;

    private ApplicationContext context;

    @Autowired
    public ControllerConfiguration(ApplicationContext ctx){
        this.context = ctx;
        fxFactory = new SpringBeanBuilderFactory(context, new JavaFXBuilderFactory());
        System.out.println("Config controllers was created.");
    }

    @Bean(name = ControllerBeanNames.ROOT_CTRL)
    @DependsOn({
            ControllerBeanNames.TREE_FILES_CTRL,
            ControllerBeanNames.TAB_CONSOLE_CTRL,
            ControllerBeanNames.EDITOR_CTRL,
            ControllerBeanNames.RIGHT_CTRL,
            ControllerBeanNames.TAB_OUTPUT_CTRL,
    })
    public RootWindowController getRootController() throws IOException {
        System.out.println("Load root window...");
        return loadRootView(ControllerBeanFXML.ROOT_FXML);
    }

    @Bean(name = ControllerBeanNames.TREE_FILES_CTRL)
    @ConditionalOnMissingBean(TreeFilesController.class)
    public TreeFilesController treeFilesController() throws IOException {
        return (TreeFilesController) loadController(ControllerBeanFXML.TREE_FILES_FXML, TreeFilesController.class);
    }


    @Bean(name = ControllerBeanNames.TAB_CONSOLE_CTRL)
    public ConsoleTabController consoleController() throws IOException {
        return (ConsoleTabController) loadController(ControllerBeanFXML.TAB_CONSOLE_FXML, ConsoleTabController.class);
    }

    @Bean(name = ControllerBeanNames.TAB_OUTPUT_CTRL)
    public OutputTabController outputController() throws IOException {
        return (OutputTabController) loadController(ControllerBeanFXML.TAB_OUTPUT_FXML, OutputTabController.class);
    }

    @Bean(name = ControllerBeanNames.EDITOR_CTRL)
    public EditorFilesController editorController() throws IOException {
        return (EditorFilesController) loadController(ControllerBeanFXML.EDITOR_FXML, EditorFilesController.class);
    }


    @Bean(name = ControllerBeanNames.RIGHT_CTRL)
    public RightMenuController rightController() throws IOException {
        return (RightMenuController) loadController(ControllerBeanFXML.RIGHT_MENU_FXML, RightMenuController.class);
    }



    /* Parse FXML file indicated with url, and create instance of specified Controller's type and its Parent View */
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

    /* Parse FXML and create instances of RootWindowView and its RootWindowController */
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
