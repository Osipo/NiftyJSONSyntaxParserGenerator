package bmstu.iu7m.osipov.configurations;

import bmstu.iu7m.osipov.services.files.FileLocatorService;
import bmstu.iu7m.osipov.ui.controllers.RootWindowController;
import bmstu.iu7m.osipov.ui.controllers.TreeFilesController;
import bmstu.iu7m.osipov.ui.factories.SpringBeanBuilderFactory;
import bmstu.iu7m.osipov.ui.views.callbacks.TextFieldTreeCellCallback;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CountDownLatch;

@Configuration
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
    @DependsOn({ControllerBeanNames.TREE_FILES_CTRL})
    public RootWindowController getRootController() throws IOException {
        System.out.println("Load root window...");
        return loadRootView(ControllerBeanFXML.ROOT_FXML);
    }

    /* Mocking injected services */
    @MockBean
    FileLocatorService service;

    @MockBean
    private TextFieldTreeCellCallback tc_callback;

    @Bean(name = ControllerBeanNames.TREE_FILES_CTRL)
    @DependsOn({"imgMap"})
    @ConditionalOnMissingBean(TreeFilesController.class)
    public TreeFilesController treeFilesController() throws IOException {
        return (TreeFilesController) loadController(ControllerBeanFXML.TREE_FILES_FXML, TreeFilesController.class);
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
