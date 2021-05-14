package bmstu.iu7m.osipov.ui.factories;

import bmstu.iu7m.osipov.ui.builders.SpringBeanBuilder;
import javafx.util.Builder;
import javafx.util.BuilderFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

public class SpringBeanBuilderFactory implements BuilderFactory {

    private ApplicationContext springContext;

    private BuilderFactory defaultBuilderFactory;

    public SpringBeanBuilderFactory(ApplicationContext context, BuilderFactory defaultBuilderFactory){
        this.springContext = context;
        this.defaultBuilderFactory = defaultBuilderFactory;
    }

    @Override
    public Builder<?> getBuilder(Class<?> type) {
        Object o = null;
        try {
            o = springContext.getBean(type.getSimpleName(), type);
            return new SpringBeanBuilder<>(o);
        }catch (NoSuchBeanDefinitionException e){
            //System.out.println("Cannot find bean of type "+type.getTypeName());
            if(defaultBuilderFactory != null)
                return defaultBuilderFactory.getBuilder(type);
        }
        return null;
    }
}
