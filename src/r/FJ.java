package r;

import java.util.concurrent.*;

/** Class holding the elements for the usage of Fork-Join framework in large vector arithmetics.
 *
 * To enable fork/join for large vectors, use fastr with --fj command line option.
 */
public class FJ {

    public static final boolean ENABLED = false;

    public static final int THRESHOLD = 100;

    public static ForkJoinPool pool;



    public static void initialize() {
        pool = new ForkJoinPool();
    }

    public static <T extends RecursiveAction> T invoke(T task) {
        pool.invoke(task);
        return task;
    }

}
