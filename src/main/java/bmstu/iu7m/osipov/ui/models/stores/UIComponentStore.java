package bmstu.iu7m.osipov.ui.models.stores;

import bmstu.iu7m.osipov.ui.models.entities.UIComponent;
import com.codepoetics.protonpack.maps.MapStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component(value = "uiStore")
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
@DependsOn({"uiComponents", "engToRus"})
public class UIComponentStore {
    private Set<UIComponent> components;

    private Map<String, String> eng_to_rus;

    private Map<String, String> rus_to_eng;

    @Autowired
    public UIComponentStore(@Qualifier("uiComponents") Set<UIComponent> components, @Qualifier("engToRus") Map<String, String> eng_to_rus){
        this.components = components;
        this.eng_to_rus = eng_to_rus;
        this.rus_to_eng = MapStream.of(eng_to_rus).inverseMapping().collect();
        System.out.println("ui_Store is created");
    }

    public Set<UIComponent> getComponents(){
        return this.components;
    }

    public Map<String, String> englishToRussian(){
        return this.eng_to_rus;
    }

    public Map<String, String> russianToEnglish(){
        return this.rus_to_eng;
    }
}
