package r;

import java.util.concurrent.*;

/** Class holding the elements for the usage of Fork-Join framework in large vector arithmetics.
 *
 */
public class FJ {

    // TODO - maybe change the initialization to option, but we would loose the final
    public static final boolean ENABLED = true;

    public static final int THRESHOLD = 125;

    public static ForkJoinPool pool = ENABLED ? new ForkJoinPool() : null;


    public static <T extends RecursiveAction> T invoke(T task) {
        pool.invoke(task);
        return task;
    }


}
