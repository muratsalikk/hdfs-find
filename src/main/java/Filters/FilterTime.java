package Filters;

import org.apache.hadoop.fs.FileStatus;

public class FilterTime extends BaseFilter {
    public static final int ATIME = 0x01;
    public static final int AMIN = 0x02;
    public static final int ANEWER = 0x03;
    public static final int MMIN = 0x04;
    public static final int MTIME = 0x05;
    public static final int NEWER = 0x06;

    char method ;
    char operand ;
    long value;

    public FilterTime(String value, int TYPE) {
        if (TYPE == MMIN || TYPE == MTIME) {
            method = 'm';
        } else if (TYPE == AMIN || TYPE == ATIME) {
            method = 'a';
        }
        if (value.charAt(0) == '+') {
            operand = '>';
            if (TYPE == AMIN || TYPE == MMIN) {
                this.value = toMillis(toInteger(value.replace('+', '0')));
            } else if (TYPE == ATIME || TYPE == MTIME) {
                this.value = toMillis(toInteger(value.replace('+', '0')) * 1440 );
            }
        } else if (value.charAt(0) == '-') {
            operand = '<';
            if (TYPE == AMIN || TYPE == MMIN) {
                this.value = toMillis(toInteger(value.replace('-', '0')));
            } else if (TYPE == ATIME) {
                this.value = toMillis(toInteger(value.replace('-', '0')) * 1440 );
            }
        } else {
            if (TYPE == AMIN || TYPE == MMIN) {
                operand = 'm';
                this.value = toMillis(toInteger(value)) - toMillis(toInteger(value))%(60000*1440) ;
            } else if (TYPE == ATIME || TYPE == MTIME) {
                operand = 'd';
                this.value = toMillis(toInteger(value) * 1440 );
            } else if (TYPE == ANEWER) {
                operand = '>';
                method = 'a';
            } else if (TYPE == NEWER) {
                operand = '>';
                method = 'm';
            }

        }
    }

    @Override
    public boolean evaluate(FileStatus file) {
        long a = 0L;
        if (method == 'm') {
            a=file.getModificationTime();
        } else if (method == 'a') {
            a=file.getAccessTime();
        }
        return switch (operand) {
            case '>' -> (a >= value);
            case '<' -> (a <= value);
            case 'm' -> (a - a%(60000*1440) == value);
            case 'd' -> (a - a%60000 == value);
            default -> false;
        };
    }
}
