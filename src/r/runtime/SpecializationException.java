package r.runtime;

public class SpecializationException extends Exception {

    private static final long serialVersionUID = 1L;
    Object payload;

    public SpecializationException(Object payload) {
        this.payload = payload;
    }

    public Object getResult() {
        return payload;
    }

}
