package bmstu.iu7m.osipov.configurations;

import bmstu.iu7m.osipov.ui.models.EditorModel;
import bmstu.iu7m.osipov.ui.models.TreeFilesModel;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;;

@Configuration
@DependsOn({"ServicesConfiguration_all"})
public class ModelsConfiguration {

    @Bean(name = "editorModel")
    public EditorModel getEditorModel(){
        return new EditorModel();
    }

    @Bean(name = "treeFilesModel")
    public TreeFilesModel getTreeViewModel(){
        return new TreeFilesModel();
    }

    @Bean(name = "ModelsConfiguration_all")
    @DependsOn({"editorModel", "treeFilesModel"})
    public Object allBean(){
        System.out.println("Beans of ModelsConfiguration are created.");
        return null;
    }
}
