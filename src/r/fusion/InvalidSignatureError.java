package r.fusion;

/** Error to be thrown when unsupported view state is found.
 *
 * When this error is thrown from the build process, there should be no further attempts on building the fused
 * operator for a view with such signature.
 */
public class InvalidSignatureError extends Error {
    final int idx;
    public InvalidSignatureError(String message) {
        super(message);
        idx = -1;
    }

    public InvalidSignatureError(String message, int idx) {
        super(message);
        this.idx = idx;

    }
}
