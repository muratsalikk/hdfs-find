import java.net.URI;
import java.util.Arrays;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;

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