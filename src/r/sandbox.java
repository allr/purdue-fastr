package r;

import r.data.RDouble;
import r.data.internal.DoubleImpl;

public class sandbox {

    static final int size = 100000000;
    static final int vsize = 10;




    public static Object f() {
        DoubleImpl aa = new DoubleImpl(vsize);
        DoubleImpl bb = new DoubleImpl(vsize);
        Object result = null;
        for (int i = 0; i < size; ++i) {
            final double[] l = aa.getContent();
            final double[] r = bb.getContent();
            final double[] res = new double[l.length];
            for (int j = 0; j < l.length; ++j) {
                double a = l[j];
                double b = r[j];
                double c = a + b;
                if (RDouble.RDoubleUtils.isNA(c)) {
                    if (RDouble.RDoubleUtils.isNA(a) || RDouble.RDoubleUtils.isNA(b)) {
                        res[j] = RDouble.NA;
                    }
                } else {
                    res[j] = c;
                }
            }
            result = RDouble.RDoubleFactory.getFor(res, null, null, null);
        }
        return result;
    }



    public static void main(String[] args) {
        for (int i = 0; i < 10; ++i) {
            long t = System.currentTimeMillis();
            f();
            t = System.currentTimeMillis() - t;
            System.out.println("Iteration "+i+" took "+(t/1000.0)+" [s]");
        }
    }

}
