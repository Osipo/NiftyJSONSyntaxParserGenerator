package bmstu.iu7m.osipov;

import javafx.application.Application;
import org.springframework.boot.Banner;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;


public abstract class AbstractJavaFxApplication extends Application {

    private static String[] savedArgs;

    protected ConfigurableApplicationContext context;

    AbstractJavaFxApplication(){
        System.out.println("JavaFX Launched.");
    }

    @Override
    public void init() throws Exception {
        this.context = new SpringApplicationBuilder()
                .sources(getClass())
                .initializers(context -> {
                    Environment env = context.getEnvironment();
                    System.out.println("Application runner");
                    System.out.println("Spring config name: "+env.getProperty("spring.config.name"));
                    System.out.println("Spring current profile: "+env.getProperty("spring.profiles.active"));
                })
                .bannerMode(Banner.Mode.OFF)
                .headless(false)
                .run(savedArgs);
        context.getAutowireCapableBeanFactory().autowireBean(this);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        context.close();
    }

    //TODO: Process arguments here.
    protected static void launchApp(Class<? extends AbstractJavaFxApplication> clazz, String[] args) {
        AbstractJavaFxApplication.savedArgs = args;
        Application.launch(clazz, args);
    }
}
