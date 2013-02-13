package r.data;

import r.Convert.ConversionStatus;
import r.data.internal.*;
import r.nodes.*;
import r.nodes.truffle.*;

// NOTE: error handling with casts is tricky, because different commands do it differently
//  sometimes error is signalled by returning an NA
//  sometimes that comes with a warning that NAs have been introduced, but the warning is only given once for the whole vector even if multiple NAs are introduced
//  but sometimes there is a different warning or even an error when the conversion is not possible
//
//  also, error messages sometimes come from R itself when builtins are implemented in R, but we implement some in Java that are in GNU-R implemented in R
//    (this is not fully implemented in R)
public interface RAny {

    String TYPE_STRING = "any";
    String typeOf();

    RAttributes getAttributes();
    RAny stripAttributes();

    String pretty();
    String prettyMatrixElement();

        // casts that don't set a flag
        // FIXME: maybe could remove these and always pass the argument, but that might be slower
    RRaw asRaw();
    RLogical asLogical();
    RInt asInt();
    RDouble asDouble();
    RComplex asComplex();
    RString asString();

    RList asList();

        // casts that do set a flag when NA is introduced, out of range raw value, discarded imaginary part
    RRaw asRaw(ConversionStatus warn);
    RLogical asLogical(ConversionStatus warn);
    RInt asInt(ConversionStatus warn);
    RDouble asDouble(ConversionStatus warn);
    RComplex asComplex(ConversionStatus warn);
    RString asString(ConversionStatus warn); // FIXME: is any error ever produced? is this needed for String?

    void ref();
    boolean isShared(); // FIXME: at some point will probably need do distinguish between 0, 1, and 2

    <T extends RNode> T callNodeFactory(OperationFactory<T> factory);
}
