package bmstu.iu7m.osipov;

import javafx.application.Application;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;


public abstract class AbstractJavaFxApplication extends Application {

    private static String[] savedArgs;

    protected ConfigurableApplicationContext context;

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

        /* Faster than above approach but less configurable.
        SpringApplication app = new SpringApplication(getClass());
        app.setBannerMode(Banner.Mode.OFF);
        app.setHeadless(false);// we should instantiate AWT. (true = do not init AWT).
        //System.setProperty("java.awt.headless", "false"); alternative way to init AWT kit.


        context = app.run(savedArgs);// load all beans
        context.getAutowireCapableBeanFactory().autowireBean(this);
         */
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
