package r.builtins;

import java.io.*;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

import com.oracle.truffle.api.frame.*;
import com.oracle.truffle.api.nodes.*;

/**
 * "cat" Outputs the objects, concatenating the representations. cat performs much less conversion than print.
 * 
 * <pre>
 * ... -- R objects
 * file -- A connection, or a character string naming the file to print to. If "" (the default), cat prints to the 
 *      standard output connection, the console unless redirected by sink. If it is "|cmd", the output is piped to the 
 *      command given by cmd, by opening a pipe connection.
 * sep -- a character vector of strings to append after each element.
 * fill -- a logical or (positive) numeric controlling how the output is broken into successive lines. If FALSE (default), 
 *        newlines created explicitly by "\n" are printed. Otherwise, the output is broken into lines with print width equal
 *         to the option width if fill is TRUE, or the value of fill if this is numeric. Non-positive fill values are
 *          ignored, with a warning.
 * labels -- character vector of labels for the lines printed. Ignored if fill is FALSE.
 * append --logical. Only used if the argument file is the name of file (and not a connection or "|cmd"). If TRUE output
 *           will be appended to file; otherwise, it will overwrite the contents of file.
 * </pre>
 */
// FIXME: not quite the GNU-R output particularly for numbers; re-visit if some code could be removed once we have
// fully GNU-R string representation of numerics
final class Cat extends CallFactory {

    static final CallFactory _ = new Cat("cat", new String[]{"...", "file", "sep", "fill", "labels", "append"}, new String[]{});

    Cat(String name, String[] parameters, String[] required) {
        super(name, parameters, required);
    }

    private static final int DEFAULT_BUFFER_SIZE = 4096;
    private static char[] buffer = new char[DEFAULT_BUFFER_SIZE]; // FIXME: This is not thread safe!!! 

    static String catElement(RArray v, int i) { // TODO: replace this by virtual calls, even cat can be important for performance (e.g. fasta)
        if (v instanceof RDouble) { return Convert.prettyNA(Convert.double2string(((RDouble) v).getDouble(i))); }
        if (v instanceof RInt) { return Convert.prettyNA(Convert.int2string(((RInt) v).getInt(i))); }
        if (v instanceof RLogical) { return Convert.prettyNA(Convert.logical2string(((RLogical) v).getLogical(i))); }
        if (v instanceof RString) { return ((RString) v).getString(i); }
        if (v instanceof RComplex) { return catElement((RComplex) v, i); }
        if (v instanceof RRaw) { return Convert.raw2string(((RRaw) v).getRaw(i)); }
        throw Utils.nyi("unsupported type");
    }

    static String catElement(RComplex v, int i) {
        return Convert.prettyNA(Convert.complex2string(v.getReal(i), v.getImag(i)));
    }

    static void genericCat(PrintWriter out, RAny[] args, int sepArgPos, ASTNode ast) {
        RString sep = null;
        int ssize = 0;
        if (sepArgPos != -1) {
            RAny v = args[sepArgPos];
            if (!(v instanceof RString)) { throw RError.getInvalidSep(ast); }
            sep = (RString) v;
            ssize = sep.size();
        }

        int si = 0;
        boolean lastWasNull = false;
        int nprinted = 0;
        for (int i = 0; i < args.length; i++) {
            if (i == sepArgPos) {
                continue;
            } else if (nprinted > 0 && !lastWasNull) {
                if (sep != null) {
                    out.print(sep.getString(si++));
                    if (si == ssize) {
                        si = 0;
                    }
                } else {
                    out.print(" ");
                }
            }
            RAny v = args[i];

            if (v instanceof RNull) {
                lastWasNull = true;
                continue;
            }
            lastWasNull = false;
            if (v instanceof RList) { throw RError.getGenericError(ast, String.format(RError.CAT_ARGUMENT_LIST, i + 1)); }
            if (v instanceof RArray) {
                RArray va = (RArray) v;
                int vsize = va.size();
                for (int j = 0; j < vsize; j++) {
                    if (j > 0) {
                        if (sep != null) {
                            out.print(sep.getString(si++));
                            if (si == ssize) {
                                si = 0;
                            }
                        } else {
                            out.print(" ");
                        }
                    }
                    nprinted++;
                    out.print(catElement(va, j));
                }
            }
        }
        out.flush();
    }

    // speculates on that all arguments are strings and separator is an empty string
    // the empty separator is a usual thing in R programs
    // all args strings is inspired by fasta
    static void catStringsBuilder(PrintWriter out, RAny[] args, int sepArgPos, ASTNode ast) throws UnexpectedResultException {
        if (sepArgPos == -1) { throw new UnexpectedResultException(null); }
        StringBuilder str = new StringBuilder();
        int argslen = args.length;
        for (int j = 0; j < argslen; j++) {
            RAny arg = args[j];
            if (!(arg instanceof RString)) { throw new UnexpectedResultException(null); }
            RString rs = (RString) arg;
            int size = rs.size();
            if (j != sepArgPos) {
                for (int i = 0; i < size; i++) {
                    str.append(rs.getString(i));
                }
            } else {
                if (size != 1 || rs.getString(0).length() > 0) { throw new UnexpectedResultException(null); }
            }
        }
        out.append(str); // FIXME: this creates a copy of the string, internally
        out.flush();
    }

    // a (slightly) faster version of catStringsBuilder
    static void catStrings(PrintWriter out, RAny[] args, int sepArgPos, ASTNode ast) throws UnexpectedResultException {
        if (sepArgPos == -1) { throw new UnexpectedResultException(null); }
        int argslen = args.length;
        int bufPos = 0;
        for (int j = 0; j < argslen; j++) {
            RAny arg = args[j];
            if (!(arg instanceof RString)) { throw new UnexpectedResultException(null); }
            RString rs = (RString) arg;
            int size = rs.size();
            if (j != sepArgPos) {
                for (int i = 0; i < size; i++) {
                    String as = rs.getString(i);
                    int asSize = as.length();
                    if (bufPos + asSize > buffer.length) {
                        int newSize = buffer.length * 2;
                        while (bufPos + asSize > newSize) {
                            newSize *= 2;
                        }
                        char[] oldBuffer = buffer;
                        buffer = new char[newSize];
                        System.arraycopy(oldBuffer, 0, buffer, 0, bufPos);
                    }
                    for (int k = 0; k < asSize; k++) {
                        buffer[bufPos++] = as.charAt(k);
                    }
                }
            } else {
                if (size != 1 || rs.getString(0).length() > 0) { throw new UnexpectedResultException(null); }
            }
        }
        out.write(buffer, 0, bufPos);
        out.flush();
    }

    // another (slightly) faster version of catStringsBuilder
    // NOTE: we could optimize even more, e.g. specialize for a node with just one scalar string and then a constant (empty) separator and a constant newline
    // argument; however, it is unlikely to help much, if printing a lot and doing it line-by-line, we cannot really help much
    // an optimization to do a lazy flush could help more

    static void catScalarStringsNoCopy(PrintWriter out, RAny[] args, int sepArgPos, ASTNode ast) throws UnexpectedResultException {
        if (sepArgPos == -1) { throw new UnexpectedResultException(null); }
        int argslen = args.length;
        for (int j = 0; j < argslen; j++) {
            RAny arg = args[j];
            if (!(arg instanceof ScalarStringImpl)) { throw new UnexpectedResultException(null); }
        }
        String sep = ((ScalarStringImpl) args[sepArgPos]).getString();
        if (sep.length() != 0) { throw new UnexpectedResultException(null); }
        for (int j = 0; j < argslen; j++) {
            String str = ((ScalarStringImpl) args[j]).getString();
            out.write(str);
        }
        out.flush();
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        if (exprs.length == 0) { return new Builtin.BuiltIn0(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame) {
                return RNull.getNull();
            }
        }; }
        ArgumentInfo ia = check(call, names, exprs);
        final int sepPosition = ia.provided("sep") ? ia.position("sep") : -1;
        final PrintWriter stdOut = new PrintWriter(System.out, true); // stdout buffering, important for fasta
        return new Builtin(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] params) {
                // assume we are only printing strings and separator is an empty (single-element) string
                try {
                    //                        catStringsBuilder(stdOut, params, sepPosition, ast);
                    catStrings(stdOut, params, sepPosition, ast);
                    // not so much better than catStrings, and fasta uses non-scalar strings, so we would need yet another node
                    //                        catScalarStringsNoCopy(stdOut, params, sepPosition, ast);
                } catch (UnexpectedResultException e) {
                    RNode generic = new Builtin(ast, argNames, argExprs) {
                        @Override public RAny doBuiltIn(Frame frame, RAny[] params) {
                            genericCat(stdOut, params, sepPosition, ast);
                            return RNull.getNull();
                        }
                    };
                    replace(generic, "install Cat.Generic from Cat.Strings.NoSep");
                    genericCat(stdOut, params, sepPosition, ast);
                }
                return RNull.getNull();
            }
        };
    }
}
