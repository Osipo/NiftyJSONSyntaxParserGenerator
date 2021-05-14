package bmstu.iu7m.osipov.ui.builders;

import javafx.util.Builder;

public class SpringBeanBuilder<T> implements Builder<T> {

    private Object bean;
    public SpringBeanBuilder(Object bean){
        this.bean = bean;
    }

    @Override
    public T build() {
        return (T) bean;
    }
}
