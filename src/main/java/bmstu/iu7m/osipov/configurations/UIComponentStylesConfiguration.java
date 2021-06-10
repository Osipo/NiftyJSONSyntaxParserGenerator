package bmstu.iu7m.osipov.configurations;

import bmstu.iu7m.osipov.ui.models.entities.UIComponent;
import bmstu.iu7m.osipov.ui.models.stores.UIComponentStore;
import javafx.event.Event;
import javafx.event.EventHandler;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;

@Configuration
public class UIComponentStylesConfiguration {


    @Bean(name = "uiComponents")
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public HashMap<String, UIComponent> getUIComponents(){
        System.out.println("uiComponents bean is created.");
        return new HashMap<>();
    }

    @Bean(name = "hdlrsMap")
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public HashMap<String, EventHandler> getEventHandlersStore(){
        System.out.println("structure for event handlers is created");
        return new HashMap<>();
    }

    @Bean(name = "rusLang")
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public HashMap<String, String> russianMap(){
        HashMap<String, String> t = new HashMap<>();
        t.put(UIComponentIds.FileMenu, "Файл");
        t.put(UIComponentIds.FileNewMenu, "Новый");
        t.put(UIComponentIds.FileNewTextFileMenu, "Текстовый файл (txt)");
        t.put(UIComponentIds.FileOpenMenu, "Открыть");
        t.put(UIComponentIds.FileCloseMenu, "Закрыть");
        t.put(UIComponentIds.FileExitMenu, "Выход");
        t.put(UIComponentIds.PreferencesMenu, "Настройки");
        t.put(UIComponentIds.PreferencesLanguagesMenu, "Языки");
        t.put(UIComponentIds.PreferencesLanguagesEngMenu, "Английский");
        t.put(UIComponentIds.PreferencesLanguagesRusMenu, "Русский");
        t.put(UIComponentIds.HelpMenu, "Помощь");
        t.put(UIComponentIds.HelpAboutMenu,"О программе");
        t.put(UIComponentIds.BottomTerminal, "Терминал");
        t.put(UIComponentIds.BottomOutput, "Окно вывода");

        t.put(UIComponentIds.SearchButton, "Поиск");
        t.put(UIComponentIds.TreeFileMenuOptionsLabel, "Искать только: ");
        t.put(UIComponentIds.TreeFileMenuOptionAll, "Всё");
        t.put(UIComponentIds.TreeFileMenuOptionFiles, "Файлы");
        t.put(UIComponentIds.TreeFileMenuOptionDirs, "Папки");

        t.put(UIComponentIds.EditorFilesSaveBtn, "Сохранить");

        //------------------------------------------
        // TreeView ContextMenu elements translation
        //------------------------------------------
        t.put(UIComponentIds.TreeViewContextMenuOpen, "Открыть");
        t.put(UIComponentIds.TreeViewContextMenuClose, "Закрыть");

        //-----------------------------------
        // File dialog components translation
        //-----------------------------------
        t.put(FileDialogText.openTitleText, "Открыть новый файл");
        t.put(FileDialogText.fileNameText, "Имя файла:");
        t.put(FileDialogText.fileTypeText, "Тип файла:");
        t.put(FileDialogText.lookInText, "Искать в папке:");
        t.put(FileDialogText.upFolderTooltipText, "На один уровень вверх");
        t.put(FileDialogText.homeFolderTooltipText, "Рабочий стол");
        t.put(FileDialogText.listViewTooltipText, "Список");
        t.put(FileDialogText.detailsTooltipText, "Детали");
        t.put(FileDialogText.newFolderTooltipText, "Создать новую папку");
        t.put(FileDialogText.cancelText, "Отмена");
        t.put(FileDialogText.openText, "Открыть");
        t.put(FileDialogText.cancelTooltipText, "Отменить диалог (не открывать файл)");
        t.put(FileDialogText.allFilterText, "Все");
        t.put(FileDialogText.jsonFilterText, "Файлы формата JSON - Нотация объектов JavaScript.");

        t.put(FileDialogText.fileNameHeaderText, "Имя");
        t.put(FileDialogText.fileTypeHeaderText, "Тип (расширение)");
        t.put(FileDialogText.fileSizeHeaderText, "Размер");
        t.put(FileDialogText.fileDateHeaderText, "Дата последнего изменения");

        return t;
    }

    @Bean(name = "engLang")
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public HashMap<String, String> englishMap(){
        HashMap<String, String> t = new HashMap<>();
        t.put(UIComponentIds.FileMenu, "_File");
        t.put(UIComponentIds.FileNewMenu, "New");
        t.put(UIComponentIds.FileNewTextFileMenu, "Text File");
        t.put(UIComponentIds.FileOpenMenu, "Open");
        t.put(UIComponentIds.FileCloseMenu, "Close");
        t.put(UIComponentIds.FileExitMenu, "Exit");
        t.put(UIComponentIds.PreferencesMenu, "_Preferences");
        t.put(UIComponentIds.PreferencesLanguagesMenu, "Languages");
        t.put(UIComponentIds.PreferencesLanguagesEngMenu, "English");
        t.put(UIComponentIds.PreferencesLanguagesRusMenu, "Russian");
        t.put(UIComponentIds.HelpMenu, "_Help");
        t.put(UIComponentIds.HelpAboutMenu,"About");
        t.put(UIComponentIds.BottomTerminal, "Terminal");
        t.put(UIComponentIds.BottomOutput, "Output");

        t.put(UIComponentIds.SearchButton, "Search");
        t.put(UIComponentIds.TreeFileMenuOptionsLabel, "Search only: ");
        t.put(UIComponentIds.TreeFileMenuOptionAll, "All");
        t.put(UIComponentIds.TreeFileMenuOptionFiles, "Files");
        t.put(UIComponentIds.TreeFileMenuOptionDirs, "Directories");

        t.put(UIComponentIds.EditorFilesSaveBtn, "Save");

        //------------------------------------------
        // TreeView ContextMenu elements translation
        //------------------------------------------
        t.put(UIComponentIds.TreeViewContextMenuOpen, "Open");
        t.put(UIComponentIds.TreeViewContextMenuClose, "Close");

        //-----------------------------------
        // File dialog components translation
        //-----------------------------------
        t.put(FileDialogText.openTitleText, "Open new file");
        t.put(FileDialogText.fileNameText, "File Name:");
        t.put(FileDialogText.fileTypeText, "Type of File:");
        t.put(FileDialogText.lookInText, "Search in:");
        t.put(FileDialogText.upFolderTooltipText, "One level up");
        t.put(FileDialogText.homeFolderTooltipText, "Desktop");
        t.put(FileDialogText.listViewTooltipText, "List");
        t.put(FileDialogText.detailsTooltipText, "Details");
        t.put(FileDialogText.newFolderTooltipText, "Create new folder");
        t.put(FileDialogText.cancelText, "Cancel");
        t.put(FileDialogText.openText, "Open");
        t.put(FileDialogText.cancelTooltipText, "Abort selection and opening of file.");
        t.put(FileDialogText.allFilterText, "All");
        t.put(FileDialogText.jsonFilterText, "JavaScript Object Notation (JSON) files");

        t.put(FileDialogText.fileNameHeaderText, "Name");
        t.put(FileDialogText.fileTypeHeaderText, "Type (extension)");
        t.put(FileDialogText.fileSizeHeaderText, "Size");
        t.put(FileDialogText.fileDateHeaderText, "Last modified");

        return t;
    }
}
