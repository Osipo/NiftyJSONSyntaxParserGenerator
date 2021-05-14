package bmstu.iu7m.osipov.ui.models;

import bmstu.iu7m.osipov.ui.views.IView;

public interface IModel {
    public void setView(IView view);
    public void updateView();
}