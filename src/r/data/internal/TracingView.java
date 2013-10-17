package r.data.internal;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import r.*;
import r.data.*;

public interface TracingView {

    public static final boolean VIEW_TRACING = false;  // master switch
    public static final boolean SILENT = false; // good for JUnit tests

    public static final boolean PRINT_INDIVIDUAL_VIEWS = false;
    public static final boolean PRINT_VIEW_PATTERNS = true;

    public static final boolean TRACE_USE_SITES = false;
    public static final boolean TRACE_SINGLE_USE_SITE = false;
    public static final boolean TRACE_FIRST_GET_SITE = false;
    public static final boolean TRACE_FIRST_MATERIALIZE_SITE = false;
    public static final boolean TRACE_FIRST_SUM_SITE = false;
    public static final boolean TRACE_ALLOCATION_SITE = false;

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

        public static void printSite(Site s, StringBuilder out) {
            if (s == null) {
                out.append("(null)");
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
                out.append( " " + e.getMethodName() + "(" + e.getFileName() + ":" + e.getLineNumber() + ")");
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

        public static void printSite(Site s) {
            StringBuilder b = new StringBuilder();
            printSite(s, b);
            ps.println(b.toString());
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
        int sumCount;
        final Site allocationSite;
        Site firstGetSite;
        Site firstMaterializeSite;
        Site firstSumSite;
        Site singleUseSite;
        boolean multipleUseSites;

        HashSet<Site> useSites;

        static HashSet<ViewTrace> viewsRegistry = new HashSet<ViewTrace>();

        public ViewTrace(RArray real) {
            getCounts = new int[real.size()];
            allocationSite = TRACE_ALLOCATION_SITE ? new Site() : null;
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
            if (TRACE_USE_SITES) {
                Site here = new Site();
                useSites.add(here);
                return here;
            } else if (TRACE_SINGLE_USE_SITE) {
                if (multipleUseSites) {
                    return null;
                } else {
                    Site here = new Site();
                    if (singleUseSite == null) {
                        singleUseSite = here;
                    } else {
                        if (!here.equals(singleUseSite)) {
                            multipleUseSites = true;
                            singleUseSite = null;
                        }
                    }
                    return here;

                }
            } else {
                return null;
            }
        }

        private static Site currentSite(Site here) {
            if (here != null) {
                return here;
            }
            return new Site();
        }

        public void get(int i) {
            Site here = updateUseSite();
            if (TRACE_FIRST_GET_SITE && getCount == 0) {
                firstGetSite = currentSite(here);
            }
            getCount++;
            getCounts[i]++;
        }

        public void materialize() {
            Site here = updateUseSite();
            if (TRACE_FIRST_MATERIALIZE_SITE && materializeCount == 0) {
                firstMaterializeSite = currentSite(here);
            }
            materializeCount++;
        }

        public void sum() {
            Site here = updateUseSite();
            if (TRACE_FIRST_SUM_SITE && sumCount == 0) {
                firstSumSite = currentSite(here);
            }
            sumCount++;

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
            printedIndividualViews.add(trace);

            ps.println(trace.realView + " size = " + trace.realView.size());
            if (TRACE_ALLOCATION_SITE) {
                indent(depth);
                ps.print("    allocationSite =");
                Site.printSite(trace.allocationSite);
                ps.println();
            }

            int unused = trace.unusedElements();
            int redundant = trace.redundantGets();

            boolean singleUse;
            Site[] useSites;

            if (TRACE_USE_SITES) {
                useSites = trace.useSites.toArray(new Site[trace.useSites.size()]);
                singleUse = (useSites.length == 1);
            } else if (TRACE_SINGLE_USE_SITE){
                useSites = null;
                singleUse = !trace.multipleUseSites;
            } else {
                useSites = null;
                singleUse = false;
            }
            if (singleUse) {
                indent(depth);
                ps.print("    singleUseSite = US");
                Site.printSite(useSites != null ? useSites[0] : trace.singleUseSite);

                if (trace.getCount > 0) {
                    ps.println(" (get)");
                } else if (trace.sumCount > 0) {
                    ps.println(" (sum)");
                } else {
                    ps.println(" (materialize)");
                }

            } else if (trace.getCount > 0) {
                if (TRACE_FIRST_GET_SITE) {
                    indent(depth);
                    ps.print("    firstGetSite =");
                    Site.printSite(trace.firstGetSite);
                    ps.println();
                }
                if (trace.materializeCount == 0 && trace.sumCount == 0) {
                    if (unused > 0) {
                        indent(depth);
                        ps.println("    unusedElements = " + unused);
                    }
                    if (redundant > 0) {
                        indent(depth);
                        ps.println("    redundantGets = " + redundant + " (no materialize, sum)");
                    }
                }
            } else {
                if (trace.materializeCount == 0 && trace.sumCount == 0) {
                    indent(depth);
                    ps.println("    UNUSED");
                } else {
                    indent(depth);
                    ps.println("    materializeCount = " + trace.materializeCount + " sumCount = " + trace.sumCount + " getCount = " + trace.getCount);
                }
            }
            if (TRACE_FIRST_MATERIALIZE_SITE && trace.materializeCount > 0 && !singleUse) {
                indent(depth);
                ps.print("    firstMaterializeSite =");
                Site.printSite(trace.firstMaterializeSite);
                ps.println();
            }
            if (TRACE_FIRST_SUM_SITE && trace.sumCount > 0 && !singleUse) {
                indent(depth);
                ps.print("    firstSumSite =");
                Site.printSite(trace.firstSumSite);
                ps.println();
            }
            if (TRACE_USE_SITES) {
                if (useSites.length != 1) {
                    indent(depth);
                    ps.println("    useSites (" + useSites.length + "):");
                    for (Site s : useSites) {
                        indent(depth);
                        ps.print("        US");
                        Site.printSite(s);
                        ps.println();
                    }
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

        static HashSet<ViewTrace> printedIndividualViews;
        public static void printIndividualViews() {
            printedIndividualViews = new HashSet<ViewTrace>();
            ps.println("Individual views statistics ------------------- \n");
            for(ViewTrace trace : viewsRegistry) {
                if (printedIndividualViews.contains(trace)) {
                    continue;
                }
                ViewTrace v = trace.getRootView();
                ps.print("ROOT ");
                dumpView(0, v);
                ps.println();
            }
            printViewPatterns();
            printedIndividualViews = null;
        }

        public static void printGlobalStats() {
            if (PRINT_INDIVIDUAL_VIEWS) {
                printIndividualViews();
            }
            if (PRINT_VIEW_PATTERNS) {
                printViewPatterns();
            }
        }

        private static void indent(int depth, StringBuilder p) {
            for(int i = 0; i < depth; i++) {
                p.append(" ");
            }
        }
        private static void extractViewPattern(int depth, ViewTrace trace, StringBuilder p) {
            if (!processedViewsForPatterns.add(trace)) {
               p.append("(ALIASED) ");
            }

            p.append(trace.realView.getClass() + " size = " + trace.realView.size() + "\n");
            indent(depth, p);

            p.append("    use:");
            if (trace.materializeCount == 0 && trace.sumCount == 0 && trace.getCount == 0) {
                p.append(" UNUSED");
            } else {
                if (trace.getCount > 0) {
                    p.append(" get");
                }
                if (trace.materializeCount > 0) {
                    p.append(" materialize");
                }
                if (trace.sumCount > 0) {
                    p.append(" sum");
                }
            }
            p.append("\n");

            if (false) {
                p.append("    allocationSite =");
                Site.printSite(trace.allocationSite, p);
                p.append("\n");
                indent(depth, p);
            }

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
                indent(depth, p);
                p.append("    " + f.getName() + " ");
                try {
                    f.setAccessible(true);
                    Object o = f.get(view);
                    p.append(o == null ? "null (" + fieldClass + ")" : o.getClass());
                    p.append("\n");
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
                    p.append("\n");
                    printNewline = false;
                }
                indent(depth, p);
                p.append("    " + f.getName() + " ");
                try {
                    f.setAccessible(true);
                    Object o = f.get(view);
                    if (o instanceof TracingView) {
                        p.append("VIEW ");
                        TracingView child = (TracingView) o;
                        extractViewPattern(depth + 2, child.getTrace(), p);
                    } else {
                        p.append("ARRAY " + o.getClass() + " size = " + ((RArray)o).size());
                        if (o instanceof View) {
                            ps.println("MISSED VIEW " + o);
                        }
                    }
                    p.append("\n");
                } catch (IllegalAccessException e) {
                    assert Utils.check(false, "can't read a view field " + e);
                }
            }
        }

        static HashSet<ViewTrace> processedViewsForPatterns;
        public static void printViewPatterns() {
            processedViewsForPatterns = new HashSet<ViewTrace>();
            HashMap<String, Integer> viewPatterns = new HashMap<String, Integer>();

            ps.println("ViewPatterns ------------------- \n");
            for(ViewTrace trace : viewsRegistry) {
                if (processedViewsForPatterns.contains(trace)) {
                    continue;
                }
                ViewTrace v = trace.getRootView();
                StringBuilder p = new StringBuilder();
                p.append("PATTERN ");
                extractViewPattern(0, v, p);
                p.append("\n");
                String pattern = p.toString();
                Integer count = viewPatterns.get(pattern);
                if (count == null) {
                    viewPatterns.put(pattern, 1);
                } else {
                    viewPatterns.put(pattern, count + 1);
                }
            }
            processedViewsForPatterns = null;
            for (Map.Entry<String, Integer> e : viewPatterns.entrySet()) {
                ps.print("(" + e.getValue() + ") ");
                ps.println(e.getKey());
            }
        }

        public static <T extends RArray> T trace(RArray orig) {
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

        @Override
        public ListImpl materialize() {
            trace.materialize();
            return orig.materialize();
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

        @Override
        public RString materialize() {
            trace.materialize();
            return orig.materialize();
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
            trace.sum();
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
