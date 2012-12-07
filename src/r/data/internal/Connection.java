package r.data.internal;

import java.io.*;

import r.*;
import r.errors.*;
import r.nodes.*;


public abstract class Connection {

    String description;
    ConnectionMode mode;
    ConnectionMode defaultMode;

    Connection(String description, ConnectionMode mode, ConnectionMode defaultMode) {
        this.description = description;
        this.mode = mode;
        this.defaultMode = defaultMode;
    }

    public boolean isOpen() {
        return mode != null;
    }

    public ConnectionMode currentMode() {
        return mode;
    }

    public abstract void open(ConnectionMode openMode) throws IOException;
    public abstract void open(ConnectionMode openMode, ASTNode ast);

    public abstract Reader reader();

    public static class FileConnection extends Connection {

        FileReader reader;

        FileConnection(String name, ConnectionMode mode, ConnectionMode defaultMode) {
            super(name, mode, defaultMode);
        }

        public static FileConnection createUnopened(String name, ConnectionMode defaultMode) {
            return new FileConnection(name, null, defaultMode);
        }

        public static FileConnection createOpened(String name, ConnectionMode mode) throws IOException {
            FileConnection con = new FileConnection(name, null, null);
            con.open(mode);
            return con;
        }

        public static FileConnection createOpened(String name, ConnectionMode mode, ASTNode ast) {
            try {
                return FileConnection.createOpened(name, mode);
            } catch (IOException e) {
                throw RError.getCannotOpenFile(ast, name, e.toString());
            }
        }

        @Override
        public void open(ConnectionMode openMode) throws IOException {
            Utils.check(mode == null);
            if (openMode.read() && openMode.text() && !openMode.write() && !openMode.append() && !openMode.truncate()) {
                reader = new FileReader(description);
                mode = openMode;
            } else {
                Utils.nyi("unsupported open mode");
            }
        }

        @Override
        public void open(ConnectionMode openMode, ASTNode ast) {
            try {
                open(openMode);
            } catch (IOException e) {
                throw RError.getCannotOpenFile(ast, description, e.toString());
            }
        }

        @Override
        public Reader reader() {
            return reader;
        }
    }
}
