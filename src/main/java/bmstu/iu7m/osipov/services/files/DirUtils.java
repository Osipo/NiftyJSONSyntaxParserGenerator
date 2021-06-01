package bmstu.iu7m.osipov.services.files;

public class DirUtils {
    public static String getSubtraction(String p1, String p2){
        int i = 0;
        String c = null;
        if(p1.length() > p2.length()){
            c = p1;
            p1 = p2;
            p2 = c;
        }
        while(p2.charAt(i) == p1.charAt(i))
            i++;
        if(i >= p1.length())
            return null;
        else if(p2.charAt(i) == '/' && i + 1 >= p1.length())
            return null;
        else if(p2.charAt(i) == '/')
            i = i + 1;
        return p2.substring(i);
    }
}
