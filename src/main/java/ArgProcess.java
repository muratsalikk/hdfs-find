import org.apache.commons.cli.*;
import org.apache.hadoop.fs.Path;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArgProcess {
    static String[] args ;
    static List<TestArg> tl = new ArrayList<>();
    static Path initialPath;

    public ArgProcess(String[] args) {
        ArgProcess.args =args;
        parseArgs();
    }
    public List<TestArg> getTestArgList() {
        return tl;
    }
    public Path getInitialPath() {
        return initialPath;
    }


    static PrintArg printarg = new PrintArg("default");

    static void parseArgs() {
        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        CommandLine line = null;
        OptionGroup printOptionGroup = new OptionGroup();
        OptionGroup pathOptionGroup = new OptionGroup();

        //Define options
        for (Enums e : Enums.values()) {
            if (e == Enums.OR || e == Enums.AND) {
                options.addOption(Option.builder()
                        .option(e.opt)
                        .desc(e.desc)
                        .hasArg(false)
                        .build());
            } else if (e == Enums.PRINT0 || e == Enums.LS) {
                printOptionGroup.addOption(Option.builder()
                        .option(e.opt)
                        .desc(e.desc)
                        .hasArg(false)
                        .build());
            } else if (e == Enums.PRINTF) {
                printOptionGroup.addOption(Option.builder()
                        .option(e.opt)
                        .desc(e.desc)
                        .hasArgs()
                        .argName(e.argName)
                        .build());
            } else if (e == Enums.HELP) {
                options.addOption(Option.builder()
                        .option(e.opt)
                        .longOpt("help")
                        .desc(e.desc)
                        .hasArg(false)
                        .build());
            } else {
                options.addOption(Option.builder()
                        .option(e.opt)
                        .desc(e.desc)
                        .hasArg()
                        .argName(e.argName)
                        .build());
            }
        }
        pathOptionGroup.addOption(Option.builder()
                .option("")
                .hasArg()
                .argName("path")
                .required(true)
                .desc("Required - Initial path that tests starts on.")
                .build());
        options.addOptionGroup(pathOptionGroup);
        options.addOptionGroup(printOptionGroup);


        try {
            line = parser.parse(options, args);
        } catch (ParseException exp) {
            System.out.println("Unexpected exception:" + exp.getMessage());
            System.exit(1);
        }


        //take wd, path as first arg
        if(line.getArgList().size() > 0) {
            initialPath = new Path(line.getArgList().get(0));
        } else if (options.hasOption("h") || options.hasLongOption("help")) {
            printHelp(options, 0);
        } else {
            System.out.println("Unexpected usage.");
            printHelp(options, 1);
        }


        // Build TestArgList
        for (Option o : line.getOptions()) {
            TestArg t = null;
            String v;

            switch (Enums.findByOpt(o.getOpt())) {
                case MINDEPTH -> {
                    t = new TestArg.Builder(FilterArgNames.MINDEPTH)
                            .value(toDepth(toInteger(o.getValue())))
                            .build();
                }
                case MAXDEPTH -> {
                    t = new TestArg.Builder(FilterArgNames.MAXDEPTH)
                            .value(toDepth(toInteger(o.getValue())))
                            .build();
                }

                /* NAME */
                case NAME -> {
                    t = new TestArg.Builder(FilterArgNames.NAME)
                            .value(Pattern.compile(toPattern(o.getValue())))
                            .build();
                }
                case INAME -> {
                    t = new TestArg.Builder(FilterArgNames.NAME)
                            .value(Pattern.compile(toPattern(o.getValue()), Pattern.CASE_INSENSITIVE))
                            .build();
                }
                case REGEX -> {
                    t = new TestArg.Builder(FilterArgNames.NAME)
                            .value(Pattern.compile(o.getValue()))
                            .build();
                }
                case IREGEX -> {
                    t = new TestArg.Builder(FilterArgNames.NAME)
                            .value(Pattern.compile(o.getValue(), Pattern.CASE_INSENSITIVE))
                            .build();
                }
                case PATH -> {
                    t = new TestArg.Builder(FilterArgNames.PATH)
                            .value(Pattern.compile(toPattern(o.getValue())))
                            .build();
                }
                case IPATH -> {
                    t = new TestArg.Builder(FilterArgNames.PATH)
                            .value(Pattern.compile(toPattern(o.getValue()), Pattern.CASE_INSENSITIVE))
                            .build();
                }

                /* TIME */
                case AMIN -> {
                    v = o.getValue();
                    if (v.charAt(0) == '+') {
                        t = new TestArg.Builder(FilterArgNames.ACCESS_TIME_OLDER)
                                .value(toMillis(toInteger(v.replace('+', '0'))))
                                .build();
                    } else if (v.charAt(0) == '-') {
                        t = new TestArg.Builder(FilterArgNames.ACCESS_TIME_NEWER)
                                .value(toMillis(toInteger(v.replace('-', '0'))))
                                .build();
                    } else {
                        t = new TestArg.Builder(FilterArgNames.ACCESS_TIME_EQUAL_MIN)
                                .value(toMillis(toInteger(v)))
                                .build();
                    }
                }
                case ATIME -> {
                    v = o.getValue();
                    if (v.charAt(0) == '+') {
                        t = new TestArg.Builder(FilterArgNames.ACCESS_TIME_OLDER)
                                .value(toMillis(toInteger(v.replace('+', '0')) * 1440))
                                .build();
                    } else if (v.charAt(0) == '-') {
                        t = new TestArg.Builder(FilterArgNames.ACCESS_TIME_NEWER)
                                .value(toMillis(toInteger(v.replace('-', '0')) * 1440))
                                .build();
                    } else {
                        t = new TestArg.Builder(FilterArgNames.ACCESS_TIME_EQUAL_DAY)
                                .value(toMillis(toInteger(v) * 1440))
                                .build();
                    }
                }
                case MMIN -> {
                    v = o.getValue();
                    if (v.charAt(0) == '+') {
                        t = new TestArg.Builder(FilterArgNames.MODIFICATION_TIME_OLDER)
                                .value(toMillis(toInteger(v.replace('+', '0'))))
                                .build();
                    } else if (v.charAt(0) == '-') {
                        t = new TestArg.Builder(FilterArgNames.MODIFICATION_TIME_NEWER)
                                .value(toMillis(toInteger(v.replace('-', '0'))))
                                .build();
                    } else {
                        t = new TestArg.Builder(FilterArgNames.MODIFICATION_TIME_EQUAL_MIN)
                                .value(toMillis(toInteger(v)))
                                .build();
                    }
                }
                case MTIME -> {
                    v = o.getValue();
                    if (v.charAt(0) == '+') {
                        t = new TestArg.Builder(FilterArgNames.MODIFICATION_TIME_OLDER)
                                .value(toMillis(toInteger(v.replace('+', '0')) * 1440))
                                .build();
                    } else if (v.charAt(0) == '-') {
                        t = new TestArg.Builder(FilterArgNames.MODIFICATION_TIME_NEWER)
                                .value(toMillis(toInteger(v.replace('-', '0')) * 1440))
                                .build();
                    } else {
                        t = new TestArg.Builder(FilterArgNames.MODIFICATION_TIME_EQUAL_DAY)
                                .value(toMillis(toInteger(v) * 1440))
                                .build();
                    }
                }
                case ANEWER -> {
                    t = new TestArg.Builder(FilterArgNames.NEWER_ACCESS_TIME)
                            .value(new Connect().getFileStatus(new Path(o.getValue())))
                            .build();
                }
                case NEWER -> {
                    t = new TestArg.Builder(FilterArgNames.NEWER_MODIFICATION_TIME)
                            .value(new Connect().getFileStatus(new Path(o.getValue())))
                            .build();
                }

                /* OTHER ATTRIBUTES */
                case TYPE -> {
                    t = new TestArg.Builder(FilterArgNames.TYPE)
                            .value(o.getValue().charAt(0))
                            .build();
                }
                case EMPTY -> {
                    t= new TestArg.Builder(FilterArgNames.EMPTY)
                        .value(true)
                        .build();
                }
                case GROUP -> {
                    t= new TestArg.Builder(FilterArgNames.GROUP)
                        .value(o.getValue())
                        .build();
                }
                case USER -> {
                    t= new TestArg.Builder(FilterArgNames.USER)
                            .value(o.getValue())
                            .build();
                }
                case SIZE -> {
                    v=o.getValue();
                    if (v.charAt(0) == '+') {
                        t = new TestArg.Builder(FilterArgNames.SIZE_BIGGER)
                                .value(toSize(v.replace('+', '0')))
                                .build();
                    } else if (v.charAt(0) == '-') {
                        t = new TestArg.Builder(FilterArgNames.SIZE_SMALLER)
                                .value(toSize(v.replace('-', '0')))
                                .build();
                    } else {
                        t = new TestArg.Builder(
                                switch (v.charAt(v.length() - 1)) {
                                    case 'K', 'k' -> FilterArgNames.SIZE_KB_EQUAL;
                                    case 'M', 'm' -> FilterArgNames.SIZE_MB_EQUAL;
                                    case 'G', 'g' -> FilterArgNames.SIZE_GB_EQUAL;
                                    default -> FilterArgNames.SIZE_B_EQUAL;
                                }
                            )
                                .value(toSize(v))
                                .build();
                    }
                }

                /* OPERATORS */
                case OR -> t = new TestArg.Builder(FilterArgNames.OR).value("OR").build();
                case AND -> t = new TestArg.Builder(FilterArgNames.AND).value("AND").build();

                /* PRINT */
                case LS -> {
                    printarg = new PrintArg("ls");
                    continue;
                }
                case PRINT0 -> {
                    printarg = new PrintArg("print0");
                    continue;
                }
                case PRINTF -> {
                    printarg = new PrintArg(o.getValues(), "printf");
                    continue;
                }
                case HELP -> {
                    printHelp(options,0);
                }
                default -> throw new IllegalStateException("Unexpected value: " + o.getOpt());
            }
            tl.add(t);
        }
    }

    static int toInteger(String s) {
        int n = 0;
        try {
            n = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            System.out.println("invalid input for date cond: "+ s);
            e.printStackTrace();
            System.exit(1);
        }
        return n;
    }

    static long toMillis(int i) {
        long result;
        long min=60000;
        long currentMillisMin=System.currentTimeMillis()-System.currentTimeMillis()%min;
        result=currentMillisMin-(min*i);
        return result;
    }

    static String toPattern(String s) {
        Pattern p = Pattern.compile("[^*?]+|(\\*)|(\\?)");
        Matcher m = p.matcher(s);
        StringBuilder b= new StringBuilder();
        while (m.find()) {
            if(m.group(1) != null) m.appendReplacement(b, ".*");
            else if(m.group(2) != null) m.appendReplacement(b, ".");
            else m.appendReplacement(b, "\\\\Q" + m.group(0) + "\\\\E");
        }
        m.appendTail(b);
        return b.toString();
    }

    static long toSize(String s) {
        char lastChar = s.charAt(s.length()-1);
        long value = Long.parseLong(s.substring(0,s.length()-2));
        return switch (lastChar) {
            case 'B', 'b' -> value;
            case 'K', 'k' -> value * 1024;
            case 'M', 'm' -> value * 1024 * 1024;
            case 'G', 'g' -> value * 1024 * 1024 * 1024;
            default -> (long) toInteger(s);
        };
    }

    static int toDepth(int i) {
        return initialPath.depth() + i + 1 ;
    }

    static void printHelp(Options options, int status) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("hdfs-find", "header", options, "footer", true);
        System.exit(status);
    }

    public static PrintArg getPrintArg() {
        return printarg;
    }
}