
import org.apache.commons.cli.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArgProcess {
    static String[] args ;;
    public ArgProcess(String[] args) {
        ArgProcess.args =args;
    }

    static List<FilterArg> parseArgs() {

        List<FilterArg> al = new ArrayList<>();
        CommandLineParser parser = new DefaultParser();
        Options options = new Options();

        { //Define options
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

            options.addOption(Option.builder("MTIME").option("mtime")
                    .desc("File's data was last modified n*24 hours ago.")
                    .hasArg()
                    .build());

            options.addOption("o","Or");
            options.addOption("a","And");
        }

        CommandLine line = null;
        try {
            line = parser.parse(options, args);
        } catch (ParseException exp) {
            System.out.println("Unexpected exception:" + exp.getMessage());
            System.exit(1);
        }

        String initialPath = line.getArgList().get(0);
        FilterArg iP = new FilterArg();
        iP.setCond("initialPath");
        iP.setValue(initialPath);
        al.add(iP);

        for (Option o : line.getOptions()) {
            FilterArg a = new FilterArg();
            switch (o.getOpt()) {
                /* STRINGS */
                case "name":
                    a.setCond("name");
                    a.setValue(Pattern.compile(toPattern(o.getValue())));
                    a.setIdentifier(0);
                    break;
                case "iname":
                    a.setCond("name");
                    a.setValue(Pattern.compile(o.getValue(), Pattern.CASE_INSENSITIVE));
                    a.setIdentifier(1);
                    break;
                case "regex":
                    a.setCond("name");
                    a.setValue(Pattern.compile(o.getValue()));
                    a.setIdentifier(0);
                    break;
                case "iregex":
                    a.setCond("name");
                    a.setValue(Pattern.compile(o.getValue(), Pattern.CASE_INSENSITIVE));
                    a.setIdentifier(0);
                    break;

                /* TIME */
                case "mtime":
                    a.setCond("mtime");
                    String v = o.getValue();
                    if (v.charAt(0) == '+') {
                        a.setValue(toMillis(tointeger(v.replace('+', '0')))*24*60);
                        a.setIdentifier(2);
                    } else if (v.charAt(0) == '-') {
                        a.setValue(toMillis(tointeger(v.replace('+', '0')))*24*60);
                        a.setIdentifier(0);
                    } else {
                        a.setValue(toMillis(tointeger(v)*24*60));
                        a.setIdentifier(1);
                    }
                    break;

                /* OPERATORS */
                case "o":
                    a.setCond("OR");
                    break;
                case "a":
                    a.setCond("AND");
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + o.getOpt());
            }
            al.add(a);
        }
        return al;
    }

    static int tointeger(String s) {
        int n = 0;
        try {
            n = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            System.out.println("invalid input for age cond: "+ s);
            e.printStackTrace();
            System.exit(1);
        }
        return n;
    }

    static long toMillis(int i) {
        long result=0;
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