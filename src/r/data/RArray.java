package r.data;


public interface RArray extends RAny {
    int size();

    Object get(int i);
    RArray set(int i, Object val);

    RArray subset(RAny keys);

    RArray subset(RInt index);
    RArray subset(RString names);

    RArray materialize();
}
