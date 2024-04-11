import org.apache.hadoop.fs.FileStatus;

import java.util.regex.Pattern;

public interface Test {
    boolean execute(FileStatus file);
}

class TestMinDepth implements Test {
    int value;
    TestMinDepth(int value) {
        this.value=value;
    }
    public boolean execute(FileStatus file) {
        //System.out.println(file.getPath() + " - " + file.getPath().depth() + " - value; " + value + "result: " + (file.getPath().depth() > value -1 ));
        return (file.getPath().depth() > value -1 );
    }
}
class TestMaxDepth implements Test {
    int value;
    TestMaxDepth(int value) {
        this.value=value;
    }
    public boolean execute(FileStatus file) {
        return (file.getPath().depth() < value);
    }
}

class TestName implements Test {
    Pattern p;
    TestName(Pattern p) {
        this.p=p;
    }
    public boolean execute(FileStatus file) {
        return (p.matcher(file.getPath().getName()).matches());
    }
}
class TestPath implements Test {
    Pattern p;
    TestPath(Pattern p) {
        this.p=p;
    }
    public boolean execute(FileStatus file) {
        return (p.matcher(file.getPath().toString()).matches());
    }
}

class TestAccessTimeNewer implements Test {
    long value;
    TestAccessTimeNewer(long value) {
        this.value=value;
    }
    public boolean execute(FileStatus file) {
        return (file.getAccessTime() >= value);
    }
}
class TestAccessTimeOlder implements Test {
    long value;
    TestAccessTimeOlder(long value) {
        this.value=value;
    }
    public boolean execute(FileStatus file) {
        return (file.getAccessTime() <= value);
    }
}
class TestAccessTimeEqualDay implements Test {
    long value;
    TestAccessTimeEqualDay(long value) {
        this.value=value;
    }
    public boolean execute(FileStatus file) {
        long a= file.getAccessTime();
        a = a - a%60000;
        return (a  == value);
    }
}
class TestAccessTimeEqualMin implements Test {
    long value;
    TestAccessTimeEqualMin(long value) {
        this.value=value;
    }
    public boolean execute(FileStatus file) {
        long a= file.getAccessTime();
        a -= a % (60000*1440);
        long v = value - value%(60000*1440);
        return (a  == v);
    }
}
class TestModificationTimeNewer implements Test {
    long value;
    TestModificationTimeNewer(long value) {
        this.value=value;
    }
    public boolean execute(FileStatus file) {
        return (file.getModificationTime() >= value);
    }
}
class TestModificationTimeOlder implements Test {
    long value;
    TestModificationTimeOlder(long value) {
        this.value=value;
    }
    public boolean execute(FileStatus file) {
        return (file.getModificationTime() <= value);
    }
}
class TestModificationTimeEqualDay implements Test {
    long value;
    TestModificationTimeEqualDay(long value) {
        this.value=value;
    }
    public boolean execute(FileStatus file) {
        long a= file.getModificationTime();
        a = a - a%60000;
        return (a  == value);
    }
}
class TestModificationTimeEqualMin implements Test {
    long value;
    TestModificationTimeEqualMin(long value) {
        this.value=value;
    }
    public boolean execute(FileStatus file) {
        long a= file.getModificationTime();
        a -= a % (60000 * 1440);
        long v = value - value%(60000*1440);
        return (a  == v);
    }
}
class TestNewer implements Test {
    FileStatus value;
    TestNewer(FileStatus value) {
        this.value=value;
    }
    public boolean execute(FileStatus file) {
        return (file.getModificationTime() >= value.getModificationTime());
    }
}
class TestANewer implements Test {
    FileStatus value;
    TestANewer(FileStatus value) {
        this.value=value;
    }
    public boolean execute(FileStatus file) {
        return (file.getAccessTime() >= value.getAccessTime());
    }
}


class TestType implements Test {
    char value;
    TestType(char value) {
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
class TestEmpty implements Test {
    boolean value;
    TestEmpty(boolean value) {
        this.value=value;
    }
    public boolean execute(FileStatus file) {
        return (file.getLen() == 0);
    }
}
class TestUser implements Test {
    String value;
    TestUser(String value) {
        this.value=value;
    }
    public boolean execute(FileStatus file) {
        return (file.getOwner().equals(value));
    }
}
class TestGroup implements Test {
    String value;
    TestGroup(String value) {
        this.value=value;
    }
    public boolean execute(FileStatus file) {
        return (file.getGroup().equals(value));
    }
}

class TestSizeBigger implements Test {
    long value;
    TestSizeBigger(long value) {
        this.value=value;
    }
    public boolean execute(FileStatus file) {
        return (file.getLen() >= value);
    }
}
class TestSizeSmaller implements Test {
    long value;
    TestSizeSmaller(long value) {
        this.value=value;
    }
    public boolean execute(FileStatus file) {
        return (file.getLen() <= value);
    }
}
class TestSizeByteEqual implements Test {
    long value;
    TestSizeByteEqual(long value) {
        this.value=value;
    }
    public boolean execute(FileStatus file) {
        return (file.getLen() == value);
    }
}
class TestSizeKilobyteEqual implements Test {
    long value;
    TestSizeKilobyteEqual(long value) {
        this.value=value;
    }
    public boolean execute(FileStatus file) {
        long a = file.getLen();
        a -= a%1024;
        return (a == value);
    }
}
class TestSizeMegabyteEqual implements Test {
    long value;
    TestSizeMegabyteEqual(long value) {
        this.value=value;
    }
    public boolean execute(FileStatus file) {
        long a = file.getLen();
        a -= a%(1024*1024);
        return (a == value);
    }
}
class TestSizeGigabyteEqual implements Test {
    long value;
    TestSizeGigabyteEqual(long value) {
        this.value=value;
    }
    public boolean execute(FileStatus file) {
        long a = file.getLen();
        a -= a%(1024*1024*1024);
        return (a == value);
    }
}

class TestOr implements Test {
    public boolean execute(FileStatus file) {
        return true;
    }
}
class TestAnd implements Test {
    public boolean execute(FileStatus file) {
        return true;
    }
}
class TestTrue implements Test {
    public boolean execute(FileStatus file) {
        return true;
    }
}
