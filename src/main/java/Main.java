import java.io.IOException;
import java.net.URI;
import java.io.File;
import java.util.Arrays;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.io.IOUtils;



public class Main {
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        //conf.set("fs.hdfs.impl","hdfs://localhost:9000");
        URI h = new URI("hdfs://localhost:9000");
        conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
        FileSystem hfs = FileSystem.get(h, conf);

        System.out.println("from main:"+ Arrays.toString(args));
        ArgProcess a = new ArgProcess(args);

        Filter f = new Filter(a.parseArgs(), hfs);
    }
}