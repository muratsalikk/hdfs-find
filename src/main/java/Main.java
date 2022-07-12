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
        Configuration conf = new Configuration();
        URI h;
        { // get uri
            h = new URI("hdfs://localhost:9000");
            String hdfsCoreFile = null;
            if (System.getenv("HADOOP_CONF_DIR") == null) {
                hdfsCoreFile = System.getenv("HADOOP_HDFS_HOME") + "/etc/hadoop/" + "core-site.xml";
            } else {
                hdfsCoreFile = System.getenv("HADOOP_CONF_DIR") + "core-site.xml";
            }
            File f = new File(hdfsCoreFile);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(f);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("property");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) node;
                    if (eElement.getElementsByTagName("name").item(0).getTextContent().equals("fs.default.name")) {
                        h = new URI(eElement.getElementsByTagName("value").item(0).getTextContent());
                        break;
                    }
                }
            }
        }
        conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
        FileSystem hfs = FileSystem.get(h, conf);

        System.out.println("from main:" + Arrays.toString(args));
        ArgProcess a = new ArgProcess(args);

        Filter f = new Filter(a.parseArgs(), hfs);
    }
}
