package bmstu.iu7m.osipov.services.parsers.json;

import bmstu.iu7m.osipov.services.parsers.json.elements.JsonArray;
import bmstu.iu7m.osipov.services.parsers.json.elements.JsonElement;
import bmstu.iu7m.osipov.services.parsers.json.elements.JsonObject;
import bmstu.iu7m.osipov.services.parsers.json.elements.JsonString;
import bmstu.iu7m.osipov.structures.lists.LinkedStack;

import java.io.*;

public class SimpleJsonParser2 {
    private JsParserState state;
    private char[] buf;
    private int bsize;
    private int bufp;

    private int line;
    private int col;
    private boolean escaped;
    private boolean inArray;

    public SimpleJsonParser2(int bsize){
        this.state = JsParserState.START;
        this.buf = new char[bsize];
        this.bsize = bsize;
        this.bufp = 0;
        this.line = 1;
        this.col = 0;
        this.escaped = false;
        this.inArray = false;
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
        } catch (IOException e){
            System.out.println(e.getMessage());
            this.state = JsParserState.START;
            return null;
        }
        return result;
    }

    public JsonObject parseStream(InputStream in){
        LinkedStack<JsonObject> J_OBJS = new LinkedStack<>();
        LinkedStack<JsonArray> J_ARRS = new LinkedStack<>();
        LinkedStack<String> props = new LinkedStack<>();

        try(InputStreamReader ch = new InputStreamReader(in)){
            this.state = JsParserState.START;
            JsonString propName = new JsonString(null);
            while(this.state != JsParserState.CLOSEROOT && this.state != JsParserState.ERR)
                iterate(J_OBJS, J_ARRS, props, ch);

        } catch (IOException e){
            System.out.println(e.getMessage());
            System.out.println("At ("+line+":"+col+")");
            this.state = JsParserState.START;
            return null;
        }
        this.state = JsParserState.START;
        return J_OBJS.top();
    }

    public void iterate(LinkedStack<JsonObject> J_OBJS, LinkedStack<JsonArray> J_ARR, LinkedStack<String> props, InputStreamReader r) throws IOException{
        int c = (int)' ';
        JsonObject cur_obj = null;
        while(c == ' ' || c == '\n' || c == '\r' || c == '\t') { //skip spaces
            c = getch(r);
        }

        if(this.state == JsParserState.START && c != '{') //Json object must starts with '{'
            err(c, "{");

        else if(this.state == JsParserState.START){ // state = Start, c == '{'
            J_OBJS.push(new JsonObject());
            this.state = JsParserState.AWAIT_PROPS;
        }
        else if((this.state == JsParserState.AWAIT_PROPS || this.state == JsParserState.AWAIT_STRVALUE) && c != '\"')
            err(c, "\"");
        else if(this.state == JsParserState.AWAIT_PROPS){ // state == AWAIT_PROPS, c == '"' == t(Start, '{')

            c = getFilech(r); //read character.
            int l = 0;
            while(c != '\"' && bufp < bsize) {
                if(c == '\\')
                    c = getEscaped(r);
                l++;
                buf[bufp++] = (char)c;
                c = getFilech(r);
            }
            if(bufp >= bsize) {
                err('\"', ""+c);
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
            if(bufp >= bsize) {
                err('\"', ""+c);
                return;
            }

            readValue(J_OBJS, J_ARR, props, r, new String(buf, 0, l));
            flushBuf();
            this.state = JsParserState.READ_STRVALUE;
        }
        else if(this.state == JsParserState.READ_PROPNAME && c != ':')
            err(c,":");
        else if(this.state == JsParserState.READ_PROPNAME){ // ':' name value separator was read.
            this.state = JsParserState.COLON;
        }

        //FIRST SYMBOL OF VALUE (AFTER SEPARATOR)
        else if(this.state == JsParserState.COLON){
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
                    this.state = JsParserState.OPENARR;
                    if(ungetch((char) c) == 0)
                        err(c, "available space but OutOfMemory!");
                    break;
                }
                default:{
                    break;
                }
            }
        }
        else if(this.state == JsParserState.OPENARR && c != '[')
            err(c,"[");
        else if(this.state == JsParserState.OPENARR){
            this.inArray = true;
            J_ARR.push(new JsonArray());
            this.state = JsParserState.COLON;
        }
    }

    private void readValue(LinkedStack<JsonObject> J_OBJS, LinkedStack<JsonArray> J_ARRS,
                           LinkedStack<String> props, InputStreamReader r, String val) throws IOException {
        JsonObject cur_obj = J_OBJS.top();
        JsonArray cur_arr = J_ARRS.top();
        int c = (int)' ';
        while(c == ' ' || c == '\n' || c == '\r' || c == '\t') //skip trailing spaces.
            c = getch(r);

        if(c == ',' && inArray){
            cur_arr.getElements().add(parseFromStr(val));
            this.state = JsParserState.COLON;
        }
        else if(c == ']' && inArray){
            J_ARRS.pop();
            JsonArray e_arr = J_ARRS.top();
            e_arr.getElements().add(cur_arr);
            //RECURSIVE CALL (check that next is ',' or ']' or '}')
        }
        else if(c == '}' && inArray){
            cur_obj.getValue().put(props.top(), parseFromStr(val));
            props.pop();
            J_OBJS.pop();
            cur_arr.getElements().add(cur_obj);
            //RECURSIVE CALL (check that next is ',' or ']' or '}')
        }
        else if(c == ','){
            cur_obj.getValue().put(props.top(), parseFromStr(val));
            props.pop();
            this.state = JsParserState.AWAIT_PROPS;
        }
        else if(c == ']'){
            cur_obj.getValue().put(props.top(), cur_arr);
            J_ARRS.pop();
            props.pop();
            //RECURSIVE CALL (check that next is ',' or ']' or '}')
        }
        else if(c == '}'){
            cur_obj.getValue().put(props.top(), parseFromStr(val));
            props.pop();
            if(J_OBJS.size() == 1) // root object finished.
                this.state = JsParserState.CLOSEROOT;
            else{
                J_OBJS.pop();
                JsonObject e_obj = J_OBJS.top();
                e_obj.getValue().put(props.top(), cur_obj);
                props.pop();
                //RECURSIVE CALL (check that next is ',' or ']' or '}')
            }
        }
    }

    private JsonElement parseFromStr(String val){
        return null;
    }

    private void err(int act, String msg){
        state = JsParserState.ERR;
        System.out.println("Founded illegal symbol \'" + act + "\'" +
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
                int code = (int)ProcessExp.parse(new String(hcode),null,'N',16,1); //just parse positive hex number to decimal.
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
