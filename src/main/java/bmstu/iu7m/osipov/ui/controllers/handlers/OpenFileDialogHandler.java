package bmstu.iu7m.osipov.ui.controllers.handlers;

import bmstu.iu7m.osipov.configurations.FileDialogText;
import bmstu.iu7m.osipov.events.OpenFileActionEvent;
import bmstu.iu7m.osipov.ui.locale.SimpleAcceptAllFileFilter;
import bmstu.iu7m.osipov.ui.locale.SimpleAcceptJsonFileFilter;
import bmstu.iu7m.osipov.ui.models.EditorModel;
import bmstu.iu7m.osipov.ui.models.entities.DirectoryEntryItem;
import com.sun.javafx.event.EventDispatchChainImpl;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.File;
import javax.swing.filechooser.FileFilter;
import java.util.concurrent.atomic.AtomicReference;

public class OpenFileDialogHandler extends OpenFileHandler implements EventHandler<ActionEvent> {

    protected Stage parent_win;
    protected AtomicReference<Boolean> dialogFlag;

    private AtomicReference<File> selFile;
    private String oldSelectedDir = null;
    private UpdateTreeViewAndOpenFileDialogHandler child; //child for firing event.

    public OpenFileDialogHandler(Stage parent_win, EditorModel editorModel){
        super(editorModel);
        this.parent_win = parent_win;
        this.dialogFlag = new AtomicReference<>(false);
        this.selFile = new AtomicReference<>();
    }

    public void setChild(UpdateTreeViewAndOpenFileDialogHandler child){
        this.child = child;
    }

    @Override
    public void handle(ActionEvent event) {
        // If have got answer from Swing JFileChooser dialog
        // AND we approve selection of the new file.
        if(event instanceof OpenFileActionEvent
                && ((OpenFileActionEvent) event).isSelected()
                && selFile.get() != null
                &&  (editorModel.getEditedFileName() == null
                    || !editorModel.getEditedFileName().equals(selFile.get().getAbsolutePath())
                    )
        )
        {

            Alert mbox = new Alert(Alert.AlertType.INFORMATION);
            mbox.setContentText("Selected: " + selFile.get().getAbsolutePath());
            mbox.show();
            editorModel.getFileContent(selFile.get());
            editorModel.setEditedFileName(selFile.get().getAbsolutePath());
        }
        // If have got answer from Swing JFileChooser dialog
        // AND we abort the dialog.
        else if(event instanceof OpenFileActionEvent
                && ((OpenFileActionEvent) event).isSelected()
        )
        {
            ((OpenFileActionEvent) event).setCanceled(true);
        }
        //dialog is not opened yet.
        //DIALOG SECTION at EDT of Swing (not JavaFX Thread)
        else if(!dialogFlag.get()) {
            dialogFlag.set(true); // set true until we finished the whole process.

            //DO NOT perform job until we got answer from Swing JFileChooser dialog.
            //(see sections above).
            if(event instanceof OpenFileActionEvent)
                ((OpenFileActionEvent) event).setCanceled(true);

            SwingUtilities.invokeLater(() -> {
                FileFilter all = new SimpleAcceptAllFileFilter();
                FileFilter json = new SimpleAcceptJsonFileFilter();

                JFileChooser fopen = new JFileChooser();
                fopen.setDialogTitle(UIManager.getString(FileDialogText.openTitleText));
                fopen.setLocale(selected_language.get().getLocale());
                fopen.setAcceptAllFileFilterUsed(false);
                fopen.addChoosableFileFilter(all);
                fopen.addChoosableFileFilter(json);

                // SET INITIAL DIRECTORY
                if (selected_item.get() != null && selected_item.get().getValue() instanceof DirectoryEntryItem) {
                    this.oldSelectedDir = selected_item.get().getValue().getFullFileName();
                    fopen.setCurrentDirectory(new File(this.oldSelectedDir));
                } else if (this.oldSelectedDir != null) {
                    fopen.setCurrentDirectory(new File(this.oldSelectedDir));
                }

                OpenFileActionEvent ecopy = new OpenFileActionEvent(
                    event.copyFor(event.getSource(), event.getTarget())
                );

                int res = fopen.showDialog(null, UIManager.getString(FileDialogText.openText));
                if (res == JFileChooser.APPROVE_OPTION) {
                    selFile.set(fopen.getSelectedFile());
                }
                ecopy.setSelected(true);
                System.out.println("got answer from dialog.");
                if(this.child != null){
                    //return to the new JavaFX-AWT Thread.
                    Platform.runLater(() -> {
                        this.child.handle(ecopy);//fire event again with answer.
                    });
                }
            });
        }
        // Dialog is still opened.
        // AND No answer from Swing JFileChooser have got yet.
        else if(event instanceof OpenFileActionEvent){
            ((OpenFileActionEvent) event).setCanceled(true);
        }
    }
}
