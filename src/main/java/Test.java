import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.util.regex.Pattern;

public interface Test {
    boolean execute(FileStatus file);
}

class filterName implements Test {
    Pattern p;
    filterName(Pattern p) {
        this.p=p;
    }

    public boolean execute(FileStatus file) {
        return (p.matcher(file.getPath().getName()).matches());
    }
}