package bmstu.iu7m.osipov.utils;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class Dialogs {
    public static Dialog<String> createFileDialog = createFileDialog();

    private static Dialog<String> createFileDialog(){
        Dialog<String> dialog = new Dialog<>();
        Label label = new Label("File name: ");
        TextField fname = new TextField();
        ButtonType btn_ok = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        ButtonType btn_cancel = ButtonType.CANCEL;
        dialog.getDialogPane().getButtonTypes().addAll(btn_ok, btn_cancel);
        GridPane root = new GridPane();
        root.setHgap(10);
        root.add(label,0,0);
        root.add(fname, 1, 0);
        dialog.getDialogPane().setContent(root);
        dialog.setResizable(true);
        root.prefWidthProperty().bind(dialog.getDialogPane().prefWidthProperty());
        fname.prefWidthProperty().bind(root.prefWidthProperty());
        dialog.getDialogPane().prefWidthProperty().bind(dialog.widthProperty());
        dialog.getDialogPane().setMinWidth(150);
        dialog.getDialogPane().setMaxHeight(200);

        dialog.setResultConverter(btn -> {
            if(btn.equals(btn_ok))
                return fname.getText();
            return null;
        });
        return dialog;
    }
}
