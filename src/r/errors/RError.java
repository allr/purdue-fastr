package r.errors;

import r.nodes.*;

public abstract class RError extends RuntimeException {

    private static final long serialVersionUID = 1L;
    public static final String LENGTH_GT_1 = "the condition has length > 1 and only the first element will be used";
    public static final String LENGTH_0 = "argument is of length zero";
    public static final String NA_UNEXP = "missing value where TRUE/FALSE needed";
    public static final String UNKNOW_VARIABLE = "object not found";
    public static final String UNUSED_ARGUMENT = "unused argument(s)";
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

    public static final String ONLY_FIRST_USED = "numerical expression has %d elements: only the first used";
    public static final String NO_SUCH_INDEX = "no such index at level %d";
    public static final String LIST_COERCION = "(list) object cannot be coerced to type '%s'";
    public static final String CAT_ARGUMENT_LIST = "argument %d (type 'list') cannot be handled by 'cat'";
    public static final String DATA_NOT_MULTIPLE_ROWS = "data length [%d] is not a sub-multiple or multiple of the number of rows [%d]";


    public static RError getNYI(final String msg) {
        return new RError() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return msg == null ? "Not yet implemented ..." : msg;
            }
        };
    }
    public static RError getNulLength(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return LENGTH_0;
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

    public static RError getUnknownVariable(ASTNode source) {
        return new RErrorInExpr(source){
            private static final long serialVersionUID = 1L;
            @Override
            public String getMessage() {
                return UNKNOW_VARIABLE;
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
