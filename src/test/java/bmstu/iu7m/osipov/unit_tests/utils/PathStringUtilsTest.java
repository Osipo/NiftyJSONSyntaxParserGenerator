package bmstu.iu7m.osipov.unit_tests.utils;

import bmstu.iu7m.osipov.Main;
import bmstu.iu7m.osipov.exceptions.WrongOrderOfArgumentsException;
import bmstu.iu7m.osipov.utils.PathStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        loader = AnnotationConfigContextLoader.class,
        classes = {}
)
public class PathStringUtilsTest {
    @Test
    public void when_null_then_null() throws WrongOrderOfArgumentsException {
        assertNull(PathStringUtils.getSubtraction(null, ""));
        assertNull(PathStringUtils.getSubtraction("/", null));
        assertNull(PathStringUtils.getSubtraction(null, null));
    }

    @Test
    public void when_intersect_then_subtract()  throws WrongOrderOfArgumentsException {
        assertEquals("/ab", PathStringUtils.getSubtraction("p1/cc/ab", "p1/cc"));
        assertEquals("ab", PathStringUtils.getSubtraction("p1/cc/ab", "p1/cc/"));
        assertEquals("ab/cd", PathStringUtils.getSubtraction("p1/p2/ab/cd", "p1/p2/"));
        Exception ex = assertThrows(WrongOrderOfArgumentsException.class, () ->{
            PathStringUtils.getSubtraction("p1/cc", "p1/cc/ab");
        });
        assertTrue(ex.getMessage().contains("p1/cc"));
        assertTrue(ex.getMessage().contains("p1/cc/ab"));
    }

    @Test
    public void when_equals_then_empty() throws WrongOrderOfArgumentsException {
        assertEquals("", PathStringUtils.getSubtraction("", ""));
        assertEquals("", PathStringUtils.getSubtraction("p1/cc/ab", "p1/cc/ab"));
        assertEquals("",PathStringUtils.getSubtraction("p1/cc/ab/", "p1/cc/ab/"));
    }

    @Test
    public void when_not_intersect_then_returns_first() throws WrongOrderOfArgumentsException {
        String s = "ab/cd/ef";
        assertEquals(s, PathStringUtils.getSubtraction(s, "ff/gg/qq"));
        assertEquals("ff/gg/qq/11", PathStringUtils.getSubtraction("ff/gg/qq/11", s));
        Exception e = assertThrows(WrongOrderOfArgumentsException.class, () ->{
            PathStringUtils.getSubtraction(s, "ff/gg/qq/11");
        });
        assertTrue(e.getMessage().contains("ff/gg/qq/11"));
        assertTrue(e.getMessage().contains(s));
    }

    @Test
    public void when_split_then_non_empty_substrings(){
        // ab/ab/ab/ab
        String p = generatePath("ab", 4);
        List<String> dirs = PathStringUtils.splitPath(p);
        assertEquals(4, dirs.size());
        assertEquals("ab", dirs.get(0));
        assertEquals("ab", dirs.get(1));
        assertEquals("ab", dirs.get(2));
        assertEquals("ab", dirs.get(3));

        // /ab/cd/ff
        String p2 = Main.PATH_SEPARATOR + "ab" +  Main.PATH_SEPARATOR + "cd" +  Main.PATH_SEPARATOR + "ff";
        dirs = null;
        dirs = PathStringUtils.splitPath(p2);
        assertEquals(4, dirs.size());
        assertEquals(Main.PATH_SEPARATOR, dirs.get(0));
        assertEquals("ab", dirs.get(1));
        assertEquals("cd", dirs.get(2));
        assertEquals("ff", dirs.get(3));

        // /ba
        String p3 = Main.PATH_SEPARATOR + "ba";
        dirs = null;
        dirs = PathStringUtils.splitPath(p3);
        assertEquals(2, dirs.size());
        assertEquals(Main.PATH_SEPARATOR, dirs.get(0));
        assertEquals("ba", dirs.get(1));

        // cd/
        String p4 = "cd" + Main.PATH_SEPARATOR;
        dirs = null;
        dirs = PathStringUtils.splitPath(p4);
        assertEquals(1, dirs.size());
        assertEquals("cd",dirs.get(0));

        // //p1//p2/
        String p5 = Main.PATH_SEPARATOR + Main.PATH_SEPARATOR + "p1"
                + Main.PATH_SEPARATOR + Main.PATH_SEPARATOR + "p2" + Main.PATH_SEPARATOR;
        dirs = null;
        dirs = PathStringUtils.splitPath(p5);
        assertEquals(3, dirs.size());
        assertEquals(Main.PATH_SEPARATOR, dirs.get(0));
        assertEquals("p1",dirs.get(1));
        assertEquals("p2",dirs.get(2));
    }

    @Test
    public void when_split_null_or_empty_then_null(){
        assertNull(PathStringUtils.splitPath(""));
        assertNull(PathStringUtils.splitPath(null));
    }

    @Test
    public void when_only_separators_then_one_root_dir(){
        String p = Main.PATH_SEPARATOR;
        List<String> e = PathStringUtils.splitPath(p);
        assertEquals(1, e.size());
        assertEquals(Main.PATH_SEPARATOR, e.get(0));

        String p2 = Main.PATH_SEPARATOR + Main.PATH_SEPARATOR + Main.PATH_SEPARATOR;
        e = PathStringUtils.splitPath(p2);
        assertEquals(1, e.size());
        assertEquals(Main.PATH_SEPARATOR, e.get(0));
    }

    @Test
    public void when_split_do_not_include_trailings(){
        String p = "ab" + Main.PATH_SEPARATOR + "cd" + Main.PATH_SEPARATOR + "ef" + Main.PATH_SEPARATOR;
        List<String> dirs = PathStringUtils.splitPath(p);
        assertEquals(3, dirs.size());
        assertEquals("ab",dirs.get(0));
        assertEquals("cd", dirs.get(1));
        assertEquals("ef",dirs.get(2));
        dirs = null;


        // /ab/cd/ef/
        String p2 = Main.PATH_SEPARATOR + "ab" + Main.PATH_SEPARATOR + "cd" + Main.PATH_SEPARATOR + "ef" + Main.PATH_SEPARATOR;
        dirs = PathStringUtils.splitPath(p2);
        assertEquals(4, dirs.size());
        assertEquals(Main.PATH_SEPARATOR, dirs.get(0));
        assertEquals("ab",dirs.get(1));
        assertEquals("cd", dirs.get(2));
        assertEquals("ef",dirs.get(3));
    }

    @Test
    public void then_no_separators_then_no_split(){
        String same = "abdef";
        List<String> dirs = PathStringUtils.splitPath(same);
        assertEquals(1, dirs.size());
        assertEquals(same, dirs.get(0));

        String s2 = "ab" + Main.PATH_SEPARATOR;
        dirs = PathStringUtils.splitPath(s2);
        assertEquals(1, dirs.size());
        assertEquals("ab", dirs.get(0));
    }



    private static String generatePath(String pat, int times){
        StringBuilder sb = new StringBuilder();
        sb.append(pat);
        while(times-- > 1){
            sb.append(Main.PATH_SEPARATOR);
            sb.append(pat);
        }
        return sb.toString();
    }
}
