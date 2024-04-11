import java.util.*;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class TestExecution {
    List<TestArg> tl;
    FileSystem hfs;
    Path initialPath;
    TestArg maxD = null;

    PrintExecutor pa = ArgProcess.getPrintArg();

    public TestExecution(List<TestArg> tl, Path initialPath, FileSystem hfs) throws Exception {
        this.tl = tl;
        for (TestArg t : tl){
            System.out.println(t.toString());
        }
        System.out.println("-----------");
        this.hfs=hfs;
        this.initialPath=initialPath;

        boolean fwd = false;

        for (TestArg t : tl) {
            if ( t.getCond().equals("MAXDEPTH") ) {
                fwd = true;
                maxD = t;
            }
        }
        if (fwd) {
            filterWithDepth(this.initialPath);
            tl.remove(maxD);
        } else {
            filter(this.initialPath);
        }

    }

    public void filter(Path wd) throws Exception{
        FileStatus[] fst = hfs.listStatus(wd);
        for (FileStatus item : fst) {
            if (item.isDirectory()){
                filter(item.getPath());
            }
            if (runFilterLogic(item)){
                pa.print(item);
            }
        }
    }

    public void filterWithDepth(Path wd) throws Exception{
        FileStatus[] fst = hfs.listStatus(wd);
        for (FileStatus item : fst) {
            if (maxD != null && !maxD.test().execute(item)) {
                break;
            }
            if (item.isDirectory()){
                filterWithDepth(item.getPath());
            }
            if (runFilterLogic(item)){
                pa.print(item);
            }
        }
    }

    boolean runFilterLogic(FileStatus file) {
        boolean result=false;
        try {
            result = tl.get(0).test().execute(file);
            for (int i = 0; i < tl.size()-1; i++) {
                TestArg nextArg = tl.get(i + 1);
                if (nextArg.getCond().equals("OR")) {
                    nextArg= tl.get(i+2);
                    result = (result || nextArg.test().execute(file));
                    i++;
                } else if (nextArg.getCond().equals("AND")) {
                    nextArg= tl.get(i+2);
                    result = (result && nextArg.test().execute(file));
                    i++;
                } else {
                    result = (result && nextArg.test().execute(file));
                }
            }
        } catch (IndexOutOfBoundsException e) {
            if (tl.size()==0) {
                result=true;
            } else {
                e.printStackTrace();
            }
        }
        return result;
    }

}
