package r.data.internal;

import java.io.*;
import java.lang.ProcessBuilder.Redirect;
import java.util.*;

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

    public String description() {
        return description;
    }

    public abstract void open(ConnectionMode openMode) throws IOException;
    public abstract void open(ConnectionMode openMode, ASTNode ast);
    public abstract void flush(ASTNode ast);
    public abstract void close(ASTNode ast);

    public abstract Reader reader(ASTNode ast);
    public abstract OutputStream output(ASTNode ast);

    @Override
    public void finalize() throws Throwable {
        if (isOpen()) {
            close(null);
        }
        super.finalize();
    }

    public static class FileConnection extends Connection {

        RandomAccessFile file;
        FileInputStream input;
        FileOutputStream output;
        PushbackReader reader;

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
            boolean needsWrite = openMode.write() || openMode.append();

            file = new RandomAccessFile(description, !needsWrite ? "r" : "rw");
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
        public Reader reader(ASTNode ast) {
            if (reader != null) {
                return reader;
            }
            Utils.check(file != null);
            try {
                if (input == null) {
                    input = new FileInputStream(file.getFD());
                }
                reader = new PushbackReader(new InputStreamReader(input));
                return reader;
            } catch (IOException e) {
                throw RError.getGenericError(ast, e.toString());
            }
        }

        @Override
        public OutputStream output(ASTNode ast) {
            if (output != null) {
                return output;
            }
            Utils.check(file != null);
            try {
                output = new FileOutputStream(file.getFD());
                return output;
            } catch (IOException e) {
                throw RError.getGenericError(ast, e.toString());
            }
        }

        @Override
        public void flush(ASTNode ast) {
            try {
                if (output != null) {
                    output.flush();
                }
            } catch (IOException e) {
                throw RError.getGenericError(ast, e.toString());
            }
        }

        @Override
        public void close(ASTNode ast) { // FIXME: could be more lazy?
            try {
                if (file != null) {
                    file.close();
                    file = null;
                }
                output = null;
                input = null;
                reader = null;
                mode = null;
            } catch (IOException e) {
                throw RError.getGenericError(ast, e.toString());
            }
        }
    }

    public static class PipeConnection extends Connection {

        Process process;
        ProcessBuilder processBuilder;
        InputStream input;
        OutputStream output;
        PushbackReader reader;

        PipeConnection(String command, ConnectionMode mode, ConnectionMode defaultMode) {
            super(command, mode, defaultMode);

            StringTokenizer st = new StringTokenizer(command);
            String[] commandArray = new String[st.countTokens()];
            for (int i = 0; st.hasMoreTokens(); i++) {
                commandArray[i] = st.nextToken();
            }
            processBuilder = new ProcessBuilder(commandArray);
        }

        public static PipeConnection createUnopened(String command, ConnectionMode defaultMode) {
            return new PipeConnection(command, null, defaultMode);
        }

        public static PipeConnection createOpened(String command, ConnectionMode mode) throws IOException {
            PipeConnection con = new PipeConnection(command, null, null);
            con.open(mode);
            return con;
        }

        public static PipeConnection createOpened(String command, ConnectionMode mode, ASTNode ast) {
            try {
                return PipeConnection.createOpened(command, mode);
            } catch (IOException e) {
                throw RError.getCannotOpenPipe(ast, command, e.toString());
            }
        }

        @Override
        public void open(ConnectionMode openMode) throws IOException {
            Utils.check(mode == null);
            if (openMode.read()) {
                processBuilder.redirectOutput(Redirect.PIPE);
            } else {
                processBuilder.redirectOutput(Redirect.INHERIT);
            }
            if (openMode.write() || openMode.append()) {
                processBuilder.redirectInput(Redirect.PIPE);
            } else {
                processBuilder.redirectOutput(Redirect.INHERIT);
            }
            // NOTE: GNU-R uses popen, which can either read, or write, but not both

            process = processBuilder.start();
        }

        @Override
        public void open(ConnectionMode openMode, ASTNode ast) {
            try {
                open(openMode);
            } catch (IOException e) {
                throw RError.getCannotOpenPipe(ast, description, e.toString());
            }
        }

        @Override
        public Reader reader(ASTNode ast) {
            if (reader != null) {
                return reader;
            }
            Utils.check(process != null);
            if (input == null) {
                input = process.getInputStream();
            }
            reader = new PushbackReader(new InputStreamReader(input));
            return reader;
        }

        @Override
        public OutputStream output(ASTNode ast) {
            if (output != null) {
                return output;
            }
            Utils.check(process != null);
            output = process.getOutputStream();
            return output;
        }

        @Override
        public void flush(ASTNode ast) {
            try {
                if (output != null) {
                    output.flush();
                }
            } catch (IOException e) {
                throw RError.getGenericError(ast, e.toString());
            }
        }

        @Override
        public void close(ASTNode ast) { // FIXME: could be more lazy?
            try {
                if (process != null) {
                   if (output != null) {
                       output.close();
                   }
                   process.waitFor();
                   process = null;
                }
                output = null;
                input = null;
                reader = null;
                mode = null;
            } catch (InterruptedException e) {
                throw RError.getGenericError(ast, e.toString());
            } catch (IOException e) {
                throw RError.getGenericError(ast, e.toString());
            }
        }
    }
}
