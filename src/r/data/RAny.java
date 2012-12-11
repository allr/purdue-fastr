package r.data;

import r.Convert.NAIntroduced;
import r.Convert.OutOfRange;
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

    String typeOf();

    RAttributes getAttributes();
    RAny stripAttributes();

    String pretty();
    String prettyMatrixElement();

        // casts that don't set a flag (but still can introduce NAs)
    RRaw asRaw();
    RLogical asLogical();
    RInt asInt();
    RDouble asDouble();
    RString asString();
    RList asList();

        // casts that do set a flag when NA is introduced
    RRaw asRaw(NAIntroduced naIntroduced, OutOfRange outOfRange);
    RLogical asLogical(NAIntroduced naIntroduced);
    RInt asInt(NAIntroduced naIntroduced);
    RDouble asDouble(NAIntroduced naIntroduced);
    RString asString(NAIntroduced naIntroduced);

    void ref();
    boolean isShared(); // FIXME: at some point will probably need do distinguish between 0, 1, and 2

    <T extends RNode> T callNodeFactory(OperationFactory<T> factory);
}
