package bmstu.iu7m.osipov.structures.trees;

public class ChainedAction<T> implements Action<T> {

    private Action<T> prevAct;

    public ChainedAction(Action<T> act){
        this.prevAct = act;
    }

    @Override
    public void perform(T arg) {
        prevAct.perform(arg);
    }
}
