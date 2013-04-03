package r.gnur;

public class GNUR {

    public static native double rnorm(double mu, double sigma);
    public static native boolean rnorm(double[] res, int n, double mu, double sigma);
    public static native boolean rnormNonChecking(double[] res, int n, double mu, double sigma);

    static {
        System.loadLibrary("gnurglue");
    }

}
