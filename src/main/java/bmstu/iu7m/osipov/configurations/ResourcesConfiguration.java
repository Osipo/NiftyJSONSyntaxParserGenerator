package bmstu.iu7m.osipov.configurations;

import bmstu.iu7m.osipov.ui.models.entities.UIComponent;
import javafx.scene.image.Image;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.core.Ordered;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

@Configuration
public class ResourcesConfiguration {

    private static HashMap<String, Image> imgs = null;
    public static HashMap<String, Image> getImgs(){
        return imgs;
    }

    @Bean(name = "imgMap")
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public HashMap<String, Image> getImages() {
        System.out.println("load collection of images");
        HashMap<String, Image> imgs = new HashMap<>();
        loadImageByURL(imgs, ImageNames.IMG_FILE);
        loadImageByURL(imgs, ImageNames.IMG_DIR);
        loadImageByURL(imgs, ImageNames.IMG_ODIR);
        loadImageByURL(imgs, ImageNames.IMG_TARGET_BTN);
        ResourcesConfiguration.imgs = imgs;
        return imgs;
    }

    @Bean(name = "ResourcesConfiguration_all")
    @DependsOn({"imgMap"})
    public Object allBean(){
        System.out.println("Beans of ResourceConfiguration are created.");
        return null;
    }

    private void loadImageByURL(HashMap<String, Image> imap, String url){
        try{
            InputStream io = getClass().getClassLoader().getResourceAsStream(url);
            Image i = new Image(io);
            io.close();
            //System.out.println("Loaded image size: "+i.getWidth()+" , "+i.getHeight());
            imap.put(url, i);
        }
        catch (FileNotFoundException e){
            System.out.println("Cannot find file for url: "+url);
        } catch (IOException | NullPointerException e) {
            System.out.println(e.getClass().getSimpleName());
        }
    }
}
