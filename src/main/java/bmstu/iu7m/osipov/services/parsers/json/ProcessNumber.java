package bmstu.iu7m.osipov.services.parsers.json;

//Process octal,hex,binary,decimal scientific literals to decimal number.
public class ProcessNumber {

    public static double parse(String num, String e, char c, int base, int sign){//c - type of exponent.
        if(num == null || num.length() == 0)
            return Double.NaN;

        double me = 1;
        if(sign < 0)
            sign = -1;
        else
            sign = 1;

        if((c == 'P' || c == 'p') && e != null && e.length() > 0)//binary exponent.
            me = Math.pow(2.0, Double.parseDouble(e));
        else if((c == 'E' || c == 'e' || c == 'H' || c == 'h') && e != null && e.length() > 0)//H and h are exponent for hex numbers.
            me = Math.pow(10.0, Double.parseDouble(e));
        else if(c == 'E' || c == 'e' || c == 'H' || c == 'h' || c == 'P' || c == 'p'){ // e == null or e.length() == 0
            return Double.NaN;
        }
        else // c is not [PpEeHh] => ignore exponent (exp == 1)
            me = 1;
        if(base == 16)
            return parse16(num) * me * sign;
        else if(base == 8)
            return parse8(num) * me * sign;
        else if(base == 2)
            return parse2(num) * me * sign;
        else if(base == 10)
            return parse10(num) * me * sign;
        return Double.NaN;
    }

    private static double parse16(String hex){
        String digits = "0123456789ABCDEF";
        hex = hex.toUpperCase();
        double val = 0;
        int i = 0;
        while(i < hex.length())
        {
            char c = hex.charAt(i);
            if(c == '.') {
                i++;
                break;
            }
            int d = digits.indexOf(c);
            if(d == -1)
                return Double.NaN;
            val = 16 * val + d;
            i++;
        }
        int power = 1;
        while(i < hex.length()){ // read part after '.' symbol
            char c = hex.charAt(i);
            int d = digits.indexOf(c);
            if(d == -1)
                return Double.NaN;
            power *= 16;
            val = 16 * val + d;
            i++;
        }
        return val / power;
    }

    private static double parse8(String num){
        String digits = "01234567";
        num = num.toUpperCase();
        double val = 0;
        int i = 0;
        while(i < num.length())
        {
            char c = num.charAt(i);
            if(c == '.') {
                i++;
                break;
            }
            int d = digits.indexOf(c);
            if(d == -1)
                return Double.NaN;
            val = 8 * val + d;
            i++;
        }
        int power = 1;
        while(i < num.length()){
            char c = num.charAt(i);
            int d = digits.indexOf(c);
            if(d == -1)
                return Double.NaN;
            power *= 8;
            val = 8 * val + d;
            i++;
        }
        return val / power;
    }
    private static double parse2(String num){
        String digits = "01";
        num = num.toUpperCase();
        double val = 0;
        int i = 0;
        while(i < num.length())
        {
            char c = num.charAt(i);
            if(c == '.') {
                i++;
                break;
            }
            int d = digits.indexOf(c);
            if(d == -1)
                return Double.NaN;
            val = 2 * val + d;
            i++;
        }
        int power = 1;
        while(i < num.length()){
            char c = num.charAt(i);
            int d = digits.indexOf(c);
            if(d == -1)
                return Double.NaN;
            power *= 2;
            val = 2 * val + d;
            i++;
        }
        return val / power;
    }

    private static double parse10(String num){
        String digits = "0123456789";
        num = num.toUpperCase();
        double val = 0;
        int i = 0;
        while(i < num.length())
        {
            char c = num.charAt(i);
            if(c == '.') {
                i++;
                break;
            }
            int d = digits.indexOf(c);
            if(d == -1)
                return Double.NaN;
            val = 10 * val + d;
            i++;
        }
        int power = 1;
        while(i < num.length()){
            char c = num.charAt(i);
            int d = digits.indexOf(c);
            if(d == -1)
                return Double.NaN;
            power *= 10;
            val = 10 * val + d;
            i++;
        }
        return val / power;
    }


    //Shortened version of parse method, where other 4 arguments (exp, type_of_exp, base, sign)
    //are computed by num string.
    //This method returns NaN if str throws NumberFormatException
    //For example, str like '-0x' or '0b01P' are illegal (because the former does not contain digits, the latter has no digits after exponent symbol)
    //Exponent symbol requires digits (E1 or E10 or P-1).
    public static double parseNumber(String str){
        if(str == null || str.length() == 0)
            return Double.NaN;
        int sign = 1;
        int base = 10;
        int i = 0;
        if(str.charAt(0) == '-') {
            sign = -1;
            i = 1;
        }
        if(i > 0 && i == str.length()) //str is '-'
            return Double.NaN;

        // suffix '0x' => 16 (hex)
        if(str.charAt(i) == '0' && (i + 1 != str.length()) && str.charAt(i + 1) == 'x') {
            base = 16;
            i += 2;
        }
        //suffix '0b' => 2 (binary)
        else if(str.charAt(i) == '0' && (i + 1 != str.length()) && str.charAt(i + 1) == 'b') {
            base = 2;
            i += 2;
        }
        //suffix '0c' => 8 (octal)
        else if(str.charAt(i) == '0' && (i + 1 != str.length()) && str.charAt(i + 1) == 'c'){
            base = 8;
            i += 2;
        }
        if(i == str.length())// str does not contains digits but '-suffix' or 'suffix' (-0x -0b -0c 0x 0b 0c)
            return Double.NaN;

        //compute exponent type.
        int idx = str.indexOf('H');
        idx = (idx == -1) ? str.indexOf('h') : idx;
        idx = (idx == -1) ? str.indexOf('P') : idx;
        idx = (idx == -1) ? str.indexOf('p') : idx;
        idx = (idx == -1 && base != 16) ? str.indexOf('E') : idx;
        idx = (idx == -1 && base != 16) ? str.indexOf('e') : idx;

        char etype = (idx == -1) ? 'N' : str.charAt(idx);

        if(idx + 1 == str.length())// no more digits after exponent letter ('12E' or 'FFP')
            return Double.NaN;

        String exp = (idx == -1) ? null : str.substring(idx + 1);
        idx = (idx == -1) ? str.length() : idx; //if no exponent then idx is the length of the str.

        String number = str.substring(i, idx);
        return parse(number, exp, etype, base, sign);
    }
}
