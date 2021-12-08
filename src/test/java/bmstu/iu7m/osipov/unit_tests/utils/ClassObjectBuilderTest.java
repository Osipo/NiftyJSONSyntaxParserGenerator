package bmstu.iu7m.osipov.unit_tests.utils;

import bmstu.iu7m.osipov.utils.ClassObjectBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        loader = AnnotationConfigContextLoader.class,
        classes = {}
)
public class ClassObjectBuilderTest {
    @Test
    public void when_method_or_name_is_null_then_null(){
        Method m = null;
        m = ClassObjectBuilder.getDeclaredMethod(null, null);
        assertNull(m);
        m = ClassObjectBuilder.getDeclaredMethod(null, "");
        assertNull(m);
        m = ClassObjectBuilder.getDeclaredMethod(null, "aaa");
        assertNull(m);
        m = ClassObjectBuilder.getDeclaredMethod(new Object(), null);
        assertNull(m);
        m = ClassObjectBuilder.getDeclaredMethod(new Object(), "");
        assertNull(m);
        m = ClassObjectBuilder.getDeclaredMethod(new Object(), "1111");
        assertNull(m);
    }

    @Test
    public void when_method_equals_ignore_case_then_method(){
        Method m = null;
        m = ClassObjectBuilder.getDeclaredMethod(new Object(), "tostring");
        assert m != null;
        m = ClassObjectBuilder.getDeclaredMethod(new Object(), "toString");
        assert m != null;
        m = ClassObjectBuilder.getDeclaredMethod(new Object(), "toSTRING");
        assert m != null;

        assert m.getName().equals("toString");
    }
}
