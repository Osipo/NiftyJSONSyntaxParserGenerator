package bmstu.iu7m.osipov.utils;

import bmstu.iu7m.osipov.Main;
import bmstu.iu7m.osipov.exceptions.WrongOrderOfArgumentsException;

import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PathStringUtils {

    //Subtract prefix substring p2 from string s1.
    public static String getSubtraction(String p1, String p2) throws WrongOrderOfArgumentsException {
        if(p1 == null || p2 == null)
            return null;
        int i = 0;
        String c = null;
        if(p2.length() > p1.length()){
            throw new WrongOrderOfArgumentsException("Arguments have wrong order\n\t " +
                    "at PathStringUtils.getSubtraction():\n\t" +
                    " expected: (\"" + p2 + "\" , \""+ p1+"\") but found: (\"" + p1 + "\" , \"" + p2+"\")\n");
        }
        while(i < p1.length() && i < p2.length() && p1.charAt(i) == p2.charAt(i))
            i++;
        if(i == p1.length())
            return "";
        return p1.substring(i);
    }

    public static String truncatePath(String p, int limit){
        if(p == null || p.length() == 0 || limit <= 0)
            return p;
        String sep = FileSystems.getDefault().getSeparator();
        List<String> paths = splitPath(replaceSeparator(p), sep);
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < paths.size() - limit; i++) {
            if (i > 0)
                sb.append(sep);
            sb.append(paths.get(i));
        }
        return sb.toString();
    }

    public static String getUnion(String s1, String s2){
        if(s1 == null || s2 == null)
            return null;
        int i = 0;
        String c = null;
        if(s1.length() < s2.length()){
            c = s1;
            s1 = s2;
            s2 = c;
        }
        while(i < s1.length() && i < s2.length() && s1.charAt(i) == s2.charAt(i))
            i++;
        if(i == 0)
            return "";
        int l = tilIndexOf(s1, Main.PATH_SEPARATOR.charAt(0), i - 1);//find first uncommon directory.
        return s1.substring(0, l);
    }

    /** Works like lastIndexOf except that it finds last presence
     * of symbol at src which index is not greater than toIndex.
     * @param src Source string where symbol is being seeked
     * @param symbol symbol to find
     * @param toIndex the right bounder of seeking.
     */
    public static int tilIndexOf(String src, char symbol, int toIndex){
        if(src == null || toIndex < 0)
            return -1;
        int r = 0;
        for(int i = 0; i < src.length() && i <= toIndex; i++){
            if(src.charAt(i) == symbol)
                r = i;
        }
        return r;
    }

    public static List<String> splitPath(String path){
        return splitPath(path, Main.PATH_SEPARATOR);
    }

    //Split path to sub_directories.
    //Returns null for null args or empty-strings.
    //Special root directory '/' is included as first substring if presented.
    //Examples: a/b/c => [a, b, c]
    //  /oko => [/, oko]
    // /a/b/ => [/, a, b]
    // ///a//b/c/de/ => [/, a, b, c, de].
    public static List<String> splitPath(String path, String separator){
        if(path == null || path.length() == 0 || separator == null)
            return null;
        List<String> res = new ArrayList<>();

        //include root directory (for UNIX systems)
        if(path.indexOf(separator) == 0){
            res.add(separator);
            path = path.substring(separator.length());
        }
        Pattern sep = Pattern.compile("([^\\"+ separator+"]+)");
        Matcher m = sep.matcher(path);
        while(m.find())
            res.add(m.group(0));
        return res;
    }

    public static String quoute(String s){
        if(s == null)
            return null;
        return "\"" + s + "\"";
    }

    // Replace custom separator to the File system sep.
    public static String replaceSeparator(String path){
        if(path == null || path.length() == 0)
            return path;
        String sep = "\\" + FileSystems.getDefault().getSeparator(); //escaped separator
        return path.replaceAll("[\\" + "\\" + "/]+", sep);  // replace escaped '\' or just '/' with separator.
    }
}
