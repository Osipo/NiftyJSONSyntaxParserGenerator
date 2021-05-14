package bmstu.iu7m.osipov.configurations;
import bmstu.iu7m.osipov.ui.controllers.RootWindowController;
import bmstu.iu7m.osipov.ui.controllers.TreeFilesController;
import bmstu.iu7m.osipov.ui.factories.SpringBeanBuilderFactory;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@Configuration
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
    @DependsOn({ControllerBeanNames.TREE_FILES_CTRL})
    public RootWindowController getRootController() throws IOException {
        System.out.println("Load root window...");
        return loadRootView(ControllerBeanFXML.ROOT_FXML);
    }

    @Bean(name = ControllerBeanNames.TREE_FILES_CTRL)
    @DependsOn({"imgMap", "TextFieldCellCallback"})
    @ConditionalOnMissingBean(TreeFilesController.class)
    public TreeFilesController treeFilesController() throws IOException {
        return (TreeFilesController) loadController(ControllerBeanFXML.TREE_FILES_FXML, TreeFilesController.class);
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
