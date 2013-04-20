package r.builtins;

class Gsub extends Sub {
    static final CallFactory _ = new Gsub("gsub", new String[]{"pattern", "replacement", "x", "ignore.case", "perl", "fixed", "useBytes"}, new String[]{"x", "pattern", "replacement"}, true);

    Gsub(String name, String[] params, String[] required, boolean global) {
        super(name, params, required, global);
    }
}
