import Logger.Logger;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;

public class Main {
    private static final Logger logger = new Logger(Main.class);

    private static final Connect c =  new Connect() ;

    private static void scanFilesRecursive(FileSystem fs, Path path, int currentDepth, int minDepth, int maxDepth, FilterExecutor e, PrintExecutor p) throws IOException {
        if (currentDepth > maxDepth) {
            return;
        }

        FileStatus[] fileStatuses = fs.listStatus(path);

        for (FileStatus fileStatus : fileStatuses) {
            if (fileStatus.isDirectory()) {
                if (currentDepth >= minDepth) {
                    if ( e.filter(fileStatus) ) {
                        p.print(fileStatus);
                    }
                }
                scanFilesRecursive(fs, fileStatus.getPath(), currentDepth + 1, minDepth, maxDepth, e, p);
            } else {
                if (currentDepth >= minDepth) {
                    if ( e.filter(fileStatus) ) {
                      p.print(fileStatus);
                    }
                }
            }
        }
    }


    public static void main(String[] args) throws Exception {
        logger.debug("Starting with arguements: " + String.join(" ", args)) ;
        ArguementProcessor arguementProcessor = new ArguementProcessor(args);
        FilterExecutor e = new FilterExecutor(arguementProcessor.getFilterInfixList());
        ActionExecutor a = new ActionExecutor();
        PrintExecutor p = arguementProcessor.getPrintExecutor();
        scanFilesRecursive(c.getFileSystem(), arguementProcessor.getInitialPath(),  0, arguementProcessor.getMinDepth(), arguementProcessor.getMaxDepth(), e, p);
    }
}
