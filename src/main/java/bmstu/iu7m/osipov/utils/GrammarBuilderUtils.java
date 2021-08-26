package bmstu.iu7m.osipov.utils;

import bmstu.iu7m.osipov.services.grammars.GrammarSDTString;
import bmstu.iu7m.osipov.services.lexers.LanguageSymbol;
import bmstu.iu7m.osipov.structures.trees.LinkedNode;

import java.util.Locale;

public class GrammarBuilderUtils {


    /**
     * Replace all references to the symbols of the body
     * with actual lexemes. Each reference consists of
     * '$' sign and a positive number, where number indicates to the position
     * of the body (starts from zero).
     * @param body node with body of production (rule)
     * @param act_arg the string value of the argument
     * @return parsed value of argument
     */
    public static String replaceSymRefsAtArgument(LinkedNode<LanguageSymbol> body, String act_arg){
        if(body == null || act_arg == null || body.getChildren() == null)
            return null;
        else if(act_arg.length() == 0 || act_arg.length() == 1)
            return act_arg;

        char[] syms = act_arg.toCharArray();
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < syms.length; i++){
            if(syms[i] == '@' && i + 1 < syms.length) {
                sb.append(syms[i + 1]);
                i += 1;
            }
            else if(syms[i] == '$'){

                //Read number
                i += 1;
                int j = i;
                while(i < syms.length && syms[i] >= '0' && syms[i] <= '9') i++;
                int num = Integer.parseInt(act_arg.substring(j, i));
                String nval = body.getChildren().get(num).getValue().getLexeme();
                sb.append(nval);
                i -= 1;
            }
            else
                sb.append(syms[i]);
        }
        return sb.toString();
    }

    /**
     * Replace all references to the symbols of the body
     * with actual lexemes. Each reference consists of
     * '$' sign and a positive number, where number indicates to the position
     * of the body (starts from zero).
     * @param body node with body of production (rule)
     * @param act_arg the string value of the argument
     * @return parsed value of argument
     */
    public static String replaceSymRefsAtArgument(GrammarSDTString body, String act_arg){
        if(body == null || act_arg == null)
            return null;
        else if(act_arg.length() == 0 || act_arg.length() == 1)
            return act_arg;

        char[] syms = act_arg.toCharArray();
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < syms.length; i++){
            if(syms[i] == '@') {
                sb.append(syms[i + 1]);
                i += 1;
            }
            else if(syms[i] == '$'){

                //Read number
                int j = i + 1;
                while(syms[i] >= '0' && syms[i] <= '9') i++;
                int num = Integer.parseInt(act_arg.substring(j, i));
                sb.append(body.getSymbols().get(num).getVal());
                i -= 1;
            }
            else
                sb.append(syms[i]);
        }
        return sb.toString();
    }


}
