package r.builtins;

import java.util.*;

import r.*;
import r.data.*;

public class Primitives {

    public static final boolean STATIC_LOOKUP = false;

    private static Map<RSymbol, PrimitiveEntry> map;
    static {
        map = new HashMap<>();
        initializePrimitives();
    }

    public static void initializePrimitives() {
        map.clear();
        add(":", 2, 2, Colon._);
        add("+", 1, 2, OpAdd._);
        add("-", 1, 2, OpSub._);
        add("*", 2, 2, OpMult._);
        add("/", 2, 2, OpDiv._);
        add("==", 2, 2, OpEq._);
        add("!=", 2, 2, OpNe._);
        add(">", 2, 2, OpGt._);
        add("<", 2, 2, OpLt._);
        add("<=", 2, 2, OpLe._);
        add(">=", 2, 2, OpGe._);
        add("%%", 2, 2, OpMod._);
        add("%/%", 2, 2, OpIntDiv._);
        add("^", 2, 2, OpPow._);
        add("%*%", 2, 2, OpMatMult._);
        add("%o%", 2, 2, OpOuterMult._);
        add("|", 2, 2, OpOrElem._);
        add("&", 2, 2, OpAndElem._);
        add("||", 2, 2, OpOr._);
        add("&&", 2, 2, OpAnd._);
        add("!", 1, 1, OpNot._);
        add("abs", 1, 1, Abs._);
        add("aperm", 2, 3, Aperm._);
        add("array", 0, 3, Array._);
        add("assign", 2, 6, Assign._);
        add("as.character", 0, -1, AsCharacter._);
        add("as.complex", 0, -1, AsComplex._);
        add("as.double", 0, -1, AsDouble._);
        add("as.environment", 1, 1, AsEnvironment._);
        add("as.integer", 0, -1, AsInteger._);
        add("as.logical", 0, -1, AsLogical._);
        add("as.raw", 1, 1, AsRaw._);
        add("as.vector", 1, 2, AsVector._);
        add("attr", 2, 3, Attr._);
        add("attr<-", 3, 3, AttrAssign._);
        add("attributes", 1, 1, Attributes._);
        add("attributes<-", 2, 2, AttributesAssign._);
        add("c", 0, -1, C._);
        add("cat", 0, -1, Cat._);
        add("character", 0, 1, Character._);
        add("close", 1, -1, Close._);
        add("colMeans", 1, 3, ColMeans._);
        add("colSums", 1, 3, ColSums._);
        add("cumsum", 1, 1, Cumsum._);
        add("diag<-", 2, 2, DiagAssign._);
        add("dim", 1, 1, Dim._);
        add("double", 0, 1, Double._);
        add("get", 1, 5, Get._);
        add("gregexpr", 2, 6, Gregexpr._);
        add("gsub", 3, 7, Gsub._);
        add("eigen", 1, 4, Eigen._);
        add("emptyenv", 0, 0, Emptyenv._);
        add("exists", 1, 6, Exists._);
        add("exp", 1, 1, Exp._);
        add("file", 0, 5, File._);
        add("flush", 1, 1, Flush._);
        add("integer", 0, 1, Integer._);
        add("is.character", 1, 1, IsCharacter._);
        add("is.complex", 1, 1, IsComplex._);
        add("is.double", 1, 1, IsDouble._);
        add("is.integer", 1, 1, IsInteger._);
        add("is.list", 1, 1, IsList._);
        add("is.logical", 1, 1, IsLogical._);
        add("is.null", 1, 1, IsNull._);
        add("is.numeric", 1, 1, IsNumeric._);
        add("is.na", 1, 1, IsNA._);
        add("is.raw", 1, 1, IsRaw._);
        add("lapply", 2, -1, LApply._);
        add("length", 1, 1, Length._);
        add("length<-", 2, 2, LengthAssign._);
        add("list", 0, -1, List._);
        add("log", 1, 2, Log._);
        add("log10", 1, 1, Log10._);
        add("log2", 1, 1, Log2._);
        add("logical", 0, 1, Logical._);
        add("lower.tri", 1, 2, LowerTri._);
        add("ls", 0, 5, Ls._);
        add("matrix", 0, 5, Matrix._);
        add("max", 0, -1, Max._);
        add("min", 0, -1, Min._);
        add("names", 1, 1, Names._);
        add("names<-", 2, 2, NamesAssign._);
        add("nchar", 1, 3, Nchar._);
        add("ncol", 1, 1, Ncol._);
        add("new.env", 0, 3, Newenv._);
        add("nrow", 1, 1, Nrow._);
        add("options", 0, -1, Options._);
        add("order", 0, -1, Sort._);
        add("outer", 2, -1, Outer._);
        add("paste", 0, -1, Paste._);
        add("pipe", 1, 3, Pipe._);
        add("raw", 0, 1, Raw._);
        add("readLines", 0, 5, ReadLines._);
        add("regexpr", 2, 6, Regexpr._);
        add("rep", 0, -1, Rep._);
        add("rep.int", 2, 2, RepInt._);
        add("return", 0, 1, Return._);
        add("rev", 1, 1, Rev._);
        add("rev.default", 1, 1, Rev._);
        add("rowMeans", 1, 3, RowMeans._);
        add("rowSums", 1, 3, RowSums._);
        add("sapply", 2, -1, SApply._);
        add("scan", 0, 4, Scan._);
        add("seq", 0, -1, Seq._); // in fact seq.default (and only part of it)
        add("seq.default", 0, -1, Seq._);
        add("strsplit", 1, 5, Strsplit._);
        add("sub", 3, 7, Sub._);
        add("substr", 3, 3, Substr._);
        add("substring", 2, 3, Substring._);
        add("sum", 0, -1, Sum._);
        add("sqrt", 1, 1, Sqrt._);
        add("t", 1, 1, T._);
        add("t.default", 1, 1, T._);
        add("tolower", 1, 1, Tolower._);
        add("toupper", 1, 1, Toupper._);
        add("typeof", 1, 1, Typeof._);
        add("unlist", 1, 3, Unlist._);
        add("upper.tri", 1, 2, UpperTri._);
        add("which", 1, 3, Which._);
        add("writeBin", 2, 5, WriteBin._);
        add("commandArgs", 0, 1, CommandArgs._);
        // fastr specific
        add("__inspect", 1, 1, Inspect._);
    }

    public static boolean hasCallFactory(final RSymbol name, final RFunction enclosing) {
        return Primitives.get(name, enclosing) != null;
    }

    public static CallFactory getCallFactory(RSymbol name, RFunction enclosing) {
        final PrimitiveEntry pe = Primitives.get(name, enclosing);
        if (pe == null) {
            return null;
        } else {
            return pe.factory;
        }
    }

    public static RBuiltIn getBuiltIn(RSymbol name, RFunction enclosing) {
        final PrimitiveEntry pe = Primitives.get(name, enclosing);
        if (pe == null) {
            return null;
        } else {
            return pe.builtIn;
        }
    }

    public static PrimitiveEntry get(RSymbol name, RFunction fun) {
        PrimitiveEntry pe = get(name);
        if (pe != null && fun != null && fun.isInWriteSet(name)) { // TODO: fix these checks
            Utils.debug("IGNORING over-shadowing of built-in " + name.pretty() + "!!!");
            throw Utils.nyi(); // TODO the case when a primitive is shadowed by a local symbol
            // FIXME: but shouldn't we keep traversing recursively through all frames of the caller?
            // FIXME: also, what about reflections?
        }
        return pe;
    }

    public static PrimitiveEntry get(RSymbol name) {
        return map.get(name);
    }

    private static void add(String name, int minArgs, int maxArgs, CallFactory body) {
        // if (body.name != null && !body.name.name().equals(name)) System.err.println("name " + name + " != " + body.name.name());
        // else System.err.print(".");
        //if (minArgs != body.minParameters) System.err.println("name " + name + " " + minArgs + " != " + body.minParameters);
        if (maxArgs == -1) {
            if (body.maxParameters != java.lang.Integer.MAX_VALUE) System.err.println("name " + name + " " + maxArgs + " != " + body.maxParameters);
        } else if (maxArgs != body.maxParameters) System.err.println("name " + name + " " + maxArgs + " != " + body.maxParameters);
        add(name, minArgs, maxArgs, body, PrimitiveEntry.PREFIX);
    }

    private static void add(String name, int minArgs, int maxArgs, CallFactory body, int prettyPrint) {
        RSymbol sym = RSymbol.getSymbol(name);
        map.put(sym, new PrimitiveEntry(sym, minArgs, maxArgs, body, prettyPrint));
    }
}
