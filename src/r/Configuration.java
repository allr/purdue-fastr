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

    /** If set to true, then the lhs is not copied if the direct optimizations can decide that the lhs and rhs do not
     * alias. This is easy to check because in direct specializations they must all be non-views, so just their pointer
     * comparison will do.
     */
    public static final boolean ARRAY_UPDATE_DO_NOT_COPY_LHS_WHEN_NO_ALIAS_IN_DIRECT_SPECIALIZATIONS = true;

    /** If enabled, non-typecasting numeric updates (int, double, complex) with non-shared lhs are optimized into
     * single node without the rhs typecast node.
     */
    public static final boolean ARRAY_UPDATE_DIRECT_SPECIALIZATIONS = true;

    /** Determines if the Generalized case of the ArrayUpdate attempts to use the update methods from the direct
     * specializations, or not.
     */
    public static final boolean ARRAY_UPDATE_DIRECT_SPECIALIZATIONS_IN_GENERALIZED = true;

    /** If true, the latest direct specialization for the Generalized cache is cached and is tested first in next run
     * for the UpdateArray (the UpdateType enum).
     */
    public static final boolean ARRAY_UPDATE_DIRECT_SPECIALIZATIONS_IN_GENERALIZED_CACHE = true;

}
