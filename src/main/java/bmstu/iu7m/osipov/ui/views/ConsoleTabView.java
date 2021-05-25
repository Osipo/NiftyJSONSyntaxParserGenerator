package bmstu.iu7m.osipov.ui.views;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class ConsoleTabView extends VBox {
    public ConsoleTabView(){
        System.out.println("ConsoleTabView: constructor");
    }


    protected void initView(){
        System.out.println("ConsoleTabView > initView()");
        BackgroundFill bF = new BackgroundFill(Color.BLUE, new CornerRadii(1), null);
        this.setBackground(new Background(bF));
    }
}
