package r;

/** List all optimizations as boolean flags in this class. Please make sure that when you change this file you
 * recompile the whole project as the flags should be treated as compile time constants.
 */
public class Configuration {


    /** Aperm builtin will specialize on the type of the argument. If it is any <type>implementation, the fast direct
     * access methods will be used.
     */
    public static final boolean BUILTIN_APERM_TYPED_DIRECT_ACCESS = true;

    /** ValueCopy types for direct access copying will be used as special cases. Otherwise the general getter/setter
     * methods will be used.
     */
    public static final boolean ARRAY_UPDATE_LHS_VALUECOPY_DIRECT_ACCESS = true;

    /** Determines if the direct access specializations will be used for the right hand side of the array update when
     * copying the rhs.
     */
    public static final boolean ARRAY_UPDATE_RHS_VALUECOPY_DIRECT_ACCESS = true;

}
