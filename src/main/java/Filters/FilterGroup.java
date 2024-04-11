package Filters;

import org.apache.hadoop.fs.FileStatus;


public class FilterGroup extends BaseFilter {

    String value;
    public FilterGroup(String value) {
        this.value=value;
    }

    @Override
    public boolean evaluate(FileStatus file) {
        return (file.getGroup().equals(value));
    }

}
