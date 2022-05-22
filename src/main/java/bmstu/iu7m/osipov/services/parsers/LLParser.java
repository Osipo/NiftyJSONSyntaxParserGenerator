package bmstu.iu7m.osipov.services.parsers;

import bmstu.iu7m.osipov.services.lexers.LanguageSymbol;
import bmstu.iu7m.osipov.structures.graphs.Pair;
import bmstu.iu7m.osipov.structures.lists.LinkedStack;
import bmstu.iu7m.osipov.services.grammars.*;
import bmstu.iu7m.osipov.services.lexers.ILexer;
import bmstu.iu7m.osipov.services.lexers.Token;
import bmstu.iu7m.osipov.services.parsers.generators.LLParserGenerator;
import bmstu.iu7m.osipov.structures.trees.LinkedTree;
import bmstu.iu7m.osipov.structures.trees.LinkedNode;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class LLParser extends Parser{
    private Map<Pair<String, String>, GrammarString> table;
    private Set<String> T;
    private Set<String> N;
    private String start;


    public LLParser(Grammar G, ILexer lexer){
        super(G, lexer);
        G = Grammar.deleteLeftRecursion(G);
        G = G.deleteLeftFactor();
        this.table = LLParserGenerator.getTable(G);
        this.T = G.getTerminals();
        this.N = G.getNonTerminals();
        this.start = G.getStart();
    }

    //TODO: make full table with empty cells (error records)
    public void toFile(String fname){
        File f = new File(fname);
        if(f.lastModified() != 0){
            System.out.println("Cannot write to existing file!");
            return;
        }
        try (FileWriter fw = new FileWriter(f,true);) {
            fw.write("{\n\t");
            int l = 0;
            for(Pair<String,String> cell : table.keySet()){
                fw.write("\""+cell.getV1()+" "+cell.getV2()+"\": ");
                GrammarString s = table.get(cell);
                if(s != null){
                    fw.write('[');
                    int syms = 0;
                    for(GrammarSymbol sym : s.getSymbols()){
                        fw.write("\""+sym.getVal()+"\"");
                        syms++;
                        if(syms != s.getSymbols().size()){
                            fw.write(", ");
                        }
                    }
                    fw.write(']');
                }
                else
                    fw.write("error");
                l++;
                if(l != table.keySet().size()){
                    fw.write(",\n\t");
                }
            }
            fw.write("\n}");
        }
        catch (FileNotFoundException e){
            System.out.println("Cannot open file to write.");
        }
        catch (IOException e){
            System.out.println("Cannot write to file");
        }
    }

    @Override
    public File getImage() throws IOException {
        throw new UnsupportedOperationException("LLParser does not support showing of automaton of GOTO");
    }


    // Algorithm 4.20 with lexer module.
    @Override
    public LinkedTree<LanguageSymbol> parse(File file){
        try (FileInputStream f = new FileInputStream(file.getAbsolutePath())){
            LinkedStack<LinkedNode<LanguageSymbol>> S = new LinkedStack<>();
            LinkedNode<LanguageSymbol> root = new LinkedNode<>();
            LinkedNode<LanguageSymbol> EOF = new LinkedNode<>();
            int line, col = 0;
            EOF.setValue(new Token("$","$",'t',0,0));
            root.setValue(new Token(start, start,'n',0,0));
            root.setIdx(1);
            S.push(EOF);
            S.push(root);// Stack: S, $.
            LinkedNode<LanguageSymbol> X = S.top();
            Token tok = lexer.recognize(f);
            isParsed = true;
            while(tok == null || tok.getName().equals("Unrecognized")){
                if(tok != null) {// if really Unrecognized
                    System.out.println(tok);
                    isParsed = false;
                }
                tok = lexer.recognize(f);
            }
            String t = tok.getName();
            line = tok.getLine();
            col = tok.getColumn();
            int nidx = 1;//counter of elements (tree nodes)
            while(!X.getValue().getName().equals("$")) {
                if(X.getValue().getName().equals(t)){//S.Top() == X && X == t
                    showMessage(S+" >>"+t+" action: "+"Remove from stack "+t);

                    ((Token)S.top().getValue()).setLexem(tok.getLexeme()); //convert Token name to the lexeme.
                    S.pop();
                    tok = lexer.recognize(f);
                    while(tok == null || tok.getName().equals("Unrecognized")){
                        if(tok != null) {
                            System.out.println(tok);
                            isParsed = false;//TODO: MAY BE OPTIONAL.
                        }
                        tok = lexer.recognize(f);
                    }
                    t = tok.getName();
                    line = tok.getLine();
                    col = tok.getColumn();
                    X = S.top();
                    continue;
                }
                else if(X.getValue().getName().equals("Unrecognized")){
                    System.out.println(X.getValue());
                    isParsed = false;
                    break;
                }
                else if(this.T.contains(X.getValue().getName())) {
                    showMessage(S+" >>"+t+" action: "+"Error: Unmatched terms!");
                    isParsed = false;
                    break;
                }
                GrammarString prod = table.get(new Pair<String, String>(X.getValue().getName(), t));
                if(prod == null) {
                    showMessage(S+" >>"+t+" action: "+"Error: No Production ");
                    isParsed = false;
                    break;
                }
                else{
                    showMessage(S+" >>"+t+" action: "+"Produce "+X.getValue().toString()+" -> "+ prod );
                    List<GrammarSymbol> symbols = prod.getSymbols();
                    S.pop();
                    //LinkedStack<LinkedNode<String>> RS = new LinkedStack<>();//used only to order children YK..Y1 -> Y1..Yk in brace notation
                    for(int i = 0; i < symbols.size(); i++){
                        LinkedNode<LanguageSymbol> node = new LinkedNode<>();
                        nidx++;
                        node.setValue(new Token(symbols.get(i).getVal(),null, symbols.get(i).getType(),line,col));
                        node.setIdx(nidx);
                        node.setParent(X);
                        X.getChildren().add(node);
                    }

                    for(int i = symbols.size() - 1; i >= 0; i--){
                        LinkedNode<LanguageSymbol> node = X.getChildren().get(i);
                        if(!node.getValue().getName().equals(empty))//skip empty rules.
                            S.push(node);
                    }
                }
                X = S.top();
            }
            if(isParsed) {
                lexer.reset();//reset column and line counter.
                return new LinkedTree<LanguageSymbol>(root);
            }
            else {
                if(tok.getType() != 'e') {
                    System.out.println(lexer.generateError(S.top().getValue().getName(),tok.getName()));
                }
                else
                    System.out.println(tok);
            }
            lexer.reset();//reset column and line counter.
            return null;//if syntax error return null.
        }
        catch (FileNotFoundException e){
            lexer.reset();
            System.out.println("File not found. Specify file to read");
            return null;
        } catch (IOException e) {
            lexer.reset();
            System.out.println("File is not available now.");
            return null;
        }
    }

    @Override
    public void showMessage(String body) {
        if(this.mode == ParserMode.DEBUG)
            System.out.println(body);
    }
}