package Filters;

import org.apache.hadoop.fs.FileStatus;

public class FilterType extends BaseFilter {
    char value;
    public FilterType(char value) {
        this.value=value;
    }

    @Override
    public boolean evaluate(FileStatus file) {
        return switch (value) {
            case 'f' -> file.isFile();
            case 'd' -> file.isDirectory();
            case 'l' -> file.isSymlink();
            default -> false;
        };
    }
}
