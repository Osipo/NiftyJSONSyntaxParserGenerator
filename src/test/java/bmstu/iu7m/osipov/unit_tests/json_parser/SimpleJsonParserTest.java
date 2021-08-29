package bmstu.iu7m.osipov.unit_tests.json_parser;


import bmstu.iu7m.osipov.services.parsers.json.SimpleJsonParser2;
import bmstu.iu7m.osipov.services.parsers.json.elements.JsonArray;
import bmstu.iu7m.osipov.services.parsers.json.elements.JsonBoolean;
import bmstu.iu7m.osipov.services.parsers.json.elements.JsonElement;
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
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        loader = AnnotationConfigContextLoader.class,
        classes = {}
)
public class SimpleJsonParserTest {

    public static final SimpleJsonParser2 JSON_PARSER = new SimpleJsonParser2(1024);



    @Test
    public void when_illegal_then_null() throws IOException {
        String e1= "{ \"eobj\" : {";
        String e2 = "{ \"eof\" : a";
        String e3 = "{ \"eof2\" :  ";
        JsonObject obj = readFromString(e1);
        assertNull(obj);
        obj = readFromString(e2);
        assertNull(obj);
        obj = readFromString(e3);
        assertNull(obj);
    }

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
        assertEquals(12400L, obj.getProperty("num").getValue());

        obj = readFromString(d4);
        assert obj != null;
        assertEquals(false, obj.getProperty("bool").getValue());

        obj = readFromString(d5);
        assert  obj != null;
        assertEquals(2.123, obj.getProperty("realNum").getValue());
    }

    //TODO: validate empty objects and arrays.
    @Test
    public void parse_empty_object() throws IOException {
        String de1 = "{ }";
        String de2= "{ " +
                "\"p1\" : \"str\", \"p2\" : { }"
                + " }";

        String de3 = "{" +
                "\"p1\" : { }, \"p2\" : true"
                + "}";

        String de4 = "{ \"p1\" : [ { },{},{}] }";

        JsonObject obj = readFromString(de1);

        assert obj != null;
        assertEquals(0, obj.getValue().keySet().size());


        obj = readFromString(de2);
        assert  obj != null;
        assertEquals(2, obj.getValue().keySet().size());
        assertEquals("str", obj.getProperty("p1").getValue());
        assertTrue(obj.getProperty("p2") instanceof JsonObject);
        obj = (JsonObject) obj.getProperty("p2");
        assertEquals(0, obj.getValue().keySet().size());


        obj = readFromString(de3);
        assert  obj != null;
        assertEquals(2, obj.getValue().keySet().size());
        assertTrue(obj.getProperty("p2") instanceof JsonBoolean);
        assertEquals(true, obj.getProperty("p2").getValue());
        assertTrue(obj.getProperty("p1") instanceof JsonObject);
        obj = (JsonObject) obj.getProperty("p1");
        assertEquals(0, obj.getValue().keySet().size());

        obj = readFromString(de4);
        assert obj != null;
        assertEquals(1, obj.getValue().keySet().size());
        assertTrue(obj.getProperty("p1") instanceof JsonArray);
        ArrayList<JsonElement> ar = (ArrayList<JsonElement>) obj.getProperty("p1").getValue();

        assertEquals(3, ar.size());
        assertTrue(ar.get(0) instanceof JsonObject);
        assertTrue(ar.get(1) instanceof JsonObject);
        assertTrue(ar.get(2) instanceof JsonObject);
    }

    @Test
    public void parse_empty_arrays(){

    }

    public static JsonObject readFromString(String sd) throws IOException{
        InputStream src = IOUtils.toInputStream(IOUtils.toString(new StringReader(sd)), Charsets.UTF_8);
        JsonObject obj = JSON_PARSER.parseStream(src);
        src.close();
        return obj;
    }
}
