package r.data;


public interface RAny {
    RAttributes getAttributes();

    String pretty();

    RLogical asLogical();
    RInt asInt();
}
