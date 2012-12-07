package r.data.internal;

import java.util.*;


public class ConnectionMode {

    boolean read;
    boolean write;
    boolean append;
    boolean binary;
    boolean truncate;

    ConnectionMode(boolean read, boolean write, boolean append, boolean binary, boolean truncate) {
        this.read = read;
        this.write = write;
        this.append = append;
        this.binary = binary;
        this.truncate = truncate;
    }

    static final HashMap<String, ConnectionMode> modes = new HashMap<String, ConnectionMode>();

    public static ConnectionMode get(String mode) {
        return modes.get(mode);
    }

    public static void add(String mode, ConnectionMode modeObject) {
        modes.put(mode, modeObject);
    }

    static {
        add("r", new ConnectionMode(true, false, false, false, false));
        add("rt", new ConnectionMode(true, false, false, false, false));
        add("w", new ConnectionMode(false, true, false, false, false));
        add("wt", new ConnectionMode(false, true, false, false, false));
        add("a", new ConnectionMode(false, false, true, false, false));
        add("at", new ConnectionMode(false, false, true, false, false));
        add("rb", new ConnectionMode(true, false, false, true, false));
        add("wb", new ConnectionMode(false, true, false, true, false));
        add("ab", new ConnectionMode(false, false, true, true, false));
        add("r+", new ConnectionMode(true, true, false, false, false));
        add("r+b", new ConnectionMode(true, true, false, true, false));
        add("w+", new ConnectionMode(true, true, false, false, true));
        add("w+b", new ConnectionMode(true, true, false, true, false));
        add("a+", new ConnectionMode(true, false, true, false, false));
        add("a+b", new ConnectionMode(true, false, true, true, false));
    }

    public boolean read() {
        return read;
    }

    public boolean write() {
        return write;
    }

    public boolean append() {
        return append;
    }

    public boolean binary() {
        return binary;
    }

    public boolean text() {
        return !binary;
    }

    public boolean truncate() {
        return truncate;
    }
}
