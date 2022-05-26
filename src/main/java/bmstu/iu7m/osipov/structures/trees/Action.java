package bmstu.iu7m.osipov.structures.trees;

public interface Action<T> {
    void perform(T arg);

    public static <T> Action<T> getActionWithNextItr(Action2<T, VisitorsNextIteration> act, VisitorsNextIteration arg2){
        return (arg1) -> act.perform(arg1, arg2);
    }
}
