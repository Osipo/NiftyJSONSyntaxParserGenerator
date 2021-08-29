package bmstu.iu7m.osipov.services.parsers.json;

import bmstu.iu7m.osipov.services.parsers.json.elements.*;
import bmstu.iu7m.osipov.structures.lists.LinkedStack;

import java.io.*;

public class SimpleJsonParser2 {
    private JsParserState state;
    private char[] buf;
    private int bsize;
    private int bufp;

    private int line;
    private int col;
    private JsonElement curVal;

    public SimpleJsonParser2(int bsize){
        this.state = JsParserState.START;
        this.buf = new char[bsize];
        this.bsize = bsize;
        this.bufp = 0;
        this.line = 1;
        this.col = 0;
    }

    public SimpleJsonParser2(){
        this(255);
    }


    public JsonObject parse(String fileName) {
        File f = new File(fileName);
        return parse(f);
    }

    public JsonObject parse(File fl){
        JsonObject result = null;
        try(FileInputStream f = new FileInputStream(fl.getAbsolutePath());
        ){
            result = parseStream(f);
        } catch (FileNotFoundException e){
            System.out.println(e.getMessage());
            flushBuf();
        } catch (IOException e){
            System.out.println(e.getMessage());
            this.state = JsParserState.START;
            flushBuf();
            this.col = 0; this.line = 1;
            return null;
        }
        return result;
    }

    public JsonObject parseStream(InputStream in){
        LinkedStack<JsonObject> J_OBJS = new LinkedStack<>();
        LinkedStack<JsonArray> J_ARRS = new LinkedStack<>();
        LinkedStack<JsonElement> J_ROOTS = new LinkedStack<>();
        LinkedStack<String> props = new LinkedStack<>();

        try(InputStreamReader ch = new InputStreamReader(in)){
            this.state = JsParserState.START;// before read set parser to the start state.
            this.col = 0; this.line = 1;
            flushBuf();
            while(this.state != JsParserState.CLOSEROOT && this.state != JsParserState.ERR)
                iterate(J_OBJS, J_ARRS, J_ROOTS, props, ch);
            if(this.state == JsParserState.ERR)
                return null;

        } catch (IOException e){
            System.out.println(e.getMessage());
            System.out.println("At (" + line + ":" + col + ")");
            flushBuf();
            return null;
        }
        return J_OBJS.top();
    }

    public void iterate(LinkedStack<JsonObject> J_OBJS, LinkedStack<JsonArray> J_ARR,
                        LinkedStack<JsonElement> J_ROOTS, LinkedStack<String> props, InputStreamReader r) throws IOException{
        int c = (int)' ';
        JsonObject cur_obj = null;
        while(c == ' ' || c == '\n' || c == '\r' || c == '\t') { //skip spaces
            c = getch(r);
        }

        if(this.state == JsParserState.AWAIT_PROPS && c == '}' && ungetch('}') == 0)
            err('}', "available space for '}' but OutOfMemory!");

        else if(this.state == JsParserState.AWAIT_PROPS && c == '}'){ // side effect from previous if [ ungetch('}') call]!
            this.state = JsParserState.NEXT_VALUE; // just goto NEXT_VALUE where all checks.
        }

        else if(this.state == JsParserState.START && c != '{') //Json object must starts with '{'
            err(c, "{");

        else if(this.state == JsParserState.START){ // state = Start, c == '{'
            J_OBJS.push(new JsonObject());
            J_ROOTS.push(J_OBJS.top());
            this.state = JsParserState.AWAIT_PROPS;
        }
        else if((this.state == JsParserState.AWAIT_PROPS || this.state == JsParserState.AWAIT_STRVALUE) && c != '\"')
            err(c, "\"");
        else if(this.state == JsParserState.AWAIT_PROPS){ // state == AWAIT_PROPS, c == '"' [ trans_from(Start, '{') ]

            c = getFilech(r); //read character.
            int l = 0;
            while(c != '\"' && bufp < bsize) { //read all content until '"' char (end of the string) while buffer available.
                if(c == '\\')
                    c = getEscaped(r);
                l++;
                buf[bufp++] = (char)c;
                c = getFilech(r);
            }
            if(bufp >= bsize && c != '\"') { //too long string (buffer exceeded)
                err(c, "available space for \'"+(char)c +"'\' or EOL (\") symbol");
                return;
            }
            props.push(new String(buf, 0, l));
            flushBuf();
            this.state = JsParserState.READ_PROPNAME;
        }
        else if(this.state == JsParserState.AWAIT_STRVALUE){
            c = getFilech(r); //read character.
            int l = 0;
            while(c != '\"' && bufp < bsize) {
                if(c == '\\')
                    c = getEscaped(r);
                l++;
                buf[bufp++] = (char)c;
                c = getFilech(r);
            }
            if(bufp >= bsize && c != '\"') {
                err(c, "available space for \'"+(char)c +"'\' or EOL (\") symbol");
                return;
            }

            this.state = JsParserState.READ_STRVALUE;
            this.curVal = new JsonString(new String(buf, 0, l));
            flushBuf();
        }
        else if(this.state == JsParserState.READ_PROPNAME && c != ':')
            err(c,":");
        else if(this.state == JsParserState.READ_PROPNAME){ // c == ':' name value separator was read.
            this.state = JsParserState.COLON;
        }


        else if(this.state == JsParserState.EMPTY_OR_NOT_ARR && c == ']' && ungetch(']') == 0){
            err(']', "available space for ']' but OutOfMemory!");
        }
        else if(this.state == JsParserState.EMPTY_OR_NOT_ARR && c == ']'){ // side effect from previous else if [ungetch() call!]
            this.state = JsParserState.NEXT_VALUE;
        }
        else if(this.state == JsParserState.EMPTY_OR_NOT_ARR && ungetch((char) c) == 0){ // c != ']'
            err(c, "available space for '"+(char)c+"' but OutOfMemory!");
        }
        else if(this.state == JsParserState.EMPTY_OR_NOT_ARR){
            this.state = JsParserState.COLON;
        }

        //FIRST SYMBOL OF VALUE (AFTER SEPARATOR)
        else if(this.state == JsParserState.COLON){
            //System.out.println("Property: "+props.top()+ " symbol \'"+(char)c+"\'");
            switch (c){
                case '\"':{
                    this.state = JsParserState.AWAIT_STRVALUE;
                    if( ungetch((char) c) == 0)
                        err(c, "available space but OutOfMemory!");
                    break;
                }
                case '{':{
                    this.state = JsParserState.START;
                    if(ungetch((char) c) == 0)
                        err(c, "available space but OutOfMemory!");
                    break;
                }
                case '[': {
                    J_ARR.push(new JsonArray());
                    J_ROOTS.push(J_ARR.top());
                    this.state = JsParserState.EMPTY_OR_NOT_ARR;
                    break;
                }
                default: {
                    int l = 0;

                    //CHECK that rvalue is not consists of token symbols ( ']' '}' ',' ':' '[' '{', EOF)
                    while(bufp < bsize && (c != ' ' && c != '\n' && c != '\r' && c != '\t')
                        && (c != '}' && c != ']' && c != ',' && c != ':' && c != '{'  && c != '[' && c != 65535)
                    )
                    { //read all content til first space symbol ' '
                        buf[bufp++] = (char)c;
                        c = getFilech(r);
                        l++;
                    }
                    if(bufp >= bsize && (c != ' ' && c != '\n' && c != '\r' && c != '\t')
                            && (c != '}' && c != ']' && c != ',' && c != ':' && c != '{' && c != '[' && c != 65535)
                    )
                    {
                        err(c, "end of the token (space symbol or LF or CR or tab) or another token ('}' ']' etc.) but \"..."+(char)c+"\"");
                        return;
                    }
                    String v = new String(buf, 0, l);
                    flushBuf();
                    if(v.equals("null"))
                        this.curVal = new JsonNull();
                    else if(v.equals("false"))
                        this.curVal = new JsonBoolean('f');
                    else if(v.equals("true"))
                        this.curVal = new JsonBoolean('t');
                    else {
                       double val = ProcessNumber.parseNumber(v);
                       if(Double.isNaN(val)) // NaN (Not a Number) tokens are invalid.
                           err(c, "a number token but found NaN \""+v+"\"");
                       else if(Math.floor(val) == val)
                           this.curVal = new JsonNumber((long) val);
                       else
                           this.curVal = new JsonRealNumber(val);
                    }
                    this.state = JsParserState.READ_STRVALUE;
                    if(ungetch((char) c) == 0)
                        err(c, "available space but OutOfMemory!");
                    break;
                } // END of default.
            } //END of switch
        }// END COLON state.

        //BEGIN READ_STRVALUE state. (READ_NON_EMPTY_VALUE)
        else if(this.state == JsParserState.READ_STRVALUE){
            cur_obj = J_OBJS.top();
            JsonArray cur_arr = null;
            if(c == ',' && J_ROOTS.top() instanceof JsonArray){
                cur_arr = J_ARR.top();
                cur_arr.getElements().add(this.curVal);// add new item to array
                this.state = JsParserState.COLON; //awaiting new value
                this.curVal = null;
            }
            else if(c == ',' && J_ROOTS.top() instanceof JsonObject){
                cur_obj.getValue().put(props.top(), this.curVal);//add new pair prop : value to the object.
                props.pop();
                this.state = JsParserState.AWAIT_PROPS;//awaiting new property
                this.curVal = null;
            }
            else if(c == ']' && J_ROOTS.top() instanceof JsonArray){ //after processed value follows ']'
                cur_arr = J_ARR.top();
                cur_arr.getElements().add(this.curVal);// add processed value to processed array.
                J_ARR.pop(); // remove processed array.
                J_ROOTS.pop();// and update root.
                this.curVal = null;
                if(J_ROOTS.top() instanceof JsonArray){
                    J_ARR.top().getElements().add(cur_arr); //add array as item
                }
                else if(J_ROOTS.top() instanceof JsonObject){
                    J_OBJS.top().getValue().put(props.top(), cur_arr);// add array as property.
                    props.pop();
                }
                this.state = JsParserState.NEXT_VALUE;
            }
            else if(c == '}'){ //after processed value follows '}'
                cur_obj.getValue().put(props.top(), this.curVal);
                props.pop();
                this.curVal = null;
                if(J_OBJS.size() == 1) // root object finished.
                    this.state = JsParserState.CLOSEROOT; // set final state to exit from cycle.
                else{
                    J_OBJS.pop();//remove processed object.
                    J_ROOTS.pop();//and update root.
                    if(J_ROOTS.top() instanceof JsonArray){
                        J_ARR.top().getElements().add(cur_obj);
                    }
                    else if(J_ROOTS.top() instanceof JsonObject){
                        J_OBJS.top().getValue().put(props.top(), cur_obj);
                        props.pop();
                    }
                    this.state = JsParserState.NEXT_VALUE;
                }
            }
            else
                err(c, "one of ',' '}' ']' ");
        } //END READ_STRVALUE

        //BEGIN NEXT_VALUE state
        else if(this.state == JsParserState.NEXT_VALUE){
            cur_obj = J_OBJS.top();
            if(c == ',' && J_ROOTS.top() instanceof JsonArray){
                this.state = JsParserState.COLON;
            }
            else if(c == ',' && J_ROOTS.top() instanceof JsonObject){
                this.state = JsParserState.AWAIT_PROPS;
            }
            else if(c == ']' && J_ROOTS.top() instanceof JsonArray){
                JsonArray cur_arr = J_ARR.top();
                J_ARR.pop(); // remove processed array.
                J_ROOTS.pop();// and update root.
                if(J_ROOTS.top() instanceof JsonArray){
                    J_ARR.top().getElements().add(cur_arr); //add array as item
                }
                else if(J_ROOTS.top() instanceof JsonObject){
                    J_OBJS.top().getValue().put(props.top(), cur_arr);// add array as property.
                    props.pop();
                }
                this.state = JsParserState.NEXT_VALUE;
            }
            else if(c == '}'){
                cur_obj = J_OBJS.top();
                if(J_OBJS.size() == 1) // root object finished.
                    this.state = JsParserState.CLOSEROOT; // set final state to exit from cycle.
                else{
                    J_OBJS.pop();//remove processed object.
                    J_ROOTS.pop();//and update root.
                    if(J_ROOTS.top() instanceof JsonArray){
                        J_ARR.top().getElements().add(cur_obj);
                    }
                    else if(J_ROOTS.top() instanceof JsonObject){
                        J_OBJS.top().getValue().put(props.top(), cur_obj);
                        props.pop();
                    }
                    this.state = JsParserState.NEXT_VALUE;
                }
            }
            else
                err(c, "one of ',' '}' ']' ");

        } // END NEXT_VALUE
    }

    private void err(int act, String msg){
        state = JsParserState.ERR;
        System.out.println("Founded illegal symbol \'" + (char)act + "\'" +
                " at ("+line+":"+col+"). Expected: "+msg);
    }

    private int ungetch(char c){
        if(bufp >= bsize){
            System.out.println("Error ("+line+":"+col+"). ungetch(): too many characters.");
            return 0;
        }
        else{
            buf[bufp++] = c;
            return 1;
        }
    }

    private int getch(InputStreamReader r) throws IOException {
        if(bufp > 0)
            return buf[--bufp];
        else{
            col++;
            int c = r.read();
            if(c == '\n'){
                line++; col = 0;
            }
            else
                col += 1;
            return c;
        }
    }

    private int getEscaped(InputStreamReader r) throws IOException {
        col++;
        char x = (char)r.read();
        switch (x){
            case 't':{
                col++;
                return '\t';
            }
            case 'r':{
                col++;
                return '\r';
            }
            case 'n':{
                col++;
                return '\n';
            }
            case 'f':{
                col++;
                return '\f';
            }
            case 'b':{
                col++;
                return '\b';
            }
            case '\'':{
                col++;
                return '\'';
            }
            case '\"':{
                col++;
                return '\"';
            }
            case '\\':{
                col++;
                return '\\';
            }
            case 'u':{
                col++;
                int i = 0;
                char[] hcode = new char[4];
                while(i < 4 && ( ((x = (char) r.read()) >= '0' && x <='9') || (x >= 'A' && x <= 'F') || (x >= 'a' && x <= 'f') )){
                    hcode[i] = x;
                    i++;
                }
                if(i < 4) {
                    err(x, "Unicode token \\uxxxx where x one of [0-9] or [A-Fa-f]");
                    return 0;
                }
                int code = (int)ProcessNumber.parse(new String(hcode),null,'N',16,1); //just parse positive hex number to decimal.
                return code;
            }
            default:{
                ungetch(x);
                return '\\';
            }
        }
    }

    private int getFilech(InputStreamReader r) throws IOException {
        int c = r.read();
        if(c == '\n'){
            line += 1;
            col = 0;
        }
        else{
            col += 1;
        }
        return c;
    }

    private void flushBuf(){
        bufp = 0;
        for(int i = 0; i < this.buf.length; i++){
            this.buf[i] = '\u0000';
        }
    }
}
