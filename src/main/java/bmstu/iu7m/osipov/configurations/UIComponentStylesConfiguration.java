package bmstu.iu7m.osipov.configurations;

import bmstu.iu7m.osipov.ui.models.entities.UIComponent;
import bmstu.iu7m.osipov.ui.models.stores.UIComponentStore;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.HashMap;
import java.util.HashSet;

@Configuration
public class UIComponentStylesConfiguration {


    @Bean(name = "uiComponents")
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public HashSet<UIComponent> getUIComponents(){
        HashSet<UIComponent> comps = new HashSet<>();
        System.out.println("uiComponents bean is created.");
        return new HashSet<UIComponent>();
    }

    @Bean(name = "engToRus")
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public HashMap<String, String> translateEngToRus(){
        HashMap<String, String> t = new HashMap<>();
        t.put("Search", "Поиск");
        t.put("Search only: ", "Искать только: ");
        t.put("Files", "Файлы");
        t.put("Directories", "Папки");
        t.put("All", "Всё");

        //----RootWindowController > Application menu
        t.put("_File", "Файл");
        t.put("New", "Новый");
        t.put("Open", "Открыть");
        t.put("Text File", "Текстовый файл (txt)");
        t.put("Exit", "Выход");
        t.put("_Preferences", "Настройки");
        t.put("Languages", "Языки");
        t.put("English", "Английский");
        t.put("Russian", "Русский");
        t.put("About", "О программе");
        t.put("_Help", "Помощь");

        //----RootWindowController > bottom panel buttons.
        t.put("Output", "Окно Вывода");
        t.put("Terminal", "Терминал");
        System.out.println("engToRus bean is created.");
        return t;
    }
}
