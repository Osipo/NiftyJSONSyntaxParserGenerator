package bmstu.iu7m.osipov.services.files;

import bmstu.iu7m.osipov.configurations.ImageNames;
import bmstu.iu7m.osipov.configurations.ResourcesConfiguration;
import bmstu.iu7m.osipov.structures.lists.LinkedStack;
import bmstu.iu7m.osipov.ui.models.entities.DirectoryEntryItem;
import bmstu.iu7m.osipov.ui.models.entities.FileEntryItem;
import bmstu.iu7m.osipov.ui.models.entities.RegularFileEntryItem;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;

@Component("StackFileLocator")
public class StackFileLocator implements FileLocatorService {

    /**
     * Recursively finds all entries starting in parentDir location
     * @param parentDir A absolute path to the directory, from there we add content of that (all content of parentDir)
     * @return A root of the tree of file entires for specified pdir.
     */
    @Override
    public TreeItem<FileEntryItem> getAllFileEntriesFrom(String parentDir) {
        if(parentDir == null)
            return null;
        LinkedStack<TreeItem<FileEntryItem>> S = new LinkedStack<>();
        HashMap<String, Image> resImgs = ResourcesConfiguration.getImgs();

        TreeItem<FileEntryItem> root = new TreeItem<>(new DirectoryEntryItem(parentDir));
        root.getValue().setFullFileName(parentDir);
        root.setGraphic(new ImageView(resImgs.get(ImageNames.IMG_DIR)));
        S.push(root);

        TreeItem<FileEntryItem> curDir = null;
        while(!S.isEmpty()) {
            curDir = S.top();
            S.pop();
            //System.out.println(Paths.get(curDir.getValue().getFullFileName()));
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(curDir.getValue().getFullFileName()))) {
                for (Path p : stream) {
                    if (Files.isDirectory(p)) {
                        DirectoryEntryItem dentry = new DirectoryEntryItem(p.getFileName().toString());
                        dentry.setFullFileName(p.toAbsolutePath().toString());
                        //System.out.println(dentry.getFullFileName());
                        TreeItem<FileEntryItem> ndir = new TreeItem<>(dentry);
                        ndir.setGraphic(new ImageView(ImageNames.IMG_DIR));
                        curDir.getChildren().add(ndir);
                        S.push(ndir);
                    }
                    else {
                        RegularFileEntryItem fentry = new RegularFileEntryItem(p.getFileName().toString());
                        fentry.setFullFileName(p.toAbsolutePath().toString());
                        //System.out.println(fentry.getFullFileName());

                        TreeItem<FileEntryItem> nfile = new TreeItem<>(fentry);
                        nfile.setGraphic(new ImageView(ImageNames.IMG_FILE));
                        curDir.getChildren().add(nfile);
                    }
                }
            } catch (IOException e) {
                System.out.println("Cannot read path: " + curDir.getValue().getFullFileName());
            }
        }

        return root;
    }

    /**
     * Non-recursive version of method getAllFileEntriesFrom(String pdir)
     * @param pdir - Absolute path to directory, from where we add fileEntries (content of pdir only but not sub_dirs)
     * @return A root of the tree of file entires for specified pdir.
     */
    @Override
    public TreeItem<FileEntryItem> getFileEntriesIn(String pdir){
        if(pdir == null)
            return null;

        HashMap<String, Image> resImgs = ResourcesConfiguration.getImgs();
        TreeItem<FileEntryItem> root = new TreeItem<>(new DirectoryEntryItem(pdir));
        root.getValue().setFullFileName(pdir);
        root.setGraphic(new ImageView(resImgs.get(ImageNames.IMG_DIR)));
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(root.getValue().getFullFileName()))) {
            for (Path p : stream) {
                if (Files.isDirectory(p)) {
                    DirectoryEntryItem dentry = new DirectoryEntryItem(p.getFileName().toString());
                    dentry.setFullFileName(p.toAbsolutePath().toString());

                    TreeItem<FileEntryItem> ndir = new TreeItem<>(dentry);
                    ndir.setGraphic(new ImageView(ImageNames.IMG_DIR));
                    root.getChildren().add(ndir);
                }
                else {
                    RegularFileEntryItem fentry = new RegularFileEntryItem(p.getFileName().toString());
                    fentry.setFullFileName(p.toAbsolutePath().toString());

                    TreeItem<FileEntryItem> nfile = new TreeItem<>(fentry);
                    nfile.setGraphic(new ImageView(ImageNames.IMG_FILE));
                    root.getChildren().add(nfile);
                }
            }
        } catch (IOException e) {
            System.out.println("Cannot read path: " + root.getValue().getFullFileName());
        }

        return root;
    }

    @Override
    public void addEntriesTo(TreeItem<FileEntryItem> pdir){
        if(pdir == null || pdir.getValue() == null || pdir.getValue() instanceof RegularFileEntryItem)
            return;

        HashMap<String, Image> resImgs = ResourcesConfiguration.getImgs();
        String cpath = pdir.getValue().getFullFileName();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(pdir.getValue().getFullFileName()))) {
            for (Path p : stream) {
                cpath = p.toAbsolutePath().toString();
                if (Files.isDirectory(p)) {
                    DirectoryEntryItem dentry = new DirectoryEntryItem(p.getFileName().toString());
                    dentry.setFullFileName(p.toAbsolutePath().toString());

                    TreeItem<FileEntryItem> ndir = new TreeItem<>(dentry);
                    ndir.setGraphic(new ImageView(ImageNames.IMG_DIR));
                    pdir.getChildren().add(ndir);
                }
                else {
                    RegularFileEntryItem fentry = new RegularFileEntryItem(p.getFileName().toString());
                    fentry.setFullFileName(p.toAbsolutePath().toString());

                    TreeItem<FileEntryItem> nfile = new TreeItem<>(fentry);
                    nfile.setGraphic(new ImageView(ImageNames.IMG_FILE));
                    pdir.getChildren().add(nfile);
                }
            }
        } catch (IOException e){
            System.out.println("Cannot open path: "+cpath);
        }
    }


    //Example: a/b/c. until c, add content of each dir (do not apply to sub_dirs).
    @Override
    public TreeItem<FileEntryItem> getFileEntriesTo(String dest) {
        if(dest == null)
            return null;
        LinkedStack<TreeItem<FileEntryItem>> S = new LinkedStack<>();
        HashMap<String, Image> resImgs = ResourcesConfiguration.getImgs();

        String [] sdirs = dest.split("\\\\");
        int i = 0;
        if(i == sdirs.length){
            System.out.println("No dir");
            return null;
        }

        TreeItem<FileEntryItem> root = new TreeItem<>(new DirectoryEntryItem(sdirs[0]));
        root.getValue().setFullFileName(sdirs[0] + "\\");
        root.setGraphic(new ImageView(resImgs.get(ImageNames.IMG_DIR)));

        TreeItem<FileEntryItem> curDir = root;
        TreeItem<FileEntryItem> nextDir = curDir;

        while(++i < sdirs.length){
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(curDir.getValue().getFullFileName()))) {
                for (Path p : stream) {
                    if (Files.isDirectory(p)) {
                        DirectoryEntryItem dentry = new DirectoryEntryItem(p.getFileName().toString());
                        dentry.setFullFileName(p.toAbsolutePath().toString());
                        TreeItem<FileEntryItem> ndir = new TreeItem<>(dentry);
                        ndir.setGraphic(new ImageView(ImageNames.IMG_DIR));
                        //System.out.println(dentry.getFullFileName());
                        curDir.getChildren().add(ndir);
                        if(dentry.getFileName().equals(sdirs[i])){
                            nextDir = ndir;
                        }
                    }
                    else {
                        RegularFileEntryItem fentry = new RegularFileEntryItem(p.getFileName().toString());
                        fentry.setFullFileName(p.toAbsolutePath().toString());
                        //System.out.println(fentry.getFullFileName());

                        TreeItem<FileEntryItem> nfile = new TreeItem<>(fentry);
                        nfile.setGraphic(new ImageView(ImageNames.IMG_FILE));
                        curDir.getChildren().add(nfile);
                    }
                }
            } catch (IOException e) {
                System.out.println("Cannot read path: " + curDir.getValue().getFullFileName());
            }
            curDir = nextDir;
        }
        return root;
    }


}
