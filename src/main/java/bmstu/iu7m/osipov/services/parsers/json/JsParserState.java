package bmstu.iu7m.osipov.services.parsers.json;

public enum JsParserState {
    START, AWAIT_PROPS_OR_END_OF_OBJ, AWAIT_PROPS, READ_PROPNAME, AWAIT_STRVALUE, READ_STRVALUE, NEXT_VALUE,

    CLOSEROOT, COLON, EMPTY_OR_NOT_ARR, ERR;
}
