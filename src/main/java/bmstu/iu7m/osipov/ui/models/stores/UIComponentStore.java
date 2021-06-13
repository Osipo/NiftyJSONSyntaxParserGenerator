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
@DependsOn({"uiComponents", "rusLang", "engLang"})
public class UIComponentStore {
    private Map<String, UIComponent> components;

    private Map<String, String> eng;

    private Map<String, String> rus;

    @Autowired
    public UIComponentStore(@Qualifier("uiComponents") Map<String, UIComponent> components, @Qualifier("rusLang") Map<String, String> rus, @Qualifier("engLang") Map<String, String> eng ){
        this.components = components;
        this.eng = eng;
        this.rus = rus;
        System.out.println("UIComponentStore constructor call.");
    }

    public Map<String, UIComponent> getComponents(){
        return this.components;
    }

    public Map<String, String> toRussian(){
        return this.rus;
    }

    public Map<String, String> toEnglish(){
        return this.eng;
    }
}
