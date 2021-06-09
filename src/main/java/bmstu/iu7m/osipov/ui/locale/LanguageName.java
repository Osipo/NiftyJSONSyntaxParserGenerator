package bmstu.iu7m.osipov.ui.locale;

import java.util.Locale;

/*
  IANA Languages subtags
  https://www.iana.org/assignments/language-subtag-registry/language-subtag-registry
 */
public enum LanguageName {
    RU(new Locale("ru")), ENG(new Locale("en"));
    private Locale locale;
    private LanguageName(Locale l){
        this.locale = l;
    }
    public Locale getLocale(){
        return this.locale;
    }
}
