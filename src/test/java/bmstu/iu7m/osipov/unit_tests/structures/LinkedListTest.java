package bmstu.iu7m.osipov.unit_tests.structures;

import bmstu.iu7m.osipov.services.grammars.directives.SyntaxDirectedTranslation;
import bmstu.iu7m.osipov.structures.lists.LinkedList;
import bmstu.iu7m.osipov.structures.lists.LinkedStack;
import com.kitfox.svg.A;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        loader = AnnotationConfigContextLoader.class,
        classes = {}
)
public class LinkedListTest {
    private static final LinkedList<String> SUBJECT = new LinkedList<>();

    @After
    public void flush_subject(){
        SUBJECT.clear();
    }

    @Test
    public void when_created_then_zero(){
        assertEquals(SUBJECT.size(), 0);
        assertTrue(SUBJECT.isEmpty());
        assertNull(SUBJECT.get(0));
        assertNull(SUBJECT.get(1));
    }

    @Test
    public void added_from_one(){
        assertEquals(0, SUBJECT.size());
        SUBJECT.add("ITEM");
        assertEquals(1, SUBJECT.size());
        assertEquals("ITEM", SUBJECT.get(1));
        assertNull(SUBJECT.get(0));
    }

    @Test
    public void add_a_few(){
        assertEquals(0, SUBJECT.size());
        SUBJECT.add("3");
        SUBJECT.add("2");
        SUBJECT.add("1");
        assertEquals(3, SUBJECT.size());
        assertEquals("3", SUBJECT.get(1));
        assertEquals("2", SUBJECT.get(2));
        assertEquals("1", SUBJECT.get(3));
    }

    @Test
    public void add_at_beginning(){
        assertEquals(0, SUBJECT.size());
        SUBJECT.add("3");
        SUBJECT.add("2");
        SUBJECT.add("1");
        SUBJECT.add(0, "non");
        assertEquals(3, SUBJECT.size());
        assertEquals("3", SUBJECT.get(1));

        SUBJECT.add(1, "4");
        assertEquals(4, SUBJECT.size());
        assertEquals("4", SUBJECT.get(1));
        assertEquals("3", SUBJECT.get(2));
        assertEquals("2", SUBJECT.get(3));
        assertEquals("1", SUBJECT.get(4));

        SUBJECT.add(-1, "non");
        assertEquals(4, SUBJECT.size());
        assertEquals("4", SUBJECT.get(1));
    }

    @Test
    public void add_at_end(){
        assertEquals(0, SUBJECT.size());
        SUBJECT.add(0, "non");
        assertEquals(0, SUBJECT.size());

        SUBJECT.add(1, "1");
        SUBJECT.add(2,"2");
        assertEquals(2, SUBJECT.size());
        assertEquals("1", SUBJECT.get(1));
        assertEquals("2", SUBJECT.get(2));

        SUBJECT.append("3");
        assertEquals("3", SUBJECT.get(3));

        SUBJECT.add(3, "33");
        SUBJECT.append("5");
        assertEquals(5, SUBJECT.size());
        assertEquals("33", SUBJECT.get(3));
        assertEquals("3", SUBJECT.get(4));
        assertEquals("5", SUBJECT.get(5));
    }

    @Test
    public void test_arr_list(){
        ArrayList<Integer> a = new ArrayList<>();
        a.add(1);
        a.add(2);
        a.add(3);
        a.add(1, 100);
        a.forEach(x -> System.out.println(x));
    }
}
