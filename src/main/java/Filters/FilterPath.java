package Filters;

import org.apache.hadoop.fs.FileStatus;

import java.util.regex.Pattern;

public class FilterPath extends BaseFilter{
    public static final int PATH = 0x01;
    public static final int IPATH = 0x02;
    Pattern pattern;

    public FilterPath(String value, int TYPE ) {
        if (TYPE == PATH) {
            this.pattern = Pattern.compile(super.toPatternString(value));
        } else if (TYPE == IPATH) {
            this.pattern = Pattern.compile(super.toPatternString(value),Pattern.CASE_INSENSITIVE);
        }
    }

    @Override
    public boolean evaluate(FileStatus file) {
        return (pattern.matcher(file.getPath().toString()).matches());
    }

}
