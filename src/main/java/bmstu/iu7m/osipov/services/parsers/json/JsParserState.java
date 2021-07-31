package bmstu.iu7m.osipov.services.parsers.json;

public enum JsParserState {
    START, AWAIT_PROPS, READ_PROPNAME, AWAIT_STRVALUE, READ_STRVALUE, NEXT_VALUE,

    OPENROOT,CLOSEROOT,OPENBRACE,CLOSEBRACE,OPENARR,ARRELEM,CLOSEARR,OPENQ,CLOSEQ,OPENQP,CLOSEQP,COLON,ERR,ID_READ;
}
