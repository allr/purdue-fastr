package r.errors;

import r.data.RSymbol;
import r.nodes.ASTNode;
import r.nodes.tools.*;
import r.nodes.truffle.*;

public abstract class RError extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public static final String LENGTH_GT_1 = "the condition has length > 1 and only the first element will be used";
    public static final String LENGTH_ZERO = "argument is of length zero";
    public static final String NA_UNEXP = "missing value where TRUE/FALSE needed";
    public static final String LENGTH_NOT_MULTI = "longer object length is not a multiple of shorter object length";
    public static final String INTEGER_OVERFLOW = "NAs produced by integer overflow";
    public static final String NA_OR_NAN = "NA/NaN argument";
    public static final String SUBSCRIPT_BOUNDS = "subscript out of bounds";
    public static final String SELECT_LESS_1 = "attempt to select less than one element";
    public static final String SELECT_MORE_1 = "attempt to select more than one element";
    public static final String ONLY_0_MIXED = "only 0's may be mixed with negative subscripts";
    public static final String REPLACEMENT_0 = "replacement has length zero";
    public static final String NOT_MULTIPLE_REPLACEMENT = "number of items to replace is not a multiple of replacement length";
    public static final String MORE_SUPPLIED_REPLACE = "more elements supplied than there are to replace";
    public static final String NA_SUBSCRIPTED = "NAs are not allowed in subscripted assignments";
    public static final String INVALID_ARG_TYPE = "invalid argument type";
    public static final String INVALID_ARG_TYPE_UNARY = "invalid argument to unary operator";
    public static final String INVALID_LENGTH = "invalid 'length' argument";
    public static final String VECTOR_SIZE_NEGATIVE = "vector size cannot be negative";
    public static final String NO_LOOP_FOR_BREAK_NEXT = "no loop for break/next, jumping to top level";
    public static final String INVALID_FOR_SEQUENCE = "invalid for() loop sequence";
    public static final String NO_NONMISSING_MAX = "no non-missing arguments to max; returning -Inf";
    public static final String LENGTH_NONNEGATIVE = "length must be non-negative number";
    public static final String INVALID_TIMES = "invalid 'times' argument";
    public static final String INVALID_TFB = "invalid (to - from)/by in seq(.)";
    public static final String WRONG_SIGN_IN_BY = "wrong sign in 'by' argument";
    public static final String BY_TOO_SMALL = "'by' argument is much too small";
    public static final String INCORRECT_SUBSCRIPTS = "incorrect number of subscripts";
    public static final String INVALID_TYPE_LIST = "invalid 'type' (list) of argument";
    public static final String INVALID_SEP = "invalid 'sep' specification";
    public static final String NOT_FUNCTION = "argument is not a function, character or symbol"; // GNU R gives also expression for the argument
    public static final String NON_NUMERIC_MATH = "non-numeric argument to mathematical function";
    public static final String NAN_PRODUCED = "NaNs produced";
    public static final String NUMERIC_COMPLEX_MATRIX_VECTOR = "requires numeric/complex matrix/vector arguments";
    public static final String NON_CONFORMABLE_ARGS = "non-conformable arguments";
    public static final String INVALID_BYROW = "invalid 'byrow' argument";
    public static final String DATA_VECTOR = "'data' must be of a vector type";
    public static final String NON_NUMERIC_MATRIX_EXTENT = "non-numeric matrix extent";
    public static final String INVALID_NCOL = "invalid 'ncol' value (too large or NA)"; // also can mean empty
    public static final String INVALID_NROW = "invalid 'nrow' value (too large or NA)"; // also can mean empty
    public static final String NEGATIVE_NCOL = "invalid 'ncol' value (< 0)";
    public static final String NEGATIVE_NROW = "invalid 'nrow' value (< 0)";
    public static final String NON_CONFORMABLE_ARRAYS = "non-conformable arrays";
    public static final String INVALID_MODE = "invalid 'mode' argument";
    public static final String UNKNOWN_VARIABLE = "object not found";
    public static final String ONLY_MATRIX_DIAGONALS = "only matrix diagonals can be replaced";
    public static final String REPLACEMENT_DIAGONAL_LENGTH = "replacement diagonal has wrong length";
    public static final String NA_INTRODUCED_COERCION = "NAs introduced by coercion";
    public static final String ARGUMENT_WHICH_NOT_LOGICAL = "argument to 'which' is not logical";
    public static final String X_NUMERIC = "'x' must be numeric";
    public static final String X_ARRAY_TWO = "'x' must be an array of at least two dimensions";
    public static final String ACCURACY_MODULUS = "probable complete loss of accuracy in modulus";
    public static final String INVALID_SEPARATOR = "invalid separator";
    public static final String INCORRECT_DIMENSIONS = "incorrect number of dimensions";
    public static final String LOGICAL_SUBSCRIPT_LONG = "(subscript) logical subscript too long";
    public static final String DECREASING_TRUE_FALSE = "'decreasing' must be TRUE or FALSE";
    public static final String ARGUMENT_LENGTHS_DIFFER = "argument lengths differ";
    public static final String ZERO_LENGTH_PATTERN = "zero-length pattern";
    public static final String ALL_CONNECTIONS_IN_USE = "all connections are in use";
    public static final String CANNOT_READ_CONNECTION = "cannot read from this connection";
    public static final String CANNOT_WRITE_CONNECTION = "cannot write to this connection";
    public static final String TOO_FEW_LINES_READ_LINES = "too few lines read in readLines";
    public static final String INVALID_CONNECTION = "invalid connection";
    public static final String OUT_OF_RANGE = "out-of-range values treated as 0 in coercion to raw";
    public static final String WRITE_ONLY_BINARY = "can only write to a binary connection";
    public static final String UNIMPLEMENTED_COMPLEX = "unimplemented complex operation";
    public static final String COMPARISON_COMPLEX = "invalid comparison with complex values";
    public static final String NON_NUMERIC_BINARY = "non-numeric argument to binary operator";
    public static final String RAW_SORT = "raw vectors cannot be sorted";
    public static final String INVALID_UNNAMED_ARGUMENT = "invalid argument";
    public static final String INVALID_UNNAMED_VALUE = "invalid value";
    public static final String NAMES_NONVECTOR = "names() applied to a non-vector";
    public static final String ONLY_FIRST_VARIABLE_NAME = "only the first element is used as variable name";
    public static final String INVALID_FIRST_ARGUMENT = "invalid first argument";
    public static final String NO_ENCLOSING_ENVIRONMENT = "no enclosing environment";
    public static final String ASSIGN_EMPTY = "cannot assign values in the empty environment";
    public static final String ARGUMENT_NOT_MATRIX = "argument is not a matrix";
    public static final String DOLLAR_ATOMIC_VECTORS = "$ operator is invalid for atomic vectors";
    public static final String COERCING_LHS_TO_LIST = "Coercing LHS to a list";
    public static final String ARGUMENT_NOT_LIST = "argument not a list";
    public static final String DIMS_CONTAIN_NEGATIVE_VALUES = "the dims contain negative values";
    public static final String NEGATIVE_LENGTH_VECTORS_NOT_ALLOWED = "negative length vectors are not allowed";
    public static final String FIRST_ARG_MUST_BE_ARRAY = "invalid first argument, must be an array";
    public static final String IMAGINARY_PARTS_DISCARDED_IN_COERCION = "imaginary parts discarded in coercion";

    public static final String ONLY_FIRST_USED = "numerical expression has %d elements: only the first used";
    public static final String NO_SUCH_INDEX = "no such index at level %d";
    public static final String LIST_COERCION = "(list) object cannot be coerced to type '%s'";
    public static final String CAT_ARGUMENT_LIST = "argument %d (type 'list') cannot be handled by 'cat'";
    public static final String DATA_NOT_MULTIPLE_ROWS = "data length [%d] is not a sub-multiple or multiple of the number of rows [%d]";
    public static final String ARGUMENT_NOT_MATCH = "supplied argument name '%s' does not match '%s'";
    public static final String ARGUMENT_MISSING = "argument '%s' is missing, with no default";
    public static final String UNKNOWN_FUNCTION = "could not find function '%s'";
    public static final String UNKNOWN_OBJECT = "object '%s' not found";
    public static final String INVALID_ARGUMENT = "invalid '%s' argument";
    public static final String INVALID_SUBSCRIPT_TYPE = "invalid subscript type '%s'";
    public static final String ARGUMENT_NOT_VECTOR = "argument %d is not a vector";
    public static final String CANNOT_COERCE = "cannot coerce type '%s' to vector of type '%s'";
    public static final String ARGUMENT_ONLY_FIRST = "argument '%s' has length > 1 and only the first element will be used";
    public static final String CANNOT_OPEN_FILE = "cannot open file '%s': %s";
    public static final String NOT_CONNECTION = "'%s' is not a connection";
    public static final String INCOMPLETE_FINAL_LINE = "incomplete final line found on '%s'";
    public static final String CANNOT_OPEN_PIPE = "cannot open pipe() cmd '%s': %s";
    public static final String INVALID_TYPE_ARGUMENT = "invalid 'type' (%s) of argument";
    public static final String ATTRIBUTE_VECTOR_SAME_LENGTH = "'%s' attribute [%d] must be the same length as the vector [%d]";
    public static final String SCAN_UNEXPECTED = "scan() expected '%s', got '%s'";
    public static final String MUST_BE_ENVIRON = "'%s' must be an environment";
    public static final String UNUSED_ARGUMENT = "unused argument(s) (%s)"; // FIXME: GNU-R gives a list of all unused arguments
    public static final String INFINITE_MISSING_VALUES = "infinite or missing values in '%s'";
    public static final String NON_SQUARE_MATRIX = "non-square matrix in '%s'";
    public static final String LAPACK_ERROR = "error code %d from Lapack routine '%s'";
    public static final String VALUE_OUT_OF_RANGE = "value out of range in '%s'";
    public static final String MUST_BE_NONNULL_STRING = "'%s' must be non-null character string";
    public static final String IS_OF_WRONG_LENGTH = "'%s' is of wrong length";
    public static final String OBJECT_NOT_SUBSETTABLE = "object of type '%s' is not subsettable";


    public static RError getNYI(final String msg) {
        return new RError() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return msg == null ? "Not yet implemented ..." : msg;
            }
        };
    }
    public static RError getLengthZero(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return LENGTH_ZERO;
            }
        };
    }
    public static RError getNAorNaN(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return NA_OR_NAN;
            }
        };
    }

    public static RError getUnexpectedNA(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return NA_UNEXP;
            }
        };
    }

    public static RError getSubscriptBounds(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return SUBSCRIPT_BOUNDS;
            }
        };
    }

    public static RError getSelectLessThanOne(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return SELECT_LESS_1;
            }
        };
    }

    public static RError getSelectMoreThanOne(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return SELECT_MORE_1;
            }
        };
    }

    public static RError getOnlyZeroMixed(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return ONLY_0_MIXED;
            }
        };
    }

    public static RError getReplacementZero(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return REPLACEMENT_0;
            }
        };
    }

    public static RError getMoreElementsSupplied(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return MORE_SUPPLIED_REPLACE;
            }
        };
    }

    public static RError getNASubscripted(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return NA_SUBSCRIPTED;
            }
        };
    }

    public static RError getInvalidArgType(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return INVALID_ARG_TYPE;
            }
        };
    }

    public static RError getInvalidArgTypeUnary(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return INVALID_ARG_TYPE_UNARY;
            }
        };
    }

    public static RError getInvalidLength(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return INVALID_LENGTH;
            }
        };
    }

    public static RError getVectorSizeNegative(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return VECTOR_SIZE_NEGATIVE;
            }
        };
    }

    public static RError getNoLoopForBreakNext(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return NO_LOOP_FOR_BREAK_NEXT;
            }
        };
    }

    public static RError getInvalidForSequence(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.INVALID_FOR_SEQUENCE;
            }
        };
    }

    public static RError getLengthNonnegative(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.LENGTH_NONNEGATIVE;
            }
        };
    }

    public static RError getInvalidTimes(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.INVALID_TIMES;
            }
        };
    }


    public static RError getWrongSignInBy(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.WRONG_SIGN_IN_BY;
            }
        };
    }

    public static RError getByTooSmall(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.BY_TOO_SMALL;
            }
        };
    }

    public static RError getIncorrectSubscripts(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.INCORRECT_SUBSCRIPTS;
            }
        };
    }

    public static RError getInvalidTFB(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.INVALID_TFB;
            }
        };
    }

    public static RError getInvalidTypeList(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.INVALID_TYPE_LIST;
            }
        };
    }

    public static RError getInvalidSep(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.INVALID_SEP;
            }
        };
    }

    public static RError getNotFunction(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.NOT_FUNCTION;
            }
        };
    }

    public static RError getNonNumericMath(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.NON_NUMERIC_MATH;
            }
        };
    }

    public static RError getNumericComplexMatrixVector(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.NUMERIC_COMPLEX_MATRIX_VECTOR;
            }
        };
    }

    public static RError getNonConformableArgs(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.NON_CONFORMABLE_ARGS;
            }
        };
    }

    public static RError getInvalidByRow(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.INVALID_BYROW;
            }
        };
    }

    public static RError getDataVector(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.DATA_VECTOR;
            }
        };
    }

    public static RError getNonNumericMatrixExtent(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.NON_NUMERIC_MATRIX_EXTENT;
            }
        };
    }

    public static RError getInvalidNCol(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.INVALID_NCOL;
            }
        };
    }

    public static RError getInvalidNRow(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.INVALID_NROW;
            }
        };
    }

    public static RError getNegativeNCol(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.NEGATIVE_NCOL;
            }
        };
    }

    public static RError getNegativeNRow(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.NEGATIVE_NROW;
            }
        };
    }

    public static RError getNonConformableArrays(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.NON_CONFORMABLE_ARRAYS;
            }
        };
    }

    public static RError getInvalidMode(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.INVALID_MODE;
            }
        };
    }

    public static RError getOnlyMatrixDiagonals(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.ONLY_MATRIX_DIAGONALS;
            }
        };
    }

    public static RError getReplacementDiagonalLength(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.REPLACEMENT_DIAGONAL_LENGTH;
            }
        };
    }

    public static RError getArgumentWhichNotLogical(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.ARGUMENT_WHICH_NOT_LOGICAL;
            }
        };
    }

    public static RError getXNumeric(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.X_NUMERIC;
            }
        };
    }

    public static RError getXArrayTwo(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.X_ARRAY_TWO;
            }
        };
    }

    public static RError getInvalidSeparator(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.INVALID_SEPARATOR;
            }
        };
    }

    public static RError getIncorrectDimensions(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.INCORRECT_DIMENSIONS;
            }
        };
    }

    public static RError getLogicalSubscriptLong(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.LOGICAL_SUBSCRIPT_LONG;
            }
        };
    }

    public static RError getDecreasingTrueFalse(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.DECREASING_TRUE_FALSE;
            }
        };
    }

    public static RError getArgumentLengthsDiffer(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.ARGUMENT_LENGTHS_DIFFER;
            }
        };
    }

    public static RError getZeroLengthPattern(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.ZERO_LENGTH_PATTERN;
            }
        };
    }

    public static RError getAllConnectionsInUse(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.ALL_CONNECTIONS_IN_USE;
            }
        };
    }

    public static RError getCannotReadConnection(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.CANNOT_READ_CONNECTION;
            }
        };
    }

    public static RError getCannotWriteConnection(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.CANNOT_WRITE_CONNECTION;
            }
        };
    }

    public static RError getTooFewLinesReadLines(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.TOO_FEW_LINES_READ_LINES;
            }
        };
    }

    public static RError getInvalidConnection(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.INVALID_CONNECTION;
            }
        };
    }

    public static RError getWriteOnlyBinary(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.WRITE_ONLY_BINARY;
            }
        };
    }

    public static RError getComparisonComplex(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.COMPARISON_COMPLEX;
            }
        };
    }

    public static RError getUnimplementedComplex(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.UNIMPLEMENTED_COMPLEX;
            }
        };
    }

    public static RError getNonNumericBinary(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.NON_NUMERIC_BINARY;
            }
        };
    }

    public static RError getRawSort(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.RAW_SORT;
            }
        };
    }

    public static RError getInvalidUnnamedArgument(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.INVALID_UNNAMED_ARGUMENT;
            }
        };
    }

    public static RError getInvalidUnnamedValue(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.INVALID_UNNAMED_VALUE;
            }
        };
    }

    public static RError getNamesNonVector(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.NAMES_NONVECTOR;
            }
        };
    }

    public static RError getInvalidFirstArgument(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.INVALID_FIRST_ARGUMENT;
            }
        };
    }

    public static RError getNoEnclosingEnvironment(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.NO_ENCLOSING_ENVIRONMENT;
            }
        };
    }

    public static RError getAssignEmpty(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.ASSIGN_EMPTY;
            }
        };
    }

    public static RError getArgumentNotMatrix(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.ARGUMENT_NOT_MATRIX;
            }
        };
    }

    public static RError getDimsContainNegativeValues(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.DIMS_CONTAIN_NEGATIVE_VALUES;
            }
        };
    }

    public static RError getNegativeLengthVectorsNotAllowed(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.NEGATIVE_LENGTH_VECTORS_NOT_ALLOWED;
            }
        };
    }


    public static RError getFirstArgMustBeArray(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.FIRST_ARG_MUST_BE_ARRAY;
            }
        };
    }


    public static RError getImaginaryPartsDiscardedInCoercion(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.IMAGINARY_PARTS_DISCARDED_IN_COERCION;
            }
        };
    }


    public static RError getNotMultipleReplacement(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.NOT_MULTIPLE_REPLACEMENT;
            }
        };
    }

    public static RError getArgumentNotList(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.ARGUMENT_NOT_LIST;
            }
        };
    }

    static class RErrorInExpr extends RError {
        private ASTNode errorNode;
        private static final long serialVersionUID = 1L;

        public RErrorInExpr(ASTNode node) {
            errorNode = node;
        }

        public ASTNode getErrorNode() {
            return errorNode;
        }
    }

    public static RError getUnknownVariable(ASTNode ast, RSymbol symbol) {
        return getGenericError(ast, String.format(RError.UNKNOWN_OBJECT, symbol.pretty()));
    }

    public static RError getUnknownFunction(ASTNode ast, RSymbol symbol) {
        return getGenericError(ast, String.format(RError.UNKNOWN_FUNCTION, symbol.pretty()));
    }

    public static RError getInvalidArgument(ASTNode ast, String str) {
        return getGenericError(ast, String.format(RError.INVALID_ARGUMENT, str));
    }

    public static RError getInvalidSubscriptType(ASTNode ast, String str) {
        return getGenericError(ast, String.format(RError.INVALID_SUBSCRIPT_TYPE, str));
    }

    public static RError getArgumentNotVector(ASTNode ast, int i) {
        return getGenericError(ast, String.format(RError.ARGUMENT_NOT_VECTOR, i));
    }

    public static RError getCannotCoerce(ASTNode ast, String srcType, String dstType) {
        return getGenericError(ast, String.format(RError.CANNOT_COERCE, srcType, dstType));
    }

    public static RError getCannotOpenFile(ASTNode ast, String fileName, String reason) {
        return getGenericError(ast, String.format(RError.CANNOT_OPEN_FILE, fileName, reason));
    }

    public static RError getCannotOpenPipe(ASTNode ast, String command, String reason) {
        return getGenericError(ast, String.format(RError.CANNOT_OPEN_PIPE, command, reason));
    }

    public static RError getNotConnection(ASTNode ast, String argName) {
        return getGenericError(ast, String.format(RError.NOT_CONNECTION, argName));
    }

    public static RError getInvalidTypeArgument(ASTNode ast, String typeName) {
        return getGenericError(ast, String.format(RError.INVALID_TYPE_ARGUMENT, typeName));
    }

    public static RError getAttributeVectorSameLength(ASTNode ast, String attr, int attrLen, int vectorLen) {
        return getGenericError(ast, String.format(RError.ATTRIBUTE_VECTOR_SAME_LENGTH, attr, attrLen, vectorLen));
    }

    public static RError getNoSuchIndexAtLevel(ASTNode ast, int level) {
        return getGenericError(ast, String.format(RError.NO_SUCH_INDEX, level));
    }

    public static RError getScanUnexpected(ASTNode ast, String expType, String gotValue) {
        return getGenericError(ast, String.format(RError.SCAN_UNEXPECTED, expType, gotValue));
    }

    public static RError getMustBeEnviron(ASTNode ast, String argName) {
        return getGenericError(ast, String.format(RError.MUST_BE_ENVIRON, argName));
    }

    public static RError getInfiniteMissingValues(ASTNode ast, String argName) {
        return getGenericError(ast, String.format(RError.INFINITE_MISSING_VALUES, argName));
    }

    public static RError getNonSquareMatrix(ASTNode ast, String argName) {
        return getGenericError(ast, String.format(RError.NON_SQUARE_MATRIX, argName));
    }

    public static RError getLapackError(ASTNode ast, int code, String routine) {
        return getGenericError(ast, String.format(RError.LAPACK_ERROR, code, routine));
    }

    public static RError getMustBeNonNullString(ASTNode ast, String argName) {
        return getGenericError(ast, String.format(RError.MUST_BE_NONNULL_STRING, argName));
    }

    public static RError getValueOutOfRange(ASTNode ast, String argName) {
        return getGenericError(ast, String.format(RError.VALUE_OUT_OF_RANGE, argName));
    }

    public static RError getValueIsOfWrongLength(ASTNode ast, String argName) {
        return getGenericError(ast, String.format(RError.IS_OF_WRONG_LENGTH, argName));
    }

    public static RError getObjectNotSubsettable(ASTNode ast, String typeName) {
        return getGenericError(ast, String.format(RError.OBJECT_NOT_SUBSETTABLE, typeName));
    }


    public static RError getUnusedArgument(ASTNode ast, RSymbol argName, RNode argExpr) {
        StringBuilder msg = new StringBuilder();

        if (argName != null || argExpr != null) {
            if (argName != null) {
                msg.append(argName.pretty());
            }
            if (argExpr != null) {
                if (argName != null) {
                    msg.append(" = ");
                }
                msg.append(PrettyPrinter.prettyPrint(argExpr.getAST()));
            }
        }
        return getGenericError(ast, String.format(RError.UNUSED_ARGUMENT, msg));
    }

    public static RError getUnknownVariable(ASTNode source) {
        return new RErrorInExpr(source){
            private static final long serialVersionUID = 1L;
            @Override
            public String getMessage() {
                return UNKNOWN_VARIABLE;
            }
        };
    }

    public static RError getDollarAtomicVectors(ASTNode source) {
        return new RErrorInExpr(source) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return RError.DOLLAR_ATOMIC_VECTORS;
            }
        };
    }

    public static RError getGenericError(ASTNode source, final String msg) {
        return new RErrorInExpr(source) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return msg;
            }
        };
    }
}
