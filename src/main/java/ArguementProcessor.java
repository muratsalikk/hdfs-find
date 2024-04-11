import Filters.*;
import Enums.OptionsEnum;
import Logger.Logger;
import org.apache.commons.cli.*;
import org.apache.hadoop.fs.Path;

import java.util.ArrayList;
import java.util.List;

import static Enums.OptionsEnum.*;

public class ArguementProcessor {
    private static final Logger logger = new Logger(ArguementProcessor.class);
    static String[] args ;
    static Options options;
    static List<TestArg> testList = new ArrayList<>();
    static List<Filter> filterInfixList = new ArrayList<>();
    static Path initialPath;
    static int minDepth = 0;
    static int maxDepth = 1000;

    static PrintExecutor printExecutor = new PrintExecutor("default");


    public ArguementProcessor(String[] args) {
        String[] processedArgs = new String[args.length];
        //normalise parantheses
        for (int i = 0; i < args.length; i++) {
            processedArgs[i] = args[i].replace("\\(", "-leftparenthesis")
                    .replace("(", "-leftparenthesis")
                    .replace("\\)", "-rightparenthesis")
                    .replace(")", "-rightparenthesis");
        }
        ArguementProcessor.args = processedArgs;
        parseArguements();
    }



    static void createOptions() {
        options = new Options();
        //Define options using OptionsEnum
        for (OptionsEnum e : values()) {
            if (e == OR || e == AND || e == NOT || e == LEFT_PARENTHESIS || e == RIGHT_PARENTHESIS) {
                options.addOption(new Option(e.opt, false, e.desc));
            } else if (e == PRINT0 || e == LS) {
                options.addOption(new Option(e.opt, false, e.desc));
            } else if (e == PRINTF) {
                options.addOption(new Option(e.opt, true, e.desc));
            } else if (e == HELP) {
                Option helpOption = new Option(e.opt, false, e.desc);
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
    }


    static void parseArguements() {
        CommandLineParser parser = new DefaultParser();
        CommandLine line = null;
        createOptions();
        try {
            line = parser.parse(options, args);
        } catch (ParseException exp) {
            logger.error(exp.getMessage());
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

        boolean previousWasFilter = false, currentIsFilter = false;
        // Build FilterInfixList
        for (Option opt : line.getOptions()) {
            TestArg testArg = null;
            String v;

            //add an AND between each pair of filters if no other operator is explicitly specified

            switch (findByOpt(opt.getOpt())) {
                case TYPE, EMPTY, GROUP, USER, SIZE, NAME, INAME, REGEX, IREGEX, PATH, IPATH,
                        ATIME, AMIN, ANEWER, MTIME, MMIN, NEWER, LEFT_PARENTHESIS  -> currentIsFilter = true;
                default -> currentIsFilter = false;
            }

            if (previousWasFilter && currentIsFilter) {
                filterInfixList.add(new FilterOperator(AND.opt));
            }

            switch (findByOpt(opt.getOpt())) {
                case TYPE, EMPTY, GROUP, USER, SIZE, NAME, INAME, REGEX, IREGEX, PATH, IPATH,
                        ATIME, AMIN, ANEWER, MTIME, MMIN, NEWER, RIGHT_PARENTHESIS -> previousWasFilter = true;
                default -> previousWasFilter = false;
            }

            switch (findByOpt(opt.getOpt())) {
                case MAXDEPTH -> maxDepth = Integer.parseInt(opt.getValue());
                case MINDEPTH -> minDepth = Integer.parseInt(opt.getValue());

                /* FILTERS */
                case NAME -> filterInfixList.add(new FilterName(opt.getValue(),FilterName.NAME));
                case INAME -> filterInfixList.add(new FilterName(opt.getValue(),FilterName.INAME));
                case REGEX -> filterInfixList.add(new FilterName(opt.getValue(),FilterName.REGEX));
                case IREGEX -> filterInfixList.add(new FilterName(opt.getValue(),FilterName.IREGEX));
                case PATH -> filterInfixList.add(new FilterPath(opt.getValue(), FilterPath.PATH));
                case IPATH -> filterInfixList.add(new FilterPath(opt.getValue(),FilterPath.IPATH));
                case ATIME -> filterInfixList.add(new FilterTime(opt.getValue(), FilterTime.ATIME));
                case AMIN -> filterInfixList.add(new FilterTime(opt.getValue(), FilterTime.AMIN));
                case ANEWER -> filterInfixList.add(new FilterTime(opt.getValue(), FilterTime.ANEWER));
                case MTIME -> filterInfixList.add(new FilterTime(opt.getValue(), FilterTime.MTIME));
                case MMIN -> filterInfixList.add(new FilterTime(opt.getValue(), FilterTime.MMIN));
                case NEWER -> filterInfixList.add(new FilterTime(opt.getValue(), FilterTime.NEWER));
                case TYPE -> filterInfixList.add(new FilterType(opt.getValue().charAt(0)));
                case EMPTY -> filterInfixList.add(new FilterSize("0B"));
                case GROUP -> filterInfixList.add(new FilterGroup(opt.getValue()));
                case USER -> filterInfixList.add(new FilterUser(opt.getValue()));
                case SIZE -> filterInfixList.add(new FilterSize(opt.getValue()));

                /* OPERATORS */
                case OR -> filterInfixList.add(new FilterOperator(OR.opt));
                case AND -> filterInfixList.add(new FilterOperator(AND.opt));
                case NOT -> filterInfixList.add(new FilterOperator(NOT.opt));
                case RIGHT_PARENTHESIS -> filterInfixList.add(new FilterOperator(RIGHT_PARENTHESIS.opt));
                case LEFT_PARENTHESIS -> filterInfixList.add(new FilterOperator(LEFT_PARENTHESIS.opt));

                /* ACTIONS */
                case LS -> {
                    printExecutor = new PrintExecutor("ls");
                    continue;
                }
                case PRINT0 -> {
                    printExecutor = new PrintExecutor("print0");
                    continue;
                }
                case PRINTF -> {
                    printExecutor = new PrintExecutor(opt.getValues(), "printf");
                    continue;
                }

                case HELP -> {
                    printHelp(options,0);
                }
                default -> throw new IllegalStateException("Unexpected value: " + opt.getOpt());
            }
            testList.add(testArg);
        }

        logger.debug(filterInfixList.toString());
        //logger.debug(printarg.toString());

    }

    static void printHelp(Options options, int status) {
        HelpFormatter formatter = new HelpFormatter();

        formatter.printHelp("hdfs-find", "header", options, "footer", true);
        System.exit(status);
    }

    public PrintExecutor getPrintExecutor() {
        return printExecutor;
    }


    public  List<Filter> getFilterInfixList() {
        return filterInfixList;
    }

    public Path getInitialPath() {
        return initialPath;
    }

    public int getMinDepth() {
        return minDepth;
    }

    public int getMaxDepth() {
        return maxDepth;
    }


}
