package r.analysis.codegen;

/** Marker for shared node fields.
 *
 * If a field of RNode is annotated with the @Shared, it will not be deep copied, but instead will be shared by all
 * deep copies of the parent node.
 */
public @interface Shared {
}
