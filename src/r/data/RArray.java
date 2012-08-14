package r.data;


public interface RArray extends RAny {
    Object get(int i);

    RArray subset(RAny keys);

    RArray subset(RInt index);
    RArray subset(RString names);

    RInt asInt();
    //RDouble asDouble();
    RArray materialize();
}
