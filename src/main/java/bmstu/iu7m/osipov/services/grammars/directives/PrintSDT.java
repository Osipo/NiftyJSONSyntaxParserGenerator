package bmstu.iu7m.osipov.services.grammars.directives;

import bmstu.iu7m.osipov.Main;
import bmstu.iu7m.osipov.services.lexers.LanguageSymbol;
import bmstu.iu7m.osipov.services.lexers.Translation;
import bmstu.iu7m.osipov.structures.trees.Node;
import bmstu.iu7m.osipov.utils.PathStringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

public class PrintSDT implements SDTParser {

    @Override
    public void exec(Translation t, Node<LanguageSymbol> parent) {
        if(t != null && t.getArguments() != null){
            String str = t.getArguments().getOrDefault("str", "");
            String fname = t.getArguments().getOrDefault("output", "");
            if(fname.equals("")){
                System.out.print(str);
            }
            else{
                fname = PathStringUtils.replaceSeparator(Main.CWD + fname);
                File f = new File(fname);

                try(FileWriter wr = new FileWriter(f)){
                    wr.write(str);
                } catch (IOException e){

                }
            }
        }
    }
}
