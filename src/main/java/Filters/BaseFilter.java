package Filters;

import Logger.Logger;
import org.apache.hadoop.fs.FileStatus;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaseFilter implements Filter{
    private static final Logger logger = new Logger(BaseFilter.class);

    String toPatternString(String s) {
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

    int toInteger(String s) {
        int n = 0;
        try {
            n = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            logger.error("invalid input for date cond: "+ s);
            System.exit(1);
        }
        return n;
    }

    long toMillis(int i) {
        long result;
        long min=60000;
        long currentMillisMin=System.currentTimeMillis()-System.currentTimeMillis()%min;
        result=currentMillisMin-(min*i);
        return result;
    }


    @Override
    public boolean evaluate(FileStatus file) {
        return false;
    }

    @Override
    public String getOperatorName() {
        return "";
    }
}
