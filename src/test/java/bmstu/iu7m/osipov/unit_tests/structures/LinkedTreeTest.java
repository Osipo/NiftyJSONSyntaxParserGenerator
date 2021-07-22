package bmstu.iu7m.osipov.unit_tests.structures;

import bmstu.iu7m.osipov.structures.trees.LinkedNode;
import bmstu.iu7m.osipov.structures.trees.LinkedTree;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        loader = AnnotationConfigContextLoader.class,
        classes = {}
)
public class LinkedTreeTest {
    /*

    private static final LinkedTree<String> SUBJECT = new LinkedTree<>();

    @After
    public void flush_subject(){
        SUBJECT.clear();
    }

    @Test
    public void when_created_then_zero(){
        assertEquals(1, SUBJECT.getCount()); // only one root node.
        assertNotNull(SUBJECT.root());
        assertEquals(0, SUBJECT.getChildren(SUBJECT.root()).size());
    }

    @Test
    public void when_added_then_new_child_and_level(){
        SUBJECT.add("item1");
        assertEquals(2, SUBJECT.getCount());
        assertEquals(1, SUBJECT.getChildren(SUBJECT.root()).size() );
        assertNotNull(SUBJECT.getChildren(SUBJECT.root()).get(0));
        assertEquals("item1", SUBJECT.getChildren(SUBJECT.root()).get(0).getValue());

        SUBJECT.add("item2");
        assertEquals(3, SUBJECT.getCount());
        assertNotNull(SUBJECT.getChildren(SUBJECT.getChildren(SUBJECT.root()).get(0) ));
        assertEquals(1, SUBJECT.getChildren(SUBJECT.getChildren(SUBJECT.root()).get(0) ).size() );
        assertEquals("item2", SUBJECT.getChildren(SUBJECT.getChildren(SUBJECT.root()).get(0) ).get(0).getValue());
    }

    @Test
    public void when_addedTo_then_list_extended(){
        SUBJECT.add("root_c1");//new child of the root.
        SUBJECT.addTo(SUBJECT.root(), "root_c2");
        assertEquals(3, SUBJECT.getCount());
        assertEquals(2, SUBJECT.getChildren(SUBJECT.root()).size());
        assertEquals("root_c2", SUBJECT.getChildren(SUBJECT.root()).get(1).getValue());
    }
     */

    @Test
    public void empty_test(){

    }
}