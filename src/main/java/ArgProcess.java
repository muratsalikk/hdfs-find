import org.apache.commons.cli.*;
import org.apache.hadoop.fs.Path;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArgProcess {
    static String[] args ;
    public ArgProcess(String[] args) {
        ArgProcess.args =args;
    }

    static PrintArg printarg = new PrintArg("default");

    static List<TestArg> parseArgs() {

        List<TestArg> tl = new ArrayList<>();
        CommandLineParser parser = new DefaultParser();
        Options options = new Options();

        //Define options
        {
            options.addOption(Option.builder("MAXDEPTH").option("maxdepth")
                    .desc("Descend at most levels (a non-negative integer) levels of directories below the starting-points.")
                    .hasArg()
                    .build());
            options.addOption(Option.builder("MINDEPTH").option("mindepth")
                    .desc("Do not apply any tests or actions at levels less than levels (a non-negative integer).")
                    .hasArg()
                    .build());


            //NAME SEARCHES
            options.addOption(Option.builder("NAME").option("name")
                    .desc("Base of file name (the path with the leading directories removed) matches shell pattern pattern.")
                    .hasArg()
                    .build());
            options.addOption(Option.builder("INAME").option("iname")
                    .desc("Like -name, but the match is case insensitive.")
                    .hasArg()
                    .build());
            options.addOption(Option.builder("REGEX").option("regex")
                    .desc("File name matches regular expression pattern.")
                    .hasArg()
                    .build());
            options.addOption(Option.builder("IREGEX").option("iregex")
                    .desc("Like -regex, but the match is case insensitive.")
                    .hasArg()
                    .build());

            //TIME OPTIONS
            options.addOption(Option.builder("AMIN").option("amin")
                    .desc("File was last accessed n minutes ago.")
                    .hasArg()
                    .build());
            options.addOption(Option.builder("ANEWER").option("anewer")
                    .desc("Time  of  the last access of the current file is " +
                            "more recent than that of the last data modification of the reference file.")
                    .hasArg()
                    .build());
            options.addOption(Option.builder("ATIME").option("atime")
                    .desc("File was last accessed n*24 hours ago.")
                    .hasArg()
                    .build());
            options.addOption(Option.builder("MMIN").option("mmin")
                    .desc("File's data was last modified n minutes ago.")
                    .hasArg()
                    .build());
            options.addOption(Option.builder("MTIME").option("mtime")
                    .desc("File's data was last modified n*24 hours ago.")
                    .hasArg()
                    .build());
            options.addOption(Option.builder("NEWER").option("newer")
                    .desc("Time  of the last data modification of the current file is " +
                            "more recent than that of the last data modification of the reference file.")
                    .hasArg()
                    .build());

            // OTHER ATTRIBUTES
            options.addOption(Option.builder("TYPE").option("type")
                    .desc("File is of type: d (directory) f (regular file) l (symbolic link)")
                    .hasArg()
                    .build());
            options.addOption(Option.builder("SIZE").option("size")
                    .desc("File uses n units of space, rounding up. b (byte) k (kibibytes) m (mebibytes) g (gibibytes)")
                    .hasArg()
                    .argName("n")
                    .build());
            options.addOption("o","Or");
            options.addOption("a","And");

            // PRINT
            options.addOption(Option.builder("PRINTF").option("printf")
                    .desc("Print  format on the standard output, with '%' directives.")
                    .hasArgs()
                    .build());

            options.addOption("h", "help", false, "help");
        }


        CommandLine line = null;
        try {
            line = parser.parse(options, args);
        } catch (ParseException exp) {
            System.out.println("Unexpected exception:" + exp.getMessage());
            System.exit(1);
        }

        //take wd, path as first arg
        if(line.getArgList().size() > 0) {
            String initialPath = line.getArgList().get(0);
            TestArg t = new TestArg.TestArgBuilder("initialPath")
                    .value(initialPath)
                    .build();
            tl.add(t);
        } else if (!options.hasOption("h")) {
            printHelp(options, 1);
        } else {
            System.out.println("Unexpected usage.");
            printHelp(options, 1);
        }

        for (Option o : line.getOptions()) {
            TestArg t = null;
            String v;
            switch (o.getOpt()) {
                case "mindepth" -> {
                    t = new TestArg.TestArgBuilder("mindepth")
                            .value(toInteger(o.getValue()))
                            .build();
                }
                case "maxdepth" -> {
                    t = new TestArg.TestArgBuilder("maxdepth")
                            .value(toInteger(o.getValue()))
                            .build();
                }

                /* NAME */
                case "name" -> {
                    t = new TestArg.TestArgBuilder("name")
                            .value(Pattern.compile(toPattern(o.getValue())))
                            .build();
                }
                case "iname" -> {
                    t = new TestArg.TestArgBuilder("name")
                            .value(Pattern.compile(toPattern(o.getValue()), Pattern.CASE_INSENSITIVE))
                            .build();
                }
                case "regex" -> {
                    t = new TestArg.TestArgBuilder("name")
                            .value(Pattern.compile(o.getValue()))
                            .build();
                }
                case "iregex" -> {
                    t = new TestArg.TestArgBuilder("name")
                            .value(Pattern.compile(o.getValue(), Pattern.CASE_INSENSITIVE))
                            .build();
                }

                /* TIME */
                case "amin" -> {
                    v = o.getValue();
                    if (v.charAt(0) == '+') {
                        t = new TestArg.TestArgBuilder("atimeolder")
                                .value(toMillis(toInteger(v.replace('+', '0'))))
                                .build();
                    } else if (v.charAt(0) == '-') {
                        t = new TestArg.TestArgBuilder("atimenewer")
                                .value(toMillis(toInteger(v.replace('-', '0'))))
                                .build();
                    } else {
                        t = new TestArg.TestArgBuilder("atimeequalmin")
                                .value(toMillis(toInteger(v)))
                                .build();
                    }
                }
                case "atime" -> {
                    v = o.getValue();
                    if (v.charAt(0) == '+') {
                        t = new TestArg.TestArgBuilder("atimeolder")
                                .value(toMillis(toInteger(v.replace('+', '0')) * 1440))
                                .build();
                    } else if (v.charAt(0) == '-') {
                        t = new TestArg.TestArgBuilder("atimeequalday")
                                .value(toMillis(toInteger(v.replace('-', '0')) * 1440))
                                .build();
                    } else {
                        t = new TestArg.TestArgBuilder("atimeequalday")
                                .value(toMillis(toInteger(v) * 1440))
                                .build();
                    }
                }
                case "mmin" -> {
                    v = o.getValue();
                    if (v.charAt(0) == '+') {
                        t = new TestArg.TestArgBuilder("mtimeolder")
                                .value(toMillis(toInteger(v.replace('+', '0'))))
                                .build();
                    } else if (v.charAt(0) == '-') {
                        t = new TestArg.TestArgBuilder("mtimenewer")
                                .value(toMillis(toInteger(v.replace('-', '0'))))
                                .build();
                    } else {
                        t = new TestArg.TestArgBuilder("mtimeequalmin")
                                .value(toMillis(toInteger(v)))
                                .build();
                    }
                }
                case "mtime" -> {
                    v = o.getValue();
                    if (v.charAt(0) == '+') {
                        t = new TestArg.TestArgBuilder("mtimeolder")
                                .value(toMillis(toInteger(v.replace('+', '0')) * 1440))
                                .build();
                    } else if (v.charAt(0) == '-') {
                        t = new TestArg.TestArgBuilder("mtimenewer")
                                .value(toMillis(toInteger(v.replace('-', '0')) * 1440))
                                .build();
                    } else {
                        t = new TestArg.TestArgBuilder("mtimeequalday")
                                .value(toMillis(toInteger(v) * 1440))
                                .build();
                    }
                }
                case "anewer" -> {
                    t = new TestArg.TestArgBuilder("anewer")
                            .value(new Connect().getFileStatus(new Path(o.getValue())))
                            .build();
                }
                case "newer" -> {
                    t = new TestArg.TestArgBuilder("newer")
                            .value(new Connect().getFileStatus(new Path(o.getValue())))
                            .build();
                }

                /* OTHER ATTRIBUTES */
                case "type" -> {
                    t = new TestArg.TestArgBuilder("type")
                            .value(o.getValue().charAt(0))
                            .build();
                }
                case "size" -> {
                    v=o.getValue();
                    if (v.charAt(0) == '+') {
                        t = new TestArg.TestArgBuilder("sizebigger")
                                .value(toSize(v.replace('+', '0')))
                                .build();
                    } else if (v.charAt(0) == '-') {
                        t = new TestArg.TestArgBuilder("sizesmaller")
                                .value(toSize(v.replace('-', '0')))
                                .build();
                    } else {
                        t = new TestArg.TestArgBuilder(
                                switch (v.charAt(v.length() - 1)) {
                                    case 'K', 'k' -> "sizekequal";
                                    case 'M', 'm' -> "sizemequal";
                                    case 'G', 'g' -> "sizegequal";
                                    default -> "sizebequal";
                                }
                            )
                                .value(toSize(v))
                                .build();
                    }
                }

                /* OPERATORS */
                case "o" -> t = new TestArg.TestArgBuilder("OR").value("OR").build();
                case "a" -> t = new TestArg.TestArgBuilder("AND").value("AND").build();

                /* PRINT */
                case "printf" -> {
                    printarg = new PrintArg(o.getValues(), "printf");
                    continue;
                }
                case "h" -> {
                    printHelp(options,0);
                }
                default -> throw new IllegalStateException("Unexpected value: " + o.getOpt());
            }
            tl.add(t);
        }
        return tl;
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

    static void printHelp(Options options, int status) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("hdfs-find", "header", options, "footer", true);
        System.exit(status);
    }

    public static PrintArg getPrintarg() {
        return printarg;
    }
}