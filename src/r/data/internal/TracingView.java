package r.data.internal;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import r.*;
import r.data.*;

public interface TracingView {

    public static final boolean VIEW_TRACING = false;

    public static final boolean SILENT = false; // good for JUnit tests

    public static final int SITES_TO_PRINT = 5;
    public static final String THIS_FILE_NAME = "TracingView.java";
    public static PrintStream nullPS = new PrintStream(new OutputStream() {

        @Override
        public void write(int b) {
        }

    });
    public static PrintStream ps = SILENT ? nullPS : System.err;


    public ViewTrace getTrace();

    public static class Site {
        final StackTraceElement[] site;
        int hashCode;
        final int offset; // offset to first element that we care about (for performance of tracing)

        public Site(StackTraceElement[] site) {
            this.site = site;

            int i = 0;
            while(!THIS_FILE_NAME.equals(site[i].getFileName())) {
                i++;
            }
            while(THIS_FILE_NAME.equals(site[i].getFileName())) {
                i++;
            }
            assert Utils.check(i < site.length - 1);
            this.offset = i;
        }

        public Site() {
            this(Thread.currentThread().getStackTrace());
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Site)) {
                return false;
            }
            Site other = (Site) o;
            if (other.hashCode() != hashCode()) {
                return false;
            }
            StackTraceElement[] osite = other.site();
            if (site.length != osite.length) {
                return false;
            }
            for (int i = offset; i < site.length; i++) {
                if (!site[i].equals(osite[i])) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int hashCode() {
            if (hashCode == 0) {
                int res = 1 + site.length * 17;
                int max = Math.max(site.length, 10);
                for(int i = offset; i < max; i++) {
                    StackTraceElement e = site[i];
                    int lineNumber = e.getLineNumber();
                    res = res * 19 + 31 * lineNumber ;
                }
                hashCode = res;
            }
            return hashCode;
        }

        public StackTraceElement[] site() {
            return site;
        }


        private static final String[] skipMethodsNames = new String[] { "getRAny", "getString", "getComplex", "getReal", "getImag", "getDouble",
            "getInt", "getLogical", "getRaw", "boxedGet", "getRef", "copy"
        };
        private static final HashSet<String> skipMethods = new HashSet<String>(Arrays.asList(skipMethodsNames));
        public static void printSite(Site s) {
            if (s == null) {
                ps.print("(null)");
                return;
            }
            StackTraceElement[] st = s.site();

            int interestingSitesPrinted = 0;
            for(int i = s.offset; i < st.length; i++) {
                StackTraceElement e = st[i];
                String fileName = e.getFileName();
                if (THIS_FILE_NAME.equals(fileName)) {
                    continue;
                }
                ps.print( " " + e.getMethodName() + "(" + e.getFileName() + ":" + e.getLineNumber() + ")");
                interestingSitesPrinted++;
                if (interestingSitesPrinted <= SITES_TO_PRINT) {
                    continue;
                }
                if (fileName == null || "View.java".equals(fileName)) {
                    continue;
                }
                String methodName = e.getMethodName();
                if (skipMethods.contains(methodName)) {
                    continue;
                }
                break;
            }
        }

        public static void printElement(Site s, int index) {
            if (s == null || index >= s.site().length) {
                ps.print("(null)");
            } else {
                StackTraceElement e = s.site()[index];
                ps.print( e.getMethodName() + " (" + e.getFileName() + ":" + e.getLineNumber() + ")");
            }
        }

        public static void printElements(Site s, int index, int nelems) {
            if (s == null || index >= s.site().length) {
                ps.print("(null)");
            } else {
                for (int i = 0; i < nelems; i++) {
                    int j = index + i;
                    if (j >= s.site().length) {
                        break;
                    }
                    if (i > 0) {
                        ps.print(", ");
                    }
                    StackTraceElement e = s.site()[j];
                    ps.print( e.getMethodName() + " (" + e.getFileName() + ":" + e.getLineNumber() + ")");
                }
            }
        }
    }

    public static class ViewTrace {
        final RArray realView;
        ViewTrace parentView;

        int[] getCounts;
        int materializeCount;
        int getCount;
        final Site allocationSite;
        Site firstGetSite;
        Site firstMaterializeSite;

        HashSet<Site> useSites;

        static HashSet<ViewTrace> viewsRegistry = new HashSet<ViewTrace>();

        public ViewTrace(RArray real) {
            getCounts = new int[real.size()];
            allocationSite = new Site();
            useSites = new HashSet<Site>();
            realView = real;
            viewsRegistry.add(this);
            linkChildren(real, this);
        }

        private static Field[] getAllFields(Class cls) {
            ArrayList<Field> res = new ArrayList<>();
            Class c = cls;
            while (c != View.class) {
                assert Utils.check(c != null);
                res.addAll(Arrays.asList(c.getDeclaredFields()));
                c = c.getSuperclass();
            }
            return res.toArray(new Field[res.size()]);
        }

        private static void linkChildren(RArray parentRealView, ViewTrace parentTrace) {
            Class viewClass = parentRealView.getClass();
            Field[] fields = getAllFields(viewClass);
            for (Field f : fields) {
                if (f.isSynthetic()) {
                    continue;
                }
                Class fieldClass = f.getType();
                if (RArray.class.isAssignableFrom(fieldClass)) {
                    try {
                        f.setAccessible(true);
                        Object o = f.get(parentRealView);
                        if (o instanceof TracingView) {
                            ((TracingView) o).getTrace().parentView = parentTrace;
                        }
                    } catch (IllegalAccessException e) {
                        assert Utils.check(false, "can't read a view field " + e);
                    }
                }
            }
        }

        private Site updateUseSite() {
            Site here = new Site();
            useSites.add(here);
            return here;
        }

        public void get(int i) {
            Site here = updateUseSite();
            if (getCount == 0) {
                firstGetSite = here;
            }
            getCount++;
            getCounts[i]++;
        }

        public void materialize() {
            Site here = updateUseSite();
            if (materializeCount == 0) {
                firstMaterializeSite = here;
            }
            materializeCount++;
        }

        public int unusedElements() {
            int unused = 0;
            for(int g : getCounts) {
                if (g == 0) {
                    unused++;
                }
            }
            return unused;
        }

        public int redundantGets() {
            int redundant = 0;
            for(int g : getCounts) {
                if (g > 1) {
                    redundant += g - 1;
                }
            }
            return redundant;
        }



        private ViewTrace getRootView() {
            ViewTrace v = this;
            while(v.parentView != null) {
                v = v.parentView;
            }
            return v;
        }


        private static void indent(int depth) {
            for(int i = 0; i < depth; i++) {
                ps.print(" ");
            }
        }

        private static void dumpView(int depth, ViewTrace trace) {
            printed.add(trace);

            ps.println(trace.realView + " size = " + trace.realView.size());
            indent(depth);
            ps.print("    allocationSite =");
//            Site.printElement(trace.allocationSite, 4);
            Site.printSite(trace.allocationSite);
            ps.println();

            int unused = trace.unusedElements();
            int redundant = trace.redundantGets();

            Site[] useSites = trace.useSites.toArray(new Site[trace.useSites.size()]);
            if (useSites.length == 1) {
                indent(depth);
                ps.print("    singleUseSite = US");
//                Site.printElements(useSites[0], 4, 3);
                Site.printSite(useSites[0]);

                if (trace.getCount > 0) {
                    ps.println(" (get)");
                } else {
                    ps.println(" (materialize)");
                }

            } else if (trace.getCount > 0) {
                indent(depth);
                ps.print("    firstGetSite =");
//                Site.printElement(trace.firstGetSite, 3);
                Site.printSite(trace.firstGetSite);
                ps.println();
                if (trace.materializeCount == 0) {
                    if (unused > 0) {
                        indent(depth);
                        ps.println("    unusedElements = " + unused);
                    }
                    if (redundant > 0) {
                        indent(depth);
                        ps.println("    redundantGets = " + redundant + " (no materialize)");
                    }
                }
            } else {
                if (trace.materializeCount == 0) {
                    indent(depth);
                    ps.println("    UNUSED");
                } else {
                    if (trace.getCount > 0) {
                        indent(depth);
                        ps.println("    extraGets = " + trace.getCount + " (in addition to materialize)");
                    }
                }
            }
            if (trace.materializeCount > 0 && useSites.length != 1) {
                indent(depth);
                ps.print("    firstMaterializeSite =");
//                Site.printElement(trace.firstMaterializeSite, 3);
                Site.printSite(trace.firstMaterializeSite);
                ps.println();
            }
            if (useSites.length != 1) {
                indent(depth);
                ps.println("    useSites (" + useSites.length + "):");
                for (Site s : useSites) {
                    indent(depth);
                    ps.print("        US");
//                    Site.printElements(s, 4, 10);
                    Site.printSite(s);
                    ps.println();
                }
            }

            ps.println();
            RArray view = trace.realView;
            Class viewClass = view.getClass();
            Field[] fields = getAllFields(viewClass);
            boolean printedField = false;

            for(Field f : fields) {
                if (f.isSynthetic()) {
                    continue;
                }
                Class fieldClass = f.getType();
                if (RArray.class.isAssignableFrom(fieldClass)) {
                    continue; // these later
                }
                indent(depth);
                ps.print("    " + f.getName() + " ");
                try {
                    f.setAccessible(true);
                    ps.println(f.get(view));
                    printedField = true;
                } catch (IllegalAccessException e) {
                    assert Utils.check(false, "can't read a view field " + e);
                }
            }

            boolean printNewline = printedField;
            for(Field f : fields) {
                if (f.isSynthetic()) {
                    continue;
                }
                Class fieldClass = f.getType();
                if (!RArray.class.isAssignableFrom(fieldClass)) {
                    continue;
                }
                if (printNewline) {
                    ps.println();
                    printNewline = false;
                }
                indent(depth);
                ps.print("    " + f.getName() + " ");
                try {
                    f.setAccessible(true);
                    Object o = f.get(view);
                    if (o instanceof TracingView) {
                        ps.print("VIEW ");
                        TracingView child = (TracingView) o;
                        dumpView(depth + 2, child.getTrace());
                    } else {
                        ps.print("ARRAY " + o + " size = " + ((RArray)o).size());
                        if (o instanceof View) {
                            ps.println("MISSED VIEW " + o);
                        }
                    }
                    ps.println();
                } catch (IllegalAccessException e) {
                    assert Utils.check(false, "can't read a view field " + e);
                }
            }
        }

        static HashSet<ViewTrace> printed;
        public static void printGlobalStats() {
            printed = new HashSet<ViewTrace>();
            ps.println("Global views statistics ------------------- \n");
            for(ViewTrace trace : viewsRegistry) {
                if (printed.contains(trace)) {
                    continue;
                }
                ViewTrace v = trace.getRootView();
                ps.print("ROOT ");
                dumpView(0, v);
                ps.println();
            }
        }

        public static <T extends RArray> T trace(RArray orig) { // FIXME: verify this has no overhead when tracing is disabled
            if (VIEW_TRACING) {
                RArray res;
                if (orig instanceof RList) {
                    res = new RListTracingView((RList) orig);
                } else if (orig instanceof RString) {
                    res = new RStringTracingView((RString) orig);
                } else if (orig instanceof RComplex) {
                    res = new RComplexTracingView((RComplex) orig);
                } else if (orig instanceof RDouble) {
                    res = new RDoubleTracingView((RDouble) orig);
                } else if (orig instanceof RInt) {
                    res = new RIntTracingView((RInt) orig);
                } else if (orig instanceof RLogical) {
                    res = new RLogicalTracingView((RLogical) orig);
                } else if (orig instanceof RRaw) {
                    res = new RRawTracingView((RRaw) orig);
                } else {
                    assert Utils.check(false, "missed view type");
                    res = orig;
                }
                return (T) res;
            } else {
                return (T) orig;
            }
        }

    }

    public static class RListTracingView extends View.RListProxy<RList> implements RList, TracingView {

        private ViewTrace trace;

        public RListTracingView(RList orig) {
            super(orig);
            trace = new ViewTrace(orig);
        }

        @Override
        public ViewTrace getTrace() {
            return trace;
        }

        @Override
        public RAny getRAny(int i) {
            trace.get(i);
            return orig.getRAny(i);
        }

    }

    public static class RStringTracingView extends View.RStringProxy<RString> implements RString, TracingView {

        private ViewTrace trace;

        public RStringTracingView(RString orig) {
            super(orig);
            trace = new ViewTrace(orig);
        }

        @Override
        public ViewTrace getTrace() {
            return trace;
        }

        @Override
        public String getString(int i) {
            trace.get(i);
            return orig.getString(i);
        }

    }

    public static class RComplexTracingView extends View.RComplexProxy<RComplex> implements RComplex, TracingView {

        private ViewTrace trace;

        public RComplexTracingView(RComplex orig) {
            super(orig);
            trace = new ViewTrace(orig);
        }

        @Override
        public ViewTrace getTrace() {
            return trace;
        }

        @Override
        public double getReal(int i) {  // TODO: perhaps special handling for complex numbers? (will always report redundancy)
            trace.get(i);
            return orig.getReal(i);
        }

        @Override
        public double getImag(int i) {  // TODO: perhaps special handling for complex numbers? (will always report redundancy)
            trace.get(i);
            return orig.getImag(i);
        }

        @Override
        public RComplex materialize() {
            trace.materialize();
            return orig.materialize();
        }

    }

    public static class RDoubleTracingView extends View.RDoubleProxy<RDouble> implements RDouble, TracingView {

        private ViewTrace trace;

        public RDoubleTracingView(RDouble orig) {
            super(orig);
            trace = new ViewTrace(orig);
        }

        @Override
        public ViewTrace getTrace() {
            return trace;
        }

        @Override
        public double getDouble(int i) {
            trace.get(i);
            return orig.getDouble(i);
        }

        @Override
        public RDouble materialize() {
            trace.materialize();
            return orig.materialize();
        }

        @Override
        public double sum(boolean narm) {
            // TODO: trace
            return orig.sum(narm);
        }

    }

    public static class RIntTracingView extends View.RIntProxy<RInt> implements RInt, TracingView {

        private ViewTrace trace;

        public RIntTracingView(RInt orig) {
            super(orig);
            trace = new ViewTrace(orig);
        }

        @Override
        public ViewTrace getTrace() {
            return trace;
        }

        @Override
        public int getInt(int i) {
            trace.get(i);
            return orig.getInt(i);
        }

        @Override
        public RInt materialize() {
            trace.materialize();
            return orig.materialize();
        }

    }

    public static class RLogicalTracingView extends View.RLogicalProxy<RLogical> implements RLogical, TracingView {

        private ViewTrace trace;

        public RLogicalTracingView(RLogical orig) {
            super(orig);
            trace = new ViewTrace(orig);
        }

        @Override
        public ViewTrace getTrace() {
            return trace;
        }

        @Override
        public int getLogical(int i) {
            trace.get(i);
            return orig.getLogical(i);
        }

        @Override
        public RLogical materialize() {
            trace.materialize();
            return orig.materialize();
        }

    }

    public static class RRawTracingView extends View.RRawProxy<RRaw> implements RRaw, TracingView {

        private ViewTrace trace;

        public RRawTracingView(RRaw orig) {
            super(orig);
            trace = new ViewTrace(orig);
        }

        @Override
        public ViewTrace getTrace() {
            return trace;
        }

        @Override
        public byte getRaw(int i) {
            trace.get(i);
            return orig.getRaw(i);
        }

        @Override
        public RRaw materialize() {
            trace.materialize();
            return orig.materialize();
        }

    }

}
