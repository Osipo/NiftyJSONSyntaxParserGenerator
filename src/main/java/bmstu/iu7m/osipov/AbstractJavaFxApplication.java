package bmstu.iu7m.osipov;

import javafx.application.Application;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;


public abstract class AbstractJavaFxApplication extends Application {

    private static String[] savedArgs;

    protected ConfigurableApplicationContext context;

    @Override
    public void init() throws Exception {
        SpringApplication app = new SpringApplication(getClass());
        app.setBannerMode(Banner.Mode.OFF);
        context = app.run(savedArgs);/* load all beans */
        context.getAutowireCapableBeanFactory().autowireBean(this);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        context.close();
    }


    protected static void launchApp(Class<? extends AbstractJavaFxApplication> clazz, String[] args) {
        AbstractJavaFxApplication.savedArgs = args;
        Application.launch(clazz, args);
    }
}
