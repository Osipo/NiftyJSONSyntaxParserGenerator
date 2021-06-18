package bmstu.iu7m.osipov.unit_tests.json_parser;


import bmstu.iu7m.osipov.services.parsers.json.SimpleJsonParser;
import bmstu.iu7m.osipov.services.parsers.json.elements.JsonObject;
import bmstu.iu7m.osipov.structures.lists.LinkedStack;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        loader = AnnotationConfigContextLoader.class,
        classes = {}
)
public class SimpleJsonParserTest {

    public static final String TEST_JSON_DOCS_DIR = "C:\\Users\\IdeaProjects\\NiftyJSONCompilerGenerator\\src\\test\\resources\\json\\";

    public static SimpleJsonParser parser = new SimpleJsonParser(1024);



    //2. Parser returns only real numbers (even then we specified number).
    @Test
    public void parse_object_with_single_property() throws IOException {
        String d1 = "{ \"str\" : \"a\"}";
        String d2 = "{ \"estr\": \"\"}";
        String d3 = "{ \"num\" : 12400 }";
        String d4 = "{ \"bool\" : false }";
        String d5 = "{ \"realNum\" : 2.123 }";

        JsonObject obj = readFromString(d1);
        assert obj != null;
        assertEquals("a", obj.getProperty("str").getValue());

        obj = readFromString(d2);
        assert obj != null;
        assertEquals("", obj.getProperty("estr").getValue());

        obj = readFromString(d3);
        assert obj != null;
        assertEquals(12400.0, obj.getProperty("num").getValue());

        obj = readFromString(d4);
        assert obj != null;
        assertEquals(false, obj.getProperty("bool").getValue());
    }

    private JsonObject readFromString(String sd) throws IOException{
        InputStream src = IOUtils.toInputStream(IOUtils.toString(new StringReader(sd)), Charsets.UTF_8);
        JsonObject obj = parser.parseStream(src);
        src.close();
        return obj;
    }

    @AfterAll
    public void clearMem(){
        parser = null;
    }
}
