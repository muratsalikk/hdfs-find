import java.net.URI;
import java.util.Arrays;
import java.io.File;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("from main:" + Arrays.toString(args));
        ArgProcess a = new ArgProcess(args);
        Filter f = new Filter(a.parseArgs(), new Connect().getFileSystem());
    }
}
