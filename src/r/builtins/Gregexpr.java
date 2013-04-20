package r.builtins;

final class Gregexpr extends Regexpr {
    static final CallFactory _ = new Gregexpr("gregexpr", new String[]{"pattern", "text", "ignore.case", "perl", "fixed", "useBytes"}, new String[]{"pattern", "text"});

    private Gregexpr(String name, String[] params, String[] required) {
        super(name, params, required, true);
    }
}
