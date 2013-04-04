package r.gnur;

public class GNUR {

    // nmath
    public static native double rnorm(double mu, double sigma);
    public static native boolean rnorm(double[] res, int n, double mu, double sigma);
    public static native boolean rnormNonChecking(double[] res, int n, double mu, double sigma);
    public static native boolean rnormStd(double[] res, int n);

    public static native boolean runifStd(double[] res, int n);

    // appl
    public static native void fft_factor(int n, int[] maxf, int[] maxp); // FIXME: could also use intW or similar instead of int[]
    public static native int fft_work(double[] ab, int nseg, int n, int nspn, int isn, double[] work, int[] iwork);

    static {
        System.loadLibrary("gnurglue");
    }

}
