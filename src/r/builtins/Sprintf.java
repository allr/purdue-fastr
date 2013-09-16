package r.builtins;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;
import r.runtime.*;

import java.lang.Double;

// FIXME: not exactly the same semantics as GNU-R, because String.format does not have the same semantics as C's sprintf
public class Sprintf extends CallFactory {

    static final CallFactory _ = new Sprintf("sprintf", new String[]{"fmt", "..."}, new String[] {"fmt"});

    private Sprintf(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override
    public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        final int posFmt = ia.position("fmt");

        return new Builtin(call, names, exprs) {

            @Override
            public RAny doBuiltIn(Frame frame, RAny[] args) {
                return sprintf(args, posFmt, ast);
            }

        };
    }

    // LICENSE: derived code from GNU R, which is licensed under GPL (though, not very closely derived)
    public static RAny sprintf(RAny[] args, int posFmt, ASTNode ast) {
        RAny fmtArg = args[posFmt];
        if (!(fmtArg instanceof RString)) {
            throw RError.getFmtNotCharacter(ast);
        }
        RString fmt = (RString) fmtArg;
        int resLength = 0;
        RArray[] refs = new RArray[args.length - 1];
        int j = 0;
        for (int i = 0; i < args.length; i++) {
            RAny aarg = args[i];
            if (!(aarg instanceof RArray)) {
                throw RError.getUnsupportedType(ast);
            }
            RArray a = (RArray) aarg;
            int size = a.size();
            if (size == 0) { // including RNull
                return RString.EMPTY;
            }
            if (size > resLength) {
                resLength = size;
            }
            if (i != posFmt) {
                refs[j++] = a;
            }
        }
        int[] refIndexes = new int[refs.length];
        int fmtIndex = 0;
        String[] resContent = new String[resLength];

        outer: for(int i = 0; i < resLength; i++) {

            if (i > 0) { // advance recycling indexes
                // NOTE: the indexes are advanced each once, independently on how many times each reference was used
                // during matching

                fmtIndex ++;
                if (fmtIndex == fmt.size()) {
                    fmtIndex = 0;
                }

                for (int k = 0; k < refIndexes.length; k++) {
                    int tmp = refIndexes[k];
                    tmp++;
                    if (tmp >= refs[k].size()) {
                        tmp = 0;
                    }
                    refIndexes[k] = tmp;
                }
            }

            StringBuilder str = new StringBuilder();
            String formatString = fmt.getString(fmtIndex);
            int formatStringLength = formatString.length();

            int formatStringPos = 0;
            int nextRef = 0; // index of the next reference to be used (for non-fixed lookups)

            for(;;) {
                j = formatString.indexOf('%', formatStringPos); // returns -1 when formatStringPos == formatString.length
                if (j == -1) {
                    str.append(formatString.substring(formatStringPos));
                    resContent[i] = str.toString();
                    continue outer;
                }

                str.append(formatString.substring(formatStringPos, j)); // copy verbatim the string part of format
                formatStringPos = j;
                if (j + 1 < formatStringLength && formatString.charAt(j + 1) == '%') { // handle %%
                    str.append('%');
                    formatStringPos += 2;
                    continue;
                }

                StringBuilder formatBit = new StringBuilder();
                formatBit.append('%');
                j++; // skip over '%'

                // handle %n$ and %nn$  (supporting only references up to 99)
                int fixedRef = -1;
                int remaining = formatStringLength - j;
                if (remaining > 2) {
                    char c1 = formatString.charAt(j);
                    char c2 = formatString.charAt(j + 1);
                    if (c1 >= '1' && c1 <= '9') {
                        int v = c1 - '0';
                        if (c2 == '$') {
                            // %n$
                            fixedRef = v - 1;
                            j += 2;
                        } else if (remaining > 3 && c2 >= '0' && c2 <= '9' && formatString.charAt(j + 2) == '$') {
                            // %nn$
                            fixedRef = (v * 10) + (c2 - '0') - 1;
                            j += 3;
                        }
                    }
                }
                if (fixedRef >= refs.length) {
                    throw RError.getReferenceNonexistent(ast, fixedRef + 1);
                }

                // search for the end of the format bit, and also look for a "*" within
                // also copy the relevant "format bit" into formatBit

                boolean foundStar = false;
                boolean foundEnd = false;
                char fmtChar = '0';
                for(; j < formatStringLength && !foundEnd; j++) {
                    char c = formatString.charAt(j);
                    switch(c) {
                        case '*':
                            if (foundStar) {
                                throw RError.getAtMostOneAsterisk(ast);
                            }
                            foundStar = true;
                            remaining = formatStringLength - j - 1;

                            // handle *n$  and *nn$ (supporting only references up to 99)
                            int fixedStarRef = -1;
                            if (remaining > 2) {
                                char c1 = formatString.charAt(j + 1);
                                char c2 = formatString.charAt(j + 2);
                                if (c1 >= '1' && c1 <= '9') {
                                    int v = c1 - '0';
                                    if (c2 == '$') {
                                        // %n$
                                        fixedStarRef = v - 1;
                                        j += 2;
                                    } else if (remaining > 3 && c2 >= '0' && c2 <= '9' && formatString.charAt(j + 3) == '$') {
                                        // %nn$
                                        fixedStarRef = (v * 10) + (c2 - '0') - 1;
                                        j += 3;
                                    }
                                }
                            }
                            if (fixedStarRef >= refs.length) {
                                throw RError.getReferenceNonexistent(ast, fixedStarRef + 1);
                            }

                            // retrieve the reference (only an integer number is supported)
                            RAny r;
                            int rindex;
                            if (fixedStarRef > 0) {
                                r = refs[fixedStarRef];
                                rindex = refIndexes[fixedStarRef];
                            } else {
                                if (nextRef == refIndexes.length) {
                                    throw RError.getTooFewArguments(ast);
                                }
                                r = refs[nextRef];
                                rindex = refIndexes[nextRef];
                                nextRef++;
                            }

                            if (!(r instanceof RInt || r instanceof RDouble)) {
                                throw RError.getArgumentStarNumber(ast);
                            }
                            int refNumber = r.asInt().getInt(rindex);
                            if (refNumber == RInt.NA) {
                                throw RError.getArgumentStarNumber(ast);
                            }
                            formatBit.append(String.format("%d", refNumber)); // FIXME: check this is correct, GNU-R seems to be doing more checks
                            break;

                            // "diosfeEgGxXaA"
                        case 'i':
                            fmtChar = c;
                            formatBit.append('d'); // turn 'i' into 'd' as Java does not support 'i'
                            foundEnd = true;
                            break;

                        case 'd':
                        case 'o':
                        case 's':
                        case 'f':
                        case 'e':
                        case 'E':
                        case 'g':
                        case 'G':
                        case 'x':
                        case 'X':
                        case 'a':
                        case 'A':
                            foundEnd = true; // falls through
                        default:
                            fmtChar = c;
                            formatBit.append(c);
                            break;
                    }
                }
                if (!foundEnd) {
                    throw RError.getUnrecognizedFormat(ast, formatString.substring(formatStringPos));
                }

                formatStringPos = j;

                // retrieve the reference (only an integer number is supported)
                RArray r;
                int rindex;
                if (fixedRef > 0) {
                    r = refs[fixedRef];
                    rindex = refIndexes[fixedRef];
                } else {
                    if (nextRef == refIndexes.length) {
                        throw RError.getTooFewArguments(ast);
                    }
                    r = refs[nextRef];
                    rindex = refIndexes[nextRef];
                    nextRef++;
                }
                print(r.boxedGet(rindex), str, formatBit, fmtChar, ast);
                resContent[i] = str.toString();
            }
        }
        return RString.RStringFactory.getFor(resContent);
    }

    public static void print(RAny r, StringBuilder str, StringBuilder formatBit, char fmtChar, ASTNode ast) {
        if (r instanceof RLogical) {
            printLogical(((RLogical) r).getLogical(0), str, formatBit, fmtChar, ast);
        } else if (r instanceof RInt) {
            printInteger(((RInt) r).getInt(0), str, formatBit, fmtChar, ast);
        } else if (r instanceof RDouble) {
            printDouble(((RDouble) r).getDouble(0), str, formatBit, fmtChar, ast);
        } else if (r instanceof RString) {
            printString(((RString) r).getString(0), str, formatBit, fmtChar, ast);
        } else {
            throw RError.getUnsupportedType(ast);
        }
    }

    public static void printLogical(int value, StringBuilder str, StringBuilder formatBit, char fmtChar, ASTNode ast) {

        switch(fmtChar) {
            case 'd':
            case 'i':
                if (value == RLogical.NA) {
                    formatBit.setCharAt(formatBit.length() - 1, 's');
                    str.append(String.format(formatBit.toString(), "NA"));
                } else {
                    str.append(String.format(formatBit.toString(), value));
                }
                break;

            case 'a':
            case 'A':
            case 'e':
            case 'f':
            case 'g':
            case 'E':
            case 'G':
                printDouble(Convert.logical2double(value), str, formatBit, fmtChar, ast);
                break;
            case 's':
                printString(Convert.logical2string(value), str, formatBit, fmtChar, ast);
                break;

            default:
                throw RError.getInvalidFormatLogical(ast, formatBit.toString());

        }
    }

    public static void printInteger(int value, StringBuilder str, StringBuilder formatBit, char fmtChar, ASTNode ast) {

        switch(fmtChar) {
            case 'd':
            case 'i':
            case 'o':
            case 'x':
            case 'X':
                if (value == RInt.NA) {
                    formatBit.setCharAt(formatBit.length() - 1, 's');
                    str.append(String.format(formatBit.toString(), "NA"));
                } else {
                    str.append(String.format(formatBit.toString(), value));
                }
                break;

            case 'a':
            case 'A':
            case 'e':
            case 'f':
            case 'g':
            case 'E':
            case 'G':
                printDouble(Convert.int2double(value), str, formatBit, fmtChar, ast);
                break;
            case 's':
                printString(Convert.int2string(value), str, formatBit, fmtChar, ast);
                break;

            default:
                throw RError.getInvalidFormatInteger(ast, formatBit.toString());

        }
    }

    public static void printDouble(double value, StringBuilder str, StringBuilder formatBit, char fmtChar, ASTNode ast) {

        switch(fmtChar) {
            case 'd':
            case 'i':
            case 'o':
            case 'x':
            case 'X':
                if (RDouble.RDoubleUtils.fitsRInt(value)) {
                    printInteger(Convert.double2int(value), str, formatBit, fmtChar, ast);
                } else {
                    throw RError.getInvalidFormatDouble(ast, formatBit.toString());
                }
                break;

            case 'a':
            case 'A':
            case 'e':
            case 'f':
            case 'g':
            case 'E':
            case 'G':
                if (RDouble.RDoubleUtils.isFinite(value)) {
                    str.append(String.format(formatBit.toString(), value));
                } else {
                    int dotIndex = formatBit.indexOf(".");
                    if (dotIndex != -1) {
                        formatBit.setLength(dotIndex + 1);
                    }
                    formatBit.setCharAt(formatBit.length() - 1, 's');
                    boolean hasSpace = formatBit.indexOf(" ") != -1;
                    boolean hasPlus = formatBit.indexOf("+") != -1;

                    if (RDouble.RDoubleUtils.isNA(value)) {
                        str.append(String.format(formatBit.toString(), hasSpace ? " NA" : "NA"));
                    } else if (RDouble.RDoubleUtils.isNAorNaN(value)) {
                        str.append(String.format(formatBit.toString(), hasSpace ? " NaN" : "NaN"));
                    } else if (value == Double.POSITIVE_INFINITY) {
                        str.append(String.format(formatBit.toString(), hasPlus ? "+Inf" : (hasSpace ? " Inf" : "Inf")));
                    } else {
                        assert Utils.check(value == Double.NEGATIVE_INFINITY);
                        str.append(String.format(formatBit.toString(), "-Inf"));
                    }
                }
                break;

            case 's':
                printString(Convert.double2string(value), str, formatBit, fmtChar, ast);
                break;

            default:
                throw RError.getInvalidFormatDouble(ast, formatBit.toString());

        }
    }

    public static void printString(String value, StringBuilder str, StringBuilder formatBit, char fmtChar, ASTNode ast) {

        switch(fmtChar) {
            case 's':
                str.append(String.format(formatBit.toString(), Convert.prettyNA(value)));
                break;

            default:
                throw RError.getInvalidFormatString(ast, formatBit.toString());

        }
    }
}
