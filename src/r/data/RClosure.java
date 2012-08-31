package r.data;


public interface RClosure extends RAny {
    RFrame environment();
    RFunction function();
}
