package bmstu.iu7m.osipov.services.interpret;

import bmstu.iu7m.osipov.Main;
import bmstu.iu7m.osipov.services.grammars.AstSymbol;
import bmstu.iu7m.osipov.services.parsers.LRAstTranslator;
import bmstu.iu7m.osipov.structures.trees.PositionalTree;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//Process
public class ModuleProcessor {
    protected Interpreter inter;
    protected LRAstTranslator parser;
    protected String cwd;
    protected Map<String, Env> moduleVars;
    protected String rootFile;
    public ModuleProcessor(LRAstTranslator parser, Interpreter inter, String cwd, String rootFile){
        this.inter = inter;
        this.parser = parser;
        inter.setModuleProcessor(this);
        this.cwd = cwd;
        this.moduleVars = new HashMap<>();
        this.rootFile = rootFile;
    }

    public ModuleProcessor(LRAstTranslator parser, Interpreter inter){
        this(parser, inter, Main.CWD, null);
    }

    public void resolveModules(Map<String, String> imports, String execModule) throws Exception {

        this.moduleVars.put(execModule, inter.getRootContext()); //save execModule context.

        //[ext] < $[] before  '$ = rootContext'
        //[ext] < [] < $[] < [fibo_sum, sum, pi] after processing sub_module
        //[ext] < $[] < [] < [...] after findModule parsed.
        //[ext] < [...] < [a, ...] after processing execModule

        //process each import stmt.
        for(Map.Entry<String, String> imp : imports.entrySet()) {
            String sub_module = imp.getKey(); //extract module.
            sub_module = sub_module.substring(0, sub_module.lastIndexOf('.'));
            String prop = imp.getValue();
            //System.out.println("try find " + prop + " at " + sub_module);

            Variable v = findModule(sub_module, prop, execModule);

            inter.setRootContext(this.moduleVars.get(execModule)); //restore context after search
            if(v != null){
                Variable nv = new Variable(v.getValue());
                //System.out.println("found at module " + sub_module + ": " + nv.getValue());
                nv.setStrVal(v.getStrVal());
                if(v.isList())
                    nv.setItems(new ArrayList<>(v.getItems()));
                nv.setFunction(v.getFunction());
                inter.getRootContext().add(nv);
            }
        }
    }

    public Variable findModule(String mod, String prop, String execMod) throws Exception {
        Path rpath = Paths.get(cwd);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(rpath)) {
            for (Path p : stream) {
                if(Files.isReadable(p) && Files.isRegularFile(p) && !Files.isDirectory(p)){
                    String fName = p.toAbsolutePath().toString();
                    if(fName.equals(rootFile))
                        continue;

                    if(!mod.equals(execMod) && moduleVars.containsKey(mod)) //if found recursive dependency A import B where B import A
                        throw new Exception("Cannot resolve module imports. Modules '" + execMod + "' and '" + mod + "' have recursive (deadlock) imports (A import B where B import A");

                    //parse module at first if needed.
                    if(!moduleVars.containsKey(fName)) {
                        PositionalTree<AstSymbol> sub = parser.translate(new File(fName));
                        if(sub != null && sub.root().getValue().getType().equals("module") && sub.root().getValue().getValue().equals(mod)) {
                            inter.interpret(sub);
                            this.moduleVars.put(fName, inter.getRootContext());
                        }
                    }

                    //extract module vars.
                    if (moduleVars.get(fName) != null){
                        Variable v = moduleVars.get(fName).getOn(prop);
                        if(v != null)
                            return v;
                    }
                } //regular file
            }// end for.
        }// end try.
        catch (IOException e){
            throw new Exception("Cannot find module '" + mod + "' at " + cwd);
        }
        return null;
    }
}