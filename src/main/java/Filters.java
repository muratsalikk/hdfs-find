import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.util.regex.Pattern;

public class Filters {
    public Filters() {
    }

    boolean filterName(FileStatus file, Pattern p ) {
        return (p.matcher(file.getPath().getName()).matches());
    }

    boolean filterAccessTime(FileStatus file, long value, int identifier) {
        boolean r=false;
        if (identifier == 0) {
            r = (file.getAccessTime() >= value);
        } else if (identifier == 1) {
            r = (file.getAccessTime() <= value);
        } else if (identifier == 2) {
            long a= file.getAccessTime();
            a = a - a%60000;
            r = (a  == value);
        }else if (identifier == 3) {
            long a= file.getAccessTime();
            a -= a % (60000*1440);
            long v = value - value%(60000*1440);
            r = (a  == v);
        }
        return r;
    }

    boolean filterAccessTimeNewer(FileStatus file, long value) {
        return (file.getAccessTime() >= value);
    }
    boolean filterAccessTimeOlder(FileStatus file, long value) {
        return (file.getAccessTime() <= value);
    }
    boolean filterAccessTimeEqualDay(FileStatus file, long value) {
        long a= file.getAccessTime();
        a = a - a%60000;
        return (a  == value);
    }
    boolean filterAccessTimeEqualMin(FileStatus file, long value) {
        long a= file.getAccessTime();
        a -= a % (60000*1440);
        long v = value - value%(60000*1440);
        return (a  == v);
    }

    boolean filterModificationTimeNewer(FileStatus file, long value) {
        return (file.getModificationTime() >= value);
    }
    boolean filterModificationTimeOlder(FileStatus file, long value) {
        return (file.getModificationTime() <= value);
    }
    boolean filterModificationTimeEqualDay(FileStatus file, long value) {
        long a= file.getModificationTime();
        a = a - a%60000;
        return (a  == value);
    }
    boolean filterModificationTimeEqualMin(FileStatus file, long value) {
        long a= file.getModificationTime();
        a -= a % (60000 * 1440);
        long v = value - value%(60000*1440);
        return (a  == v);
    }

    boolean filterNewer (FileStatus file, String reference, FileSystem hfs, int identifier) {
        boolean r= false;
        Path p = new Path(reference);
        FileStatus ref = null;

        try {
            ref = hfs.getFileStatus(p);
        } catch (IOException e) {
            System.out.println("reference file (" + reference + ") does not found.");
            System.exit(1);
        }
        if (identifier == 0) {
            r = (file.getAccessTime() > ref.getAccessTime());
        } else if (identifier == 1){
            r = (file.getModificationTime() > ref.getModificationTime());
        }
        return r;
    }

    boolean filterMinDepth(FileStatus file, int mindepth) {
        return (file.getPath().depth() > mindepth);
    }
    boolean filterMaxDepth(FileStatus file, int maxdepth) {
        return (file.getPath().depth() < maxdepth);
    }

    boolean filterType(FileStatus file, String value) {
        return switch (value) {
            case "f" -> file.isFile();
            case "d" -> file.isDirectory();
            case "l" -> file.isSymlink();
            default -> false;
        };
    }

    boolean filterSizeBigger(FileStatus file, long value) {
        return (file.getLen() >= value);
    }
    boolean filterSizeLower(FileStatus file, long value) {
        return (file.getLen() <= value);
    }
    boolean filterSizeBEqual(FileStatus file, long value) {
        return (file.getLen() == value);
    }
    boolean filterSizeKEqual(FileStatus file, long value) {
        long a = file.getLen();
        a -= a%1024;
        return (a == value);
    }
    boolean filterSizeMEqual(FileStatus file, long value) {
        long a = file.getLen();
        a -= a%(1024*1024);
        return (a == value);
    }
    boolean filterSizeGEqual(FileStatus file, long value) {
        long a = file.getLen();
        a -= a%(1024*1024*1024);
        return (a == value);
    }

}
