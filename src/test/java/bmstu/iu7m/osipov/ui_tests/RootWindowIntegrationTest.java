package bmstu.iu7m.osipov.ui_tests;

import bmstu.iu7m.osipov.configurations.ControllerBeanNames;
import bmstu.iu7m.osipov.configurations.ControllerConfiguration;
import bmstu.iu7m.osipov.ui.controllers.RootWindowController;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static org.junit.jupiter.api.Assertions.assertNotNull;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        loader = AnnotationConfigContextLoader.class,
        classes = {ControllerConfiguration.class}
)
public class RootWindowIntegrationTest {

    @Autowired
    @Qualifier(ControllerBeanNames.ROOT_CTRL)
    private RootWindowController rootWindow;

    @BeforeClass
    public static void initAWT(){
        System.setProperty("java.awt.headless", "false");
        System.out.println("Headless of AWT set to false");
    }

    @Test
    public void when_started_then_rootWindow_is_initialized(){
        assertNotNull(rootWindow);
    }
}
