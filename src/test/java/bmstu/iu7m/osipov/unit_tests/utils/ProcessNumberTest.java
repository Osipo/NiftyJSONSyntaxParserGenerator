package bmstu.iu7m.osipov.unit_tests.utils;

import bmstu.iu7m.osipov.utils.ProcessNumber;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        loader = AnnotationConfigContextLoader.class,
        classes = {}
)
public class ProcessNumberTest {

    //-----------------------------------------------
    // test parse(num, exp, etype, base, sign) method
    //-----------------------------------------------
    @Test
    public void when_num_empty_or_null_then_NaN(){
        assertEquals(Double.NaN, ProcessNumber.parse(null, "1", 'E', 10, 1, 1));
        assertEquals(Double.NaN, ProcessNumber.parse("", "1", 'E', 10, 1, -2));
    }

    @Test
    public void when_exp_empty_or_null_and_third_arg_is_not_valid_then_NaN(){
        assertEquals(Double.NaN, ProcessNumber.parse("12", null, 'E', 10, 1, 1));
        assertEquals(Double.NaN, ProcessNumber.parse("12", "", 'E', 10, 1, -1));
        assertEquals(Double.NaN, ProcessNumber.parse("12", null, 'e', 10, 1, 1));
        assertEquals(Double.NaN, ProcessNumber.parse("12", "", 'e', 10, 1, -1));
        assertEquals(Double.NaN, ProcessNumber.parse("12", null, 'P', 10, 1, 2));
        assertEquals(Double.NaN, ProcessNumber.parse("12", "", 'P', 10, 1, 0));
        assertEquals(Double.NaN, ProcessNumber.parse("12", null, 'p', 10, 1, -2));
        assertEquals(Double.NaN, ProcessNumber.parse("12", "", 'p', 10, 1, 1));
        assertEquals(Double.NaN, ProcessNumber.parse("12", null, 'H', 10, 1, -2));
        assertEquals(Double.NaN, ProcessNumber.parse("12", "", 'H', 10, 1, 1));
        assertEquals(Double.NaN, ProcessNumber.parse("12", null, 'h', 10, 1, -1));
        assertEquals(Double.NaN, ProcessNumber.parse("12", "", 'h', 10, 1, 2));
        assertEquals(12d, ProcessNumber.parse("12", null, 'n', 10, 1, 1));
        assertEquals(12d, ProcessNumber.parse("12", "", 'Q', 10, 1, 1));
    }

    @Test
    public void when_sign_is_zero_or_positive_then_num_is_positive(){
        assertEquals(101d, ProcessNumber.parse("101", null, 'N', 10, 1, 1));
        assertEquals(101d, ProcessNumber.parse("101", null, 'N', 10, 0, 1));
        assertEquals(101d, ProcessNumber.parse("101", null, 'N', 10, 2, 1));
        assertEquals(101d, ProcessNumber.parse("101", null, 'N', 10, 100, 1));
    }

    @Test
    public void when_sign_is_negative_then_num_is_negative(){
        assertEquals(-101d, ProcessNumber.parse("101", null, 'N', 10, -1, 1));
        assertEquals(-101d, ProcessNumber.parse("101", null, 'N', 10, -2, 1));
        assertEquals(-101d, ProcessNumber.parse("101", null, 'N', 10, -10, 1));
        assertEquals(-101d, ProcessNumber.parse("10.1", "1", 'E', 10, -100, 1));
    }

    @Test
    public void when_base_is_not_valid_then_NaN(){

        //check that NaN when not supported base is passed.
        assertEquals(122.0d, ProcessNumber.parse("101", null, 'N', 11, 1, 1));
        assertEquals(Double.NaN, ProcessNumber.parse("10.21", "2", 'e', -1, 1, 1));

        assertEquals(Double.NaN, ProcessNumber.parse("FFF", null, 'N', 10, 1, 1));
        assertEquals(Double.NaN, ProcessNumber.parse("222", null, 'N', 2, 1, 1));
        assertEquals(Double.NaN, ProcessNumber.parse("77888", null, 'N', 8, 1, -12));
        assertEquals(Double.NaN, ProcessNumber.parse("FGGH", null, 'N', 16, 1, 1));
    }

    @Test
    public void when_valid_bin_then_num(){
        assertEquals(7d, ProcessNumber.parse("111", null, 'N', 2, 1, -1));
        assertEquals(-7d, ProcessNumber.parse("111", null, 'N', 2, -1, 1));
        assertEquals(0.0, ProcessNumber.parse("0", null, 'N', 2, 1, 1));
    }

    @Test
    public void when_valid_octal_then_num(){
        assertEquals(10d, ProcessNumber.parse("12", null, 'N', 8, 1, 1));
        assertEquals(-10d, ProcessNumber.parse("12", null, 'N', 8, -1, -1));
        assertEquals(-12.5d, ProcessNumber.parse("1.2", "1", 'E', 8, -1, 2));
        assertEquals(60.0, ProcessNumber.parse("17", "2", 'p', 8, 1, 1));
    }

    @Test
    public void when_zero_then_zero(){
        assertEquals(0.0, ProcessNumber.parse("0", "2", 'H', 10, 1, 1));
        assertEquals(0.0, ProcessNumber.parse("00", "2", 'H', 10, 1, 1));
        assertEquals(0.0, ProcessNumber.parse("0000", "2", 'H', 10, 1, 1));
        assertEquals(1000.0, ProcessNumber.parse("0010", "2", 'H', 10, 1, 1));
    }

    //-----------------------------------------------
    // test parseNumber(strNumber) method
    //-----------------------------------------------

    @Test
    public void when_valid_num_then_num(){
        assertEquals(5420d, ProcessNumber.parseNumber("5420"));
        assertEquals(5420d, ProcessNumber.parseNumber("542E1"));
        assertEquals(5420d, ProcessNumber.parseNumber("542e1"));
        assertEquals(54200d, ProcessNumber.parseNumber("542E2"));
        assertEquals(54200d, ProcessNumber.parseNumber("542e2"));
        assertEquals(54200d, ProcessNumber.parseNumber("542H2"));
        assertEquals(54200d, ProcessNumber.parseNumber("542h2"));
        assertEquals(-5420d, ProcessNumber.parseNumber("-5420"));
        assertEquals(-54.22d, ProcessNumber.parseNumber("-54.22"));
    }

    @Test
    public void when_exp_negative_then_divide(){
        assertEquals(1d, ProcessNumber.parseNumber("1000.0E-3"));
    }

    @Test
    public void when_illegal_type_or_num_then_NaN(){
        assertEquals(Double.NaN, ProcessNumber.parseNumber(null, null));
        assertEquals(Double.NaN, ProcessNumber.parseNumber(null, String.class));
        assertEquals(Double.NaN, ProcessNumber.parseNumber("", String.class));
        assertEquals(Double.NaN, ProcessNumber.parseNumber("", int.class));
        assertEquals(Double.NaN, ProcessNumber.parseNumber("", null));

        assertEquals(Double.NaN, ProcessNumber.parseNumber("231", null));
        assertEquals(Double.NaN, ProcessNumber.parseNumber("1000", String.class));
    }

    @Test
    public void when_type_dismatched_when_error(){
        assertNotEquals(123, ProcessNumber.parseNumber("123", short.class));
        assertNotEquals(123L, ProcessNumber.parseNumber("123", int.class));
        assertNotEquals(123L, ProcessNumber.parseNumber("123", Integer.class));
        assertNotEquals(124F, ProcessNumber.parseNumber("124", Double.class));
        assertNotEquals(0.124, ProcessNumber.parseNumber("0.124", Float.class));
    }

    @Test
    public void when_valid_number_with_type_int_then_int(){
        assertEquals(123, ProcessNumber.parseNumber("123", int.class));
        assertEquals(123, ProcessNumber.parseNumber("123", Integer.class));
        assertEquals((byte)127, ProcessNumber.parseNumber("127", byte.class));
        assertEquals((byte)127, ProcessNumber.parseNumber("127", Byte.class));
        assertEquals(10000L, ProcessNumber.parseNumber("10000", Long.class));
        assertEquals(1.24f, ProcessNumber.parseNumber("1.24", Float.class));
        assertEquals(0.00001, ProcessNumber.parseNumber("0.00001", Double.class));
    }

    @Test
    public void when_valid_long_number_then_long(){
        assertEquals(Long.MAX_VALUE, ProcessNumber.parseNumber("MAX_VALUE", Long.class));
        assertEquals(Long.MAX_VALUE, ProcessNumber.parseNumber("MAX_VALUE", long.class));
        assertEquals(0x8fffffffL, ProcessNumber.parseNumber("0x8fffffff", Long.class));

        //problem when Inf and type int => 0 (as type is not float or double).
        assertEquals(0, ProcessNumber.parseNumber("INFINITY", Integer.class));
        assertTrue(Integer.class.getSuperclass().isAssignableFrom(Number.class));

        System.out.printf("%.2f :: %.2f\n", (1/0.22), (1 / (1/0.22)) );
    }

    @Test
    public void when_primitive_then_no_super(){
        assertNull(float.class.getSuperclass());
        assertNotNull(Float.class.getSuperclass());
    }
}
