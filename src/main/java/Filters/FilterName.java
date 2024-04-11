package Filters;

import org.apache.hadoop.fs.FileStatus;
import java.util.regex.Pattern;

public class FilterName extends BaseFilter{
    public static final int NAME = 0x01;
    public static final int INAME = 0x02;
    public static final int REGEX = 0x03;
    public static final int IREGEX = 0x04;

    Pattern pattern;

    public FilterName(String value, int TYPE ) {
        if (TYPE == NAME) {
           this.pattern = Pattern.compile(super.toPatternString(value));
        } else if (TYPE == INAME) {
            this.pattern = Pattern.compile(super.toPatternString(value),Pattern.CASE_INSENSITIVE);
        } else if (TYPE == REGEX) {
            this.pattern = Pattern.compile(value);
        } else if (TYPE == IREGEX) {
            this.pattern = Pattern.compile(value, Pattern.CASE_INSENSITIVE);
        }
    }

    @Override
    public boolean evaluate(FileStatus file) {
        return (pattern.matcher(file.getPath().getName()).matches());
    }

}
