import java.io.IOException;
import java.util.*;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class Filter {
    List<FilterArg> al;
    FileSystem hfs;
    String initialPath;
    Path wd;

    public Filter(List<FilterArg> al,FileSystem hfs) throws Exception {
        this.al=al;
        for (FilterArg f : al){
            System.out.println(f.getAllInfo());
        }
        this.hfs=hfs;
        for (FilterArg  f : al) {
            if (f.getCond().equals("initialPath")) {
                initialPath=f.getSvalue();
                al.remove(f);
                break;
            }
        }
        assert initialPath != null;
        this.wd = new Path(initialPath);
        filter(this.wd);
    }

    public void filter(Path wd) throws Exception{
        FileStatus[] fst = hfs.listStatus(wd);
        for (FileStatus item : fst) {
            if (item.isDirectory()){
                if (!(runFilterLogic(item))){
                    continue;
                }
                continue;
                //filter(item.getPath());
                //if (!(runFilterLogic(item.getPath()))){ }
            } else{
                if (!(runFilterLogic(item))){
                    continue;
                }
            }
            System.out.println(item.getPath().toString().replace(hfs.getUri().toString() , ""));
        }
    }

    boolean runFilterLogic(FileStatus f) {
        boolean result=false;
        //System.out.println("running for >>>>>> "+p.getAllInfo());
        try {
            FilterArg arg = al.get(0);
            result= runFilter(arg,f);
            for (int i = 0; i < al.size()-1; i++) {
                FilterArg nextArg = al.get(i + 1);
                //System.out.println("arg: "+arg.getAllInfo());

                if (nextArg.getCond().equals("OR")) {
                    nextArg=al.get(i+2);
                    //System.out.println("narg: "+nextArg.getAllInfo());
                    //System.out.println("or: "+result +"||"+runFilter(nextArg, p));
                    result = (result || runFilter(nextArg, f));
                    i++;
                } else if (nextArg.getCond().equals("AND")) {
                    nextArg=al.get(i+2);
                    //System.out.println("narg: "+nextArg.getAllInfo());
                    //System.out.println("and: "+result +"&&"+runFilter(nextArg, p));
                    result = (result && runFilter(nextArg, f));
                    i++;
                } else {
                    //System.out.println("narg: "+nextArg.getAllInfo());
                    //System.out.println("empty: "+result +"&&"+runFilter(nextArg, p));
                    result = (result && runFilter(nextArg, f));
                }
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        //System.out.println("result> "+result );
        return result;
    }

    boolean runFilter (FilterArg a, FileStatus file) {
        Filters f = new Filters();
        return switch (a.getCond()) {
            case "name" -> f.filterName(file, a.getPvalue()) ;
            case "mtime" -> f.filterModificationTime(file, a.getLvalue(), a.getIdentifier());
            case "atime" -> f.filterAccessTime(file, a.getLvalue(), a.getIdentifier());
            case "newer" -> f.filterNewer(file, a.getSvalue(), hfs, a.getIdentifier());

            default -> false;
        };
    }
}
