package r.data;

import r.Convert.ConversionStatus;
import r.data.internal.*;
import r.nodes.*;
import r.nodes.tools.*;

// TODO: add more features if needed (GNU-R can convert to string, add attributes, names, but the semantics is a bit surprising as
// internally the type is implemented using a pairlist, hence an expression a + b has length 3 and string representation "+" "a" "b")
public class RLanguage extends BaseObject implements RAny {

    final ASTNode ast;
    final static String TYPE_STRING = "language";

    public RLanguage(ASTNode ast) {
        this.ast = ast;
    }

    public ASTNode get() {
        return ast;
    }

    @Override public String typeOf() {
        return TYPE_STRING;
    }

    @Override public String pretty() {
        return PrettyPrinter.prettyPrint(ast); // FIXME: add specific formatting to ASTNode.toString?
    }

    @Override public Attributes attributes() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override public Attributes attributesRef() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override public RAny setAttributes(Attributes attributes) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override public RAny stripAttributes() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override public RRaw asRaw() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override public RLogical asLogical() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override public RInt asInt() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override public RDouble asDouble() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override public RComplex asComplex() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override public RString asString() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override public RList asList() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override public RRaw asRaw(ConversionStatus warn) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override public RLogical asLogical(ConversionStatus warn) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override public RInt asInt(ConversionStatus warn) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override public RDouble asDouble(ConversionStatus warn) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override public RComplex asComplex(ConversionStatus warn) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override public RString asString(ConversionStatus warn) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override public void ref() {
        // TODO Auto-generated method stub
    }

    @Override public boolean isShared() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override public boolean isTemporary() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override public boolean dependsOn(RAny value) {
        // TODO Auto-generated method stub
        return false;
    }

}
