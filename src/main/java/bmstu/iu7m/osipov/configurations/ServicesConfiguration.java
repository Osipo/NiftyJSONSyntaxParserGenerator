package bmstu.iu7m.osipov.configurations;

import bmstu.iu7m.osipov.services.adapters.TextReaderAdapter;
import bmstu.iu7m.osipov.services.adapters.TextReaderAdapterImpl;
import bmstu.iu7m.osipov.services.files.*;
import bmstu.iu7m.osipov.services.parsers.json.JsonParserService;
import bmstu.iu7m.osipov.services.parsers.json.SimpleJsonParser2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Configuration
@DependsOn({"UIComponentStylesConfiguration_all"})
public class ServicesConfiguration {

    @Bean(name = "StackFileLocator")
    public FileLocatorService getEntryReader(){
        return new StackFileLocator();
    }

    @Bean(name = "FileSearcher")
    public FileRetrievalService getFileSearchService(){
        return new FileRetrievalServiceImpl();
    }

    @Bean(name = "ReadWriteFileProcess")
    public FileProcessService getRWFileProcessor(){
        return new ReadWriteFileProcess();
    }

    @Bean(name = "TreeFilesReader")
    public TreeFilesReaderService getTreeFilesReader(){return new TreeFilesReaderImpl();}

    @Bean(name = "textReader")
    public TextReaderAdapter getInputTextAdapter(){
        return new TextReaderAdapterImpl();
    }

    @Bean(name = "jsonParser")
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public SimpleJsonParser2 getJSONParser(){
        System.out.println("Create json parser");
        return new SimpleJsonParser2();
    }

    @Bean(name = "ServicesConfiguration_all")
    @DependsOn({"FileSearcher", "ReadWriteFileProcess", "TreeFilesReader", "textReader", "jsonParser"})
    public Object allBean(){
        System.out.println("Beans of ServicesConfiguration are created.");
        return null;
    }
}
