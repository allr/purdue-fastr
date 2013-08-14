package r.analysis.codegen;

/** Marks a deep copyable object.
 *
 * Trees of deep copyable objects can produce deep copies of themselves. If a deep copyable object does not override the deepCopy method, a proper deepCopy method is generated for the class when it is loaded if the FastrLoader is in use.
 */
public interface DeepCopyable {
    /** Returns the deep copy of the object.
     *
     * A deep copy of the object walks all DeepCopyable fields of the object (including their arrays) and creates their deep copy which will be stored in the resulting object. However if any of the deep copyable fields is marked with @Shared annotation, the field will be shallow copied.
     *
     * @return Deep copy of the object.
     */
    public DeepCopyable deepCopy();
}
