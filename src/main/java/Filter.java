import java.util.*;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class Filter {
    List<TestArg> tl;
    FileSystem hfs;
    String initialPath;
    Path wd;

    PrintArg pa = ArgProcess.getPrintarg();

    public Filter(List<TestArg> tl, FileSystem hfs) throws Exception {
        this.tl = tl;
        for (TestArg f : tl){
            System.out.println(f.toString());
        }
        System.out.println("-----------");
        this.hfs=hfs;
        for (TestArg  f : tl) {
            if (f.getCond().equals("initialPath")) {
                initialPath=f.getInitialPath();
                tl.remove(f);
                break;
            }
        }
        assert initialPath != null;
        this.wd = new Path(initialPath);

        // TODO depth value should sum with wd.depth +1
        for (TestArg  t : tl) {
            if (t.getCond().equals("depth") || t.getCond().equals("mindepth") || t.getCond().equals("maxdepth")) {
                //t.setValue(t.getIvalue() + wd.depth() + 1);
            }
        }

        filter(wd);
    }

    public void filter(Path wd) throws Exception{
        FileStatus[] fst = hfs.listStatus(wd);
        for (FileStatus item : fst) {
            if (item.isDirectory()){
                filter(item.getPath());
            }
            if (!(runFilterLogic(item))){
                continue;
            }
            pa.print(item);
            //System.out.println(item.getPath().toString().replace(hfs.getUri().toString() , ""));
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
