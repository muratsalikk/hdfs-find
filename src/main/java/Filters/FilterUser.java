package Filters;

import org.apache.hadoop.fs.FileStatus;

public class FilterUser extends BaseFilter {

    String value;
    public FilterUser(String value) {
        this.value=value;
    }

    @Override
    public boolean evaluate(FileStatus file) {
        return (file.getOwner().equals(value));
    }
}
