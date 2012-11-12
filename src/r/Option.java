package r;

import java.io.*;
import java.util.ArrayList;

public abstract class Option {

    private String option;
    private String help;
    private int params;

    public Option(String optionName) {
        this(optionName, optionName, 0);
    }

    public Option(String optionName, String helpText) {
        this(optionName, helpText, 0);
    }

    public Option(String optionName, String helpText, int nbParams) {
        this.option = optionName;
        this.help = helpText;
        this.params = nbParams;
    }

    @Override
    public String toString() {
        return option;
    }

    public String getOption() {
        return option;
    }

    public String getHelp() {
        return help;
    }

    public int getParams() {
        return params;
    }

    public boolean hasOption(String arg) {
        return option.equals(arg);
    }

    public String formatHelpText() {
        return getOption() + (getParams() > 0 ? " <" + getParams() + ">" : "") + "\t" + getHelp();
    }

    protected abstract void processOption(String name, String[] opts) throws Exception;

    public static String[] processCommandLine(String[] args, Option[] aviableOptions) throws Exception {
        ArrayList<String> todo = new ArrayList<String>(args.length);

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            boolean found = false;
            for (int j = 0; j < aviableOptions.length && !found; j++) {
                if (aviableOptions[j].hasOption(arg)) {
                    int nbParams = aviableOptions[j].getParams();
                    String[] opts = new String[nbParams];
                    int currentParam = 0;
                    while (currentParam < nbParams) {
                        try {
                            opts[currentParam++] = args[++i];
                        } catch (ArrayIndexOutOfBoundsException e) {
                            System.err.println("Option '" + arg + "' expects some parameters, using null");
                            opts[currentParam - 1] = null;
                        }
                    }
                    aviableOptions[j].processOption(arg, opts);
                    found = true;
                }
            }
            if (!found) {
                todo.add(arg);
            }
        }
        String[] rest = new String[todo.size()];
        todo.toArray(rest);
        return rest;
    }

    public abstract static class Help extends Option {

        public Help() {
            super("-?", "Help (this screen)", 0);
        }

        @Override
        public boolean hasOption(String arg) {
            return "--help".equals(arg) || super.hasOption(arg);
        }

        public static void displayHelp(PrintStream out, Option[] opts, int exitValue) {
            if (opts != null) {
                for (Option opt : opts) {
                    out.println(opt.formatHelpText());
                }
            }
            System.exit(exitValue);
        }
    }

    public static class Text extends Option {

        public Text(String text) {
            super("", text, 0);
        }

        @Override
        protected void processOption(String name, String[] opts) {
        }

        @Override
        public boolean hasOption(String opt) {
            return false;
        }

        @Override
        public String formatHelpText() {
            return getHelp();
        }
    }

    public static class Verbose extends Option {

        public static boolean verbose = false;

        public Verbose() {
            super("-v", "Be more verbose", 0);
        }

        @Override
        protected void processOption(String name, String[] opts) {
            verbose = true;
        }
    }

    public static class Quiet extends Option {

        // This class is only for convenience, it must not be instanciated in
        // a static context to avoid conflict with verbose.
        public Quiet() {
            super("--quiet", "Be quiet", 0);
            Verbose.verbose = true;
        }

        @Override
        protected void processOption(String name, String[] opts) {
            Verbose.verbose = false;
        }
    }
}
