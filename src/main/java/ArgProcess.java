import Enums.FilterArgNames;
import Enums.OptionsEnum;
import Logger.Logger;
import org.apache.commons.cli.*;
import org.apache.hadoop.fs.Path;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static Enums.OptionsEnum.*;

public class ArgProcess {
    private static final Logger logger = new Logger(ArgProcess.class);

    static String[] args ;
    static List<TestArg> testList = new ArrayList<>();
    static Path initialPath;

    public ArgProcess(String[] args) {
        String[] processedArgs = new String[args.length];
        //normlise parantheses
        for (int i = 0; i < args.length; i++) {
            processedArgs[i] = args[i].replace("\\(", "-leftparenthesis");
            processedArgs[i] = args[i].replace("(", "-leftparenthesis");
            processedArgs[i] = args[i].replace("\\)", "-rightparenthesis");
            processedArgs[i] = args[i].replace(")", "-rightparenthesis");
        }
        ArgProcess.args = processedArgs;
        parseArgs();
    }
    public List<TestArg> getTestArgList() {
        return testList;
    }
    public Path getInitialPath() {
        return initialPath;
    }

// what the fuck is this?
    static PrintExecutor printarg = new PrintExecutor("default");

    static void parseArgs() {
        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        CommandLine line = null;

        //Define options
        for (OptionsEnum e : values()) {
            if (e == OR || e == AND || e == NOT || e == LEFT_PARENTHESIS || e == RIGHT_PARENTHESIS ) {
                options.addOption(new Option(e.opt,false, e.desc));
            } else if (e == PRINT0 || e == LS) {
                options.addOption(new Option(e.opt,false, e.desc));
            } else if (e == PRINTF) {
                options.addOption(new Option(e.opt, true, e.desc));
            } else if (e == HELP) {
                Option helpOption = new Option(e.opt,false, e.desc);
                helpOption.setLongOpt("help");
                options.addOption(helpOption);
            } else {
                options.addOption(new Option(e.opt, true, e.desc));
            }
        }

        // start path
        Option startPathOption = new Option("", "Required - Initial path that tests starts on.");
        startPathOption.setArgName("path");
        options.addOption(startPathOption);


        try {
            line = parser.parse(options, args);
        } catch (ParseException exp) {
            logger.error(exp.getMessage());
            exp.printStackTrace();
            printHelp(options, 1);
        }


        //take wd, path as first arg
        if(!line.getArgList().isEmpty()) {
            initialPath = new Path(line.getArgList().get(0));
        } else if (options.hasOption("h") || options.hasLongOption("-help")) {
            printHelp(options, 0);
        } else {
            logger.error("Unexpected usage.");
            printHelp(options, 1);
        }


        // Build TestArgList
        for (Option o : line.getOptions()) {
            TestArg t = null;
            String v;

            switch (findByOpt(o.getOpt())) {
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
                                .value(Long.parseLong(v))
                                .build();
                    }
                }

                /* OPERATORS */
                case OR -> t = new TestArg.Builder(FilterArgNames.OR).value("OR").build();
                case AND -> t = new TestArg.Builder(FilterArgNames.AND).value("AND").build();
                case RIGHT_PARENTHESIS -> t = new TestArg.Builder(FilterArgNames.AND).value("AND").build();

                /* PRINT */
                case LS -> {
                    printarg = new PrintExecutor("ls");
                    continue;
                }
                case PRINT0 -> {
                    printarg = new PrintExecutor("print0");
                    continue;
                }
                case PRINTF -> {
                    printarg = new PrintExecutor(o.getValues(), "printf");
                    continue;
                }
                case HELP -> {
                    printHelp(options,0);
                }
                default -> throw new IllegalStateException("Unexpected value: " + o.getOpt());
            }
            testList.add(t);
        }

        logger.debug(testList.toString());
        logger.debug(printarg.toString());

    }

    static int toInteger(String s) {
        int n = 0;
        try {
            n = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            logger.error("invalid input for date cond: "+ s);
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
        StringBuffer b= new StringBuffer();
        while (m.find()) {
            if(m.group(1) != null) m.appendReplacement(b, ".*");
            else if(m.group(2) != null) m.appendReplacement(b, ".");
            else m.appendReplacement(b, "\\\\Q" + m.group(0) + "\\\\E");
        }
        m.appendTail(b);
        return b.toString();
    }

    static long toSize(String value) {
        char lastChar = value.charAt(value.length()-1);
        int multiplier;
        switch (lastChar) {
            case 'B', 'b' -> multiplier = 1;
            case 'K', 'k' -> multiplier = 1024;
            case 'M', 'm' -> multiplier = 1024 * 1024;
            case 'G', 'g' -> multiplier = 1024 * 1024 * 1024;
            default -> multiplier =1;
        };
        return Long.parseLong(value.substring(0,value.length()-2)) * multiplier;
    }

    static int toDepth(int i) {
        return initialPath.depth() + i + 1 ;
    }

    static void printHelp(Options options, int status) {
        HelpFormatter formatter = new HelpFormatter();

        formatter.printHelp("hdfs-find", "header", options, "footer", true);
        System.exit(status);
    }

    public static PrintExecutor getPrintArg() {
        return printarg;
    }
}