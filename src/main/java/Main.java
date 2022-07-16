import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("from main:" + Arrays.toString(args));
        ArgProcess ap = new ArgProcess(args);
        Filter f = new Filter(ap.getTestArgList(),ap.getInitialPath(), new Connect().getFileSystem());
    }
}
