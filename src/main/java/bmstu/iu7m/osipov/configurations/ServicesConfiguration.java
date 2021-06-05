package bmstu.iu7m.osipov.configurations;

import bmstu.iu7m.osipov.services.adapters.TextReaderAdapter;
import bmstu.iu7m.osipov.services.adapters.TextReaderAdapterImpl;
import bmstu.iu7m.osipov.services.files.*;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
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
}
