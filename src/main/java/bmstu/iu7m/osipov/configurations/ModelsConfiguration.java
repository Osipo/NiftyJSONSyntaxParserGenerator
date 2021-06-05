package bmstu.iu7m.osipov.configurations;

import bmstu.iu7m.osipov.ui.models.EditorModel;
import bmstu.iu7m.osipov.ui.models.TreeFilesModel;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;;

@Configuration
@AutoConfigureAfter({ServicesConfiguration.class})
public class ModelsConfiguration {

    @Bean(name = "editorModel")
    public EditorModel getEditorModel(){
        return new EditorModel();
    }

    @Bean(name = "treeFilesModel")
    public TreeFilesModel getTreeViewModel(){
        return new TreeFilesModel();
    }
}
