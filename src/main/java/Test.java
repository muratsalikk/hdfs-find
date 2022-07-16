import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.util.regex.Pattern;

public interface Test {
    boolean execute(FileStatus file);
}

class FilterMinDepth implements Test {
    int value;
    FilterMinDepth(int value) {
        this.value=value;
    }
    public boolean execute(FileStatus file) {
        //System.out.println(file.getPath() + " - " + file.getPath().depth() + " - value; " + value + "result: " + (file.getPath().depth() > value -1 ));
        return (file.getPath().depth() > value -1 );
    }
}
class FilterMaxDepth implements Test {
    int value;
    FilterMaxDepth(int value) {
        this.value=value;
    }
    public boolean execute(FileStatus file) {
        return (file.getPath().depth() < value);
    }
}

class FilterName implements Test {
    Pattern p;
    FilterName(Pattern p) {
        this.p=p;
    }
    public boolean execute(FileStatus file) {
        return (p.matcher(file.getPath().getName()).matches());
    }
}

class FilterAccessTimeNewer implements Test {
    long value;
    FilterAccessTimeNewer(long value) {
        this.value=value;
    }
    public boolean execute(FileStatus file) {
        return (file.getAccessTime() >= value);
    }
}
class FilterAccessTimeOlder implements Test {
    long value;
    FilterAccessTimeOlder(long value) {
        this.value=value;
    }
    public boolean execute(FileStatus file) {
        return (file.getAccessTime() <= value);
    }
}
class FilterAccessTimeEqualDay implements Test {
    long value;
    FilterAccessTimeEqualDay(long value) {
        this.value=value;
    }
    public boolean execute(FileStatus file) {
        long a= file.getAccessTime();
        a = a - a%60000;
        return (a  == value);
    }
}
class FilterAccessTimeEqualMin implements Test {
    long value;
    FilterAccessTimeEqualMin(long value) {
        this.value=value;
    }
    public boolean execute(FileStatus file) {
        long a= file.getAccessTime();
        a -= a % (60000*1440);
        long v = value - value%(60000*1440);
        return (a  == v);
    }
}
class FilterModificationTimeNewer implements Test {
    long value;
    FilterModificationTimeNewer(long value) {
        this.value=value;
    }
    public boolean execute(FileStatus file) {
        return (file.getModificationTime() >= value);
    }
}
class FilterModificationTimeOlder implements Test {
    long value;
    FilterModificationTimeOlder(long value) {
        this.value=value;
    }
    public boolean execute(FileStatus file) {
        return (file.getModificationTime() <= value);
    }
}
class FilterModificationTimeEqualDay implements Test {
    long value;
    FilterModificationTimeEqualDay(long value) {
        this.value=value;
    }
    public boolean execute(FileStatus file) {
        long a= file.getModificationTime();
        a = a - a%60000;
        return (a  == value);
    }
}
class FilterModificationTimeEqualMin implements Test {
    long value;
    FilterModificationTimeEqualMin(long value) {
        this.value=value;
    }
    public boolean execute(FileStatus file) {
        long a= file.getModificationTime();
        a -= a % (60000 * 1440);
        long v = value - value%(60000*1440);
        return (a  == v);
    }
}
class FilterNewer implements Test {
    FileStatus value;
    FilterNewer(FileStatus value) {
        this.value=value;
    }
    public boolean execute(FileStatus file) {
        return (file.getModificationTime() >= value.getModificationTime());
    }
}
class FilterANewer implements Test {
    FileStatus value;
    FilterANewer(FileStatus value) {
        this.value=value;
    }
    public boolean execute(FileStatus file) {
        return (file.getAccessTime() >= value.getAccessTime());
    }
}


class FilterType implements Test {
    char value;
    FilterType(char value) {
        this.value=value;
    }
    public boolean execute(FileStatus file) {
        return switch (value) {
            case 'f' -> file.isFile();
            case 'd' -> file.isDirectory();
            case 'l' -> file.isSymlink();
            default -> false;
        };
    }
}

class FilterSizeBigger implements Test {
    long value;
    FilterSizeBigger(long value) {
        this.value=value;
    }
    public boolean execute(FileStatus file) {
        return (file.getLen() >= value);
    }
}
class FilterSizeSmaller implements Test {
    long value;
    FilterSizeSmaller(long value) {
        this.value=value;
    }
    public boolean execute(FileStatus file) {
        return (file.getLen() <= value);
    }
}
class FilterSizeByteEqual implements Test {
    long value;
    FilterSizeByteEqual(long value) {
        this.value=value;
    }
    public boolean execute(FileStatus file) {
        return (file.getLen() == value);
    }
}
class FilterSizeKilobyteEqual implements Test {
    long value;
    FilterSizeKilobyteEqual(long value) {
        this.value=value;
    }
    public boolean execute(FileStatus file) {
        long a = file.getLen();
        a -= a%1024;
        return (a == value);
    }
}
class FilterSizeMegabyteEqual implements Test {
    long value;
    FilterSizeMegabyteEqual(long value) {
        this.value=value;
    }
    public boolean execute(FileStatus file) {
        long a = file.getLen();
        a -= a%(1024*1024);
        return (a == value);
    }
}
class FilterSizeGigabyteEqual implements Test {
    long value;
    FilterSizeGigabyteEqual(long value) {
        this.value=value;
    }
    public boolean execute(FileStatus file) {
        long a = file.getLen();
        a -= a%(1024*1024*1024);
        return (a == value);
    }
}

class FilterOr implements Test {
    public boolean execute(FileStatus file) {
        return true;
    }
}
class FilterAnd implements Test {
    public boolean execute(FileStatus file) {
        return true;
    }
}
class FilterTrue implements Test {
    public boolean execute(FileStatus file) {
        return true;
    }
}
