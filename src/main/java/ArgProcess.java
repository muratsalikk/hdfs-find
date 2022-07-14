import org.apache.commons.cli.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArgProcess {
    static String[] args ;
    public ArgProcess(String[] args) {
        ArgProcess.args =args;
    }

    static List<FilterArg> parseArgs() {

        List<FilterArg> al = new ArrayList<>();
        CommandLineParser parser = new DefaultParser();
        Options options = new Options();

        { //Define options
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

            // TYPE-SIZE
            options.addOption(Option.builder("TYPE").option("type")
                    .desc("File is of type: d (directory) f (regular file) l (symbolic link)")
                    .hasArg()
                    .build());

            options.addOption("o","Or");
            options.addOption("a","And");

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
            FilterArg iP = new FilterArg();
            iP.setCond("initialPath");
            iP.setValue(initialPath);
            al.add(iP);
        } else if (!options.hasOption("h")) {
            printHelp(options, 1);
        } else {
            System.out.println("Unexpected usage.");
            printHelp(options, 1);
        }

        for (Option o : line.getOptions()) {
            FilterArg a = new FilterArg();
            String v;
            switch (o.getOpt()) {
                case "mindepth" -> {
                    a.setCond("mindepth");
                    a.setValue(toInteger(o.getValue()));
                    a.setIdentifier(0);
                }
                case "maxdepth" -> {
                    a.setCond("maxdepth");
                    a.setValue(toInteger(o.getValue()));
                    a.setIdentifier(0);
                }

                /* NAME */
                case "name" -> {
                    a.setCond("name");
                    a.setValue(Pattern.compile(toPattern(o.getValue())));
                }
                case "iname" -> {
                    a.setCond("name");
                    a.setValue(Pattern.compile(toPattern(o.getValue()), Pattern.CASE_INSENSITIVE));
                }
                case "regex" -> {
                    a.setCond("name");
                    a.setValue(Pattern.compile(o.getValue()));
                }
                case "iregex" -> {
                    a.setCond("name");
                    a.setValue(Pattern.compile(o.getValue(), Pattern.CASE_INSENSITIVE));
                }

                /* TIME */
                case "amin" -> {
                    v = o.getValue();
                    if (v.charAt(0) == '+') {
                        a.setCond("atimeolder");
                        a.setValue(toMillis(toInteger(v.replace('+', '0'))));
                    } else if (v.charAt(0) == '-') {
                        a.setCond("atimenewer");
                        a.setValue(toMillis(toInteger(v.replace('-', '0'))));
                    } else {
                        a.setCond("atimeequalmin");
                        a.setValue(toMillis(toInteger(v)));
                    }
                }
                case "anewer" -> {
                    a.setCond("newer");
                    a.setValue(o.getValue());
                    a.setIdentifier(0);
                }
                case "atime" -> {
                    v = o.getValue();
                    if (v.charAt(0) == '+') {
                        a.setCond("atimeolder");
                        a.setValue(toMillis(toInteger(v.replace('+', '0')) * 1440));
                        a.setIdentifier(1);
                    } else if (v.charAt(0) == '-') {
                        a.setCond("atimenewer");
                        a.setValue(toMillis(toInteger(v.replace('-', '0')) * 1440));
                        a.setIdentifier(0);
                    } else {
                        a.setCond("atimeequalday");
                        a.setValue(toMillis(toInteger(v) * 1440));
                        a.setIdentifier(3);
                    }
                }
                case "mmin" -> {
                    v = o.getValue();
                    if (v.charAt(0) == '+') {
                        a.setCond("mtimeolder");
                        a.setValue(toMillis(toInteger(v.replace('+', '0'))));
                    } else if (v.charAt(0) == '-') {
                        a.setCond("mtimenewer");
                        a.setValue(toMillis(toInteger(v.replace('-', '0'))));
                    } else {
                        a.setCond("mtimeequalmin");
                        a.setValue(toMillis(toInteger(v)));
                    }
                }
                case "mtime" -> {
                    v = o.getValue();
                    if (v.charAt(0) == '+') {
                        a.setCond("mtimeolder");
                        a.setValue(toMillis(toInteger(v.replace('+', '0')) * 1440));
                        a.setIdentifier(2);
                    } else if (v.charAt(0) == '-') {
                        a.setCond("mtimenewer");
                        a.setValue(toMillis(toInteger(v.replace('-', '0')) * 1440));
                        a.setIdentifier(0);
                    } else {
                        a.setCond("mtimeequalday");
                        a.setValue(toMillis(toInteger(v) * 1440));
                        a.setIdentifier(3);
                    }
                }
                case "newer" -> {
                    a.setCond("newer");
                    a.setValue(o.getValue());
                    a.setIdentifier(1);
                }

                /* ATTRIBUTES */
                case "type" -> {
                    a.setCond("type");
                    a.setValue(o.getValue());
                }
                //TODO: toSize function needed
                case "size" -> {
                    a.setCond("size");
                    a.setValue(0);
                }

                /* OPERATORS */
                case "o" -> a.setCond("OR");
                case "a" -> a.setCond("AND");

                /* ETC */
                case "h" -> {
                    printHelp(options,0);
                }
                default -> throw new IllegalStateException("Unexpected value: " + o.getOpt());
            }
            al.add(a);
        }
        return al;
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

    static void printHelp(Options options, int status) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("hdfs-find", "header", options, "footer", true);
        System.exit(status);
    }

}

class FilterArg{

    String cond;
    String svalue;
    int ivalue;
    boolean bvalue;
    long lvalue;
    Pattern pvalue;
    int identifier;

    public FilterArg(){

    }

    public void setCond(String cond) {this.cond = cond; }
    public void setIdentifier(int identifier) { this.identifier = identifier; }
    public void setValue(boolean bvalue) { this.bvalue = bvalue; }
    public void setValue(int ivalue) { this.ivalue = ivalue; }
    public void setValue(long lvalue) { this.lvalue = lvalue; }
    public void setValue(String svalue) { this.svalue = svalue; }
    public void setValue(Pattern pvalue) { this.pvalue = pvalue; }

    public String getSvalue() { return svalue; }
    public int getIvalue() { return ivalue; }
    public boolean getBvalue() { return bvalue; }
    public long getLvalue() { return lvalue;}
    public Pattern getPvalue() { return pvalue; }
    public int getIdentifier() { return identifier; }
    public String getCond() { return cond;}

    String getAllInfo() {
        String i="cond: " + getCond() + " |";
        i = i + "value: "+ getSvalue()+"-"+getLvalue()+"-"+getIvalue()+"-"+getBvalue()+"-";
        i = i + "identifier: "+ getIdentifier()+" ";
        return i;
    }
}