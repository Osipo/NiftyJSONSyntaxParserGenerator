package bmstu.iu7m.osipov.ui_tests;

import bmstu.iu7m.osipov.configurations.ControllerBeanNames;
import bmstu.iu7m.osipov.configurations.ControllerConfiguration;
import bmstu.iu7m.osipov.configurations.ResourcesConfiguration;
import bmstu.iu7m.osipov.services.files.FileLocatorService;
import bmstu.iu7m.osipov.ui.controllers.RootWindowController;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static org.junit.jupiter.api.Assertions.assertNotNull;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        loader = AnnotationConfigContextLoader.class,
        classes = {ResourcesConfiguration.class, ControllerConfiguration.class}
)
public class RootWindowIntegrationTest {

    @Autowired
    @Qualifier(ControllerBeanNames.ROOT_CTRL)
    private RootWindowController rootWindow;

    @Test
    public void when_started_then_rootWindow_is_initialized(){
        assertNotNull(rootWindow);
    }
}
