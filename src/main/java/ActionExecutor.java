import org.apache.hadoop.fs.FileStatus;

public class ActionExecutor {

    void action(FileStatus fileStatus) {
        System.out.println(fileStatus.getPath());

    }
}
