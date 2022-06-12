package bmstu.iu7m.osipov.unit_tests.structures;


import bmstu.iu7m.osipov.structures.lists.LinkedDeque;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        loader = AnnotationConfigContextLoader.class,
        classes = {}
)
public class LinkedDequeTest {
    private static final LinkedDeque<String> SUBJECT = new LinkedDeque<>();

    @After
    public void flush_subject(){
        SUBJECT.clear();
    }

    @Test
    public void empty_test(){
        assert SUBJECT.size() == 0 && SUBJECT.isEmpty();
        assert SUBJECT.top() == null && SUBJECT.tail() == null;
        assert SUBJECT.tailFrom(-1) == null && SUBJECT.topFrom(-1) == null;
        assert SUBJECT.topFrom(0) == null && SUBJECT.tailFrom(0) == null;
        assert SUBJECT.topFrom(1) == null && SUBJECT.tailFrom(1) == null;
    }

    @Test
    public void append_test(){
        SUBJECT.append("item");
        assert SUBJECT.size() == 1 && SUBJECT.top().equals("item");
        assert SUBJECT.tail().equals(SUBJECT.top());

        SUBJECT.append("2");
        assert SUBJECT.size() == 2 && SUBJECT.top().equals("item");
        assert SUBJECT.tail().equals("2");

        SUBJECT.append("3");
        assert SUBJECT.size() == 3 && SUBJECT.top().equals("item");
        assert SUBJECT.tail().equals("3");
    }

    @Test
    public void push_test(){
        SUBJECT.push("item");
        assert SUBJECT.size() == 1 && SUBJECT.top().equals("item");
        assert SUBJECT.tail().equals(SUBJECT.top());

        SUBJECT.push("2");
        assert SUBJECT.size() == 2 && SUBJECT.top().equals("2");
        assert SUBJECT.tail().equals("item");

        SUBJECT.append("3");
        assert SUBJECT.size() == 3 && SUBJECT.top().equals("2");
        assert SUBJECT.tail().equals("3"); //[2, item, 3]
    }

}
