package bmstu.iu7m.osipov.unit_tests.structures;

import bmstu.iu7m.osipov.configurations.ControllerConfiguration;
import bmstu.iu7m.osipov.structures.lists.LinkedStack;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        loader = AnnotationConfigContextLoader.class,
        classes = {}
)
public class LinkedStackTest {

    private static final LinkedStack<String> SUBJECT = new LinkedStack<>();

    @After
    public void flush_subject(){
        SUBJECT.clear();
    }

    @Test
    public void when_created_then_zero(){
        assertEquals(SUBJECT.size(), 0);
        assertTrue(SUBJECT.isEmpty());
        assertNull(SUBJECT.top());
    }

    @Test
    public void when_added_to_empty_then_one_and_when_removed_then_zero(){
        SUBJECT.push("item1");
        assertEquals(SUBJECT.size(), 1);
        assertFalse(SUBJECT.isEmpty());
        assertEquals("item1", SUBJECT.top());
        assertTrue(SUBJECT.contains("item1"));

        SUBJECT.pop();
        assertEquals(SUBJECT.size(), 0);
        assertTrue(SUBJECT.isEmpty());
        assertNull(SUBJECT.top());
        assertFalse(SUBJECT.contains("item1"));
    }

    @Test
    public void when_cleared_then_empty(){
        SUBJECT.addAll(Arrays.asList("item1", "item2", "item3"));
        assertTrue(SUBJECT.contains("item1"));
        assertTrue(SUBJECT.contains("item2"));
        assertTrue(SUBJECT.contains("item3"));
        assertEquals(SUBJECT.size(), 3);
        assertEquals("item3", SUBJECT.top());

        SUBJECT.clear();
        assertEquals(SUBJECT.size(), 0);
        assertTrue(SUBJECT.isEmpty());
        assertNull(SUBJECT.top());
        assertFalse(SUBJECT.contains("item1"));
        assertFalse(SUBJECT.contains("item2"));
        assertFalse(SUBJECT.contains("item3"));
    }

    @Test
    public void remove_only_value_from_top_if_exists(){
        SUBJECT.addAll(Arrays.asList("item1", "item2"));
        assertEquals(2,SUBJECT.size());
        assertTrue(SUBJECT.contains("item2"));
        assertTrue(SUBJECT.contains("item1"));
        assertEquals("item2",SUBJECT.top());

       SUBJECT.remove("item3");
       assertEquals("item2", SUBJECT.top());
       assertEquals(2,SUBJECT.size());
       assertTrue(SUBJECT.contains("item2"));
       assertTrue(SUBJECT.contains("item1"));

       SUBJECT.remove("item2");
       assertEquals("item1", SUBJECT.top());
       assertEquals(1,SUBJECT.size());
       assertFalse(SUBJECT.contains("item2"));
       assertTrue(SUBJECT.contains("item1"));
    }

    @Test
    public void when_added_null_vals_then_extended(){
        SUBJECT.addAll(Arrays.asList("item1", null, null, "item4"));
        assertEquals(4, SUBJECT.size());
        assertEquals("item4", SUBJECT.top());
        assertFalse(SUBJECT.isEmpty());
        assertTrue(SUBJECT.contains("item1"));
        assertTrue(SUBJECT.contains("item4"));
        assertTrue(SUBJECT.contains(null));
    }

    @Test
    public void when_removed_null_vals_then_reduced(){
        SUBJECT.addAll(Arrays.asList("item1", null, null, "item4"));
        assertEquals(4, SUBJECT.size());
        assertEquals("item4", SUBJECT.top());
        assertFalse(SUBJECT.isEmpty());
        assertTrue(SUBJECT.contains("item1"));
        assertTrue(SUBJECT.contains("item4"));
        assertTrue(SUBJECT.contains(null));

        SUBJECT.pop();
        assertFalse(SUBJECT.contains("item4"));

        SUBJECT.remove(null);
        assertEquals(2, SUBJECT.size());
        assertNull(SUBJECT.top());
        assertFalse(SUBJECT.isEmpty());
        assertTrue(SUBJECT.contains("item1"));

        SUBJECT.remove(null);
        assertEquals(1,SUBJECT.size());
        assertEquals("item1", SUBJECT.top());
        assertFalse(SUBJECT.isEmpty());
        assertTrue(SUBJECT.contains("item1"));
    }
}
