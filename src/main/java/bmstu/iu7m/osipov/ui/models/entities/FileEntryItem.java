package bmstu.iu7m.osipov.ui.models.entities;

import javafx.beans.NamedArg;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FileEntryItem {
    protected StringProperty fileName;
    protected StringProperty fullFileName;
    protected BooleanProperty selected;
    protected BooleanProperty opened;
    protected boolean isDir;

    public FileEntryItem(@NamedArg("fname") String fname){
        this.fileName = new SimpleStringProperty(this,"fileName", fname);
        this.fullFileName = new SimpleStringProperty(this,"fullFileName", null);
        this.selected = new SimpleBooleanProperty(this,"selected",false);
        this.opened = new SimpleBooleanProperty(this,"opened",false);
        this.isDir = false;
    }

    @Override
    public String toString(){
        return this.fileName.get();
    }

    public boolean isDirectory(){
        return isDir;
    }

    public final void setFileName(String fname){
        this.fileName.set(fname);
    }

    public final String getFileName(){
        return this.fileName.get();
    }

    public final void setFullFileName(String fullFileName){
        this.fullFileName.set(fullFileName);
    }

    public final String getFullFileName(){
        return this.fullFileName.get();
    }


    public final  void setSelected(boolean val){
        this.selected.set(val);
    }

    public final boolean getSelected(){
        return this.selected.get();
    }

    public final void setOpened(boolean val){
        this.opened.set(val);
    }

    public final boolean getOpened(){
        return this.opened.get();
    }

    public BooleanProperty selectedProperty(){
        return selected;
    }

    public BooleanProperty openedProperty(){
        return opened;
    }

    public StringProperty fileNameProperty(){
        return fileName;
    }

    public StringProperty fullFileNameProperty(){return fullFileName;}
}
