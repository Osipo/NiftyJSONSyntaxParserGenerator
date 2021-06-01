package bmstu.iu7m.osipov.configurations;

import bmstu.iu7m.osipov.ui.models.EditorModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelsConfiguration {

    @Bean(name = "editorModel")
    public EditorModel getEditorModel(){
        return new EditorModel();
    }
}
