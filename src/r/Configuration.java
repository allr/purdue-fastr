package r;

/** List all optimizations as boolean flags in this class. Please make sure that when you change this file you
 * recompile the whole project as the flags should be treated as compile time constants.
 */
public class Configuration {

    /** Aperm builtin will specialize on the type of the argument. If it is any <type>implementation, the fast direct
     * access methods will be used.
     */
    public static final boolean BUILTIN_APERM_TYPED_DIRECT_ACCESS = true;
}
