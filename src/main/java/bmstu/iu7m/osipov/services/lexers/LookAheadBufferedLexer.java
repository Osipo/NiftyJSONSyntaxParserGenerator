package bmstu.iu7m.osipov.services.lexers;

import java.io.IOException;
import java.io.InputStream;

public class LookAheadBufferedLexer implements LexerIO {
    //Lexer parts.
    protected char[] buf;
    protected int bsize;
    protected int bufp;
    protected int EOL;

    // Data from source file
    protected int line;
    protected int col;
    protected InputStream source;

    public LookAheadBufferedLexer(int bsize){
        this.bsize = bsize;
        this.buf = new char[bsize + 1];
        this.bufp = 0;
        this.EOL = 0;
        this.line = 1;
        this.col = 0;
        this.source = null;
    }

    public LookAheadBufferedLexer(){
        this(255);
    }

    @Override
    public int getLine(){
        return line;
    }

    @Override
    public void setLine(int l){
        this.line = l;
    }

    @Override
    public void setCol(int c){
        this.col = c;
    }

    @Override
    public int getCol(){
        return col;
    }

    @Override
    public int ungetch(char c){
        if(bufp > bsize){
            System.out.println("Error (" + line + ":" + col + "). ungetch: too many characters.");
            return 0;
        }
        else{
            buf[bufp++] = c;
            return 1;
        }
    }

    @Override
    public int getch(InputStream s) throws IOException {
        if(source == null)
            source = s;
        if(bufp > 0)
            return buf[--bufp];
        else{
            col++;
            char c = (char)source.read();
            if(c == '\n'){
                line++; col = 0;
            }
            return c;
        }
    }

    @Override
    public int getFilech(InputStream r) throws IOException {
        if(source == null)
            source = r;
        char c = (char)source.read();
        if(c == '\n'){
            line += 1;
            col = 0;
        }
        else{
            col += 1;
        }
        return c;
    }

    @Override
    public String getFromBuffer(){
        return new String(buf);
    }

    @Override
    public void clear(){
        //clear content of the buffer.
        for(int i = 0; i < this.buf.length; i++)
            this.buf[i] = '\u0000';
        this.bufp = 0;
        this.EOL = 0;
        this.source = null;
        this.col = 0;
        this.line = 1;
    }
}