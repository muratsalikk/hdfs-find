package Filters;

import org.apache.hadoop.fs.FileStatus;

public interface Filter {
    boolean evaluate(FileStatus file);
    public String getOperatorName();

}
