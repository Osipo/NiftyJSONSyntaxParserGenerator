package bmstu.iu7m.osipov.unit_tests.json_parser;

import bmstu.iu7m.osipov.Main;
import bmstu.iu7m.osipov.configurations.PathStrings;
import bmstu.iu7m.osipov.services.parsers.json.elements.JsonObject;
import bmstu.iu7m.osipov.services.parsers.json.meta.JsonDocumentDescriptor;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.io.File;
import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        loader = AnnotationConfigContextLoader.class,
        classes = {}
)
public class JsonDescriptorTest {

    @Test
    public void parse_object_with_single_property() throws IOException {
        JsonDocumentDescriptor D = new JsonDocumentDescriptor();
        String d1 = "{ \"p1\" : 12, \"p2\" : \"google\"}";
        JsonObject ob1 = SimpleJsonParserTest.readFromString(d1);
        assert ob1 != null;
        D.describe2(ob1);
        System.out.println(D);
    }

    @Test
    public void test_merge() throws IOException {
        String d1 = "{ \"p1\" : 12, \"p2\" : null }";
        String d2 = "{ \"p3\" : \"google\", \"p1\" : false }";

        JsonObject ob1 = SimpleJsonParserTest.readFromString(d1);
        JsonObject ob2 = SimpleJsonParserTest.readFromString(d2);
        assert ob1 != null;
        assert ob2 != null;
        JsonDocumentDescriptor D = new JsonDocumentDescriptor();
        JsonDocumentDescriptor D2 = new JsonDocumentDescriptor();
        D.describe2(ob1);
        D2.describe2(ob2);

        D.merge(D2);
        System.out.println(D);
    }

    @Test
    public void parse_file(){
        JsonDocumentDescriptor D = new JsonDocumentDescriptor();
        String fname = PathStrings.TEST_JSON_DOCS_DIR + "SomeData.json";
        System.out.println(fname);
        JsonObject ob = SimpleJsonParserTest.parser.parse(new File(fname));
        assert ob != null;
        D.describe2(ob);
        System.out.println(D);
    }
}
