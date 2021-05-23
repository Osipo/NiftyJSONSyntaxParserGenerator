package bmstu.iu7m.osipov.services.files;

import bmstu.iu7m.osipov.structures.lists.LinkedStack;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class FileRetrievalServiceImpl implements FileRetrievalService {
    @Override
    public String findEntry(String parent, String type, String fname) {
        if(parent == null || type == null || fname == null)
            return null;

        FileType t = FileType.valueOf(type);
        String cur_dir = parent;
        String actual = parent;
        int slash_idx = -1;
        LinkedStack<String> dirs = new LinkedStack<>();
        dirs.push(parent);
        if(Files.isDirectory(Paths.get(parent))){
            while(!dirs.isEmpty()) {
                cur_dir = dirs.top();
                dirs.pop();
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(cur_dir))) {
                    for (Path path : stream) {
                        if(Files.isDirectory(path) && (t == FileType.Directories || t == FileType.All) ){
                            slash_idx = path.toAbsolutePath().toString().lastIndexOf('/');
                            slash_idx = (slash_idx == -1) ? path.toAbsolutePath().toString().lastIndexOf('\\') : slash_idx;
                            actual = path.toAbsolutePath().toString().substring(slash_idx + 1);
                            if(actual.equals(fname))
                                return path.toAbsolutePath().toString();
                        }
                        else if(!Files.isDirectory(path) && (t == FileType.Files || t == FileType.All)  ){
                            slash_idx = path.toAbsolutePath().toString().lastIndexOf('/');
                            slash_idx = (slash_idx == -1) ? path.toAbsolutePath().toString().lastIndexOf('\\') : slash_idx;
                            actual = path.toAbsolutePath().toString().substring(slash_idx + 1);
                            if(actual.equals(fname))
                                return path.toAbsolutePath().toString();
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Cannot open file: " + cur_dir);
                }
            }
        }
        //ELSE parent dir is file and we search file
        else if(t == FileType.All || t == FileType.Files){
            slash_idx = parent.lastIndexOf('/');
            slash_idx = (slash_idx == -1) ? parent.lastIndexOf('\\') : slash_idx;
            actual = parent.substring(slash_idx + 1);
            if(actual.equals(fname))
                return parent;
        }
        return null;
    }
}
