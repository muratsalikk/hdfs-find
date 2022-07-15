import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.net.URI;

public class Connect {
    Configuration conf = new Configuration();
    static URI uri;


    static {
            try {
            uri = new URI("hdfs://localhost:9000");
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
                        uri = new URI(eElement.getElementsByTagName("value").item(0).getTextContent());
                        ;;;;System.out.println("from xml;"+ uri.toString());
                        break;
                    }
                }
            }

        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    URI getUri(){
        return uri;
    }


    FileSystem getFileSystem() {
        conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
        FileSystem hfs = null;
        try {
            hfs = FileSystem.get(uri, conf);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return hfs;
    }

    FileStatus getFileStatus(Path p) {
        try {
            return getFileSystem().getFileStatus(p);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
