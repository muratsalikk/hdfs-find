package Filters;

import org.apache.hadoop.fs.FileStatus;

public class FilterSize extends BaseFilter {
    long size ;
    char operand;

    long toSize(String value) {
        long multiplier = 1;
        char lastChar = value.toUpperCase().charAt(value.length() - 1);
        switch (lastChar) {
            case 'B':
                value = value.substring(0, value.length() - 1); // Remove unit
                break;
            case 'K':
                multiplier = 1024L;
                value = value.substring(0, value.length() - 1); // Remove unit
                break;
            case 'M':
                multiplier = 1024L * 1024;
                value = value.substring(0, value.length() - 1); // Remove unit
                break;
            case 'G':
                multiplier = 1024L * 1024 * 1024;
                value = value.substring(0, value.length() - 1); // Remove unit
                break;
            default:
                break;
        }
        return Math.abs(Long.parseLong(value)) * multiplier;
    }

    public FilterSize(String value) {
        if (value.charAt(0) == '+') {
            this.size=toSize(value);
            this.operand='>';

        }  else if (value.charAt(0) == '-') {
            this.size=toSize(value);
            this.operand='<';
        } else {
            switch (value.charAt(value.length() - 1)) {
                case 'B', 'b' -> this.operand='b';
                case 'K', 'k' -> this.operand='k';
                case 'M', 'm' -> this.operand='m';
                case 'G', 'g' -> this.operand='g';
                default -> {
                    this.operand='b';
                    value = value+"b";
                }
            }
            this.size=toSize(value.substring(0, value.length() - 1));
        }

    }

    @Override
    public boolean evaluate(FileStatus file) {
        long fileSize=file.getLen();
        return switch (operand) {
            case '>' -> (fileSize >= size);
            case '<' -> (fileSize <= size);
            case 'b' -> (fileSize == size);
            case 'k' -> (fileSize / 1024 == size);
            case 'm' -> (fileSize / (1024 * 1024) == size);
            case 'g' -> (fileSize / (1024 * 1024 * 1024) == size);
            default -> false;
        };

    }

}
