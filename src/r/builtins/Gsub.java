package r.builtins;

/**
 * "gsub"
 * 
 * <pre>
 * ppattern -- character string containing a regular expression (or character string for fixed = TRUE) to be matched in
 *          the given character vector. Coerced by as.character to a character string if possible. If a character vector
 *          of length 2 or more is supplied, the first element is used with a warning. Missing values are allowed except for regexpr and gregexpr.
 * x -- a character vector where matches are sought, or an object which can be coerced by as.character to a character vector.
 * ignore.case -- if FALSE, the pattern matching is case sensitive and if TRUE, case is ignored during matching.
 * perl -- logical. Should perl-compatible regexps be used?
 * fixed -- logical. If TRUE, pattern is a string to be matched as is. Overrides all conflicting arguments.
 * useBytes -- logical. If TRUE the matching is done byte-by-byte rather than character-by-character.
 * </pre>
 */
class Gsub extends Sub {
    @SuppressWarnings("hiding") static final CallFactory _ = new Gsub("gsub", new String[]{"pattern", "replacement", "x", "ignore.case", "perl", "fixed", "useBytes"}, new String[]{"x", "pattern", "replacement"}, true);

    Gsub(String name, String[] params, String[] required, boolean global) {
        super(name, params, required, global);
    }
}
