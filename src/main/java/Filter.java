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

        for (FilterArg  f : al) {
            if (f.getCond().equals("depth") || f.getCond().equals("mindepth") || f.getCond().equals("maxdepth")) {
                f.setValue(f.getIvalue() + wd.depth() + 1);
            }
        }

        filter(wd);
    }

    public void filter(Path wd) throws Exception{
        FileStatus[] fst = hfs.listStatus(wd);
        for (FileStatus item : fst) {
            if (item.isDirectory()){
                if (!(runFilterLogic(item))){
                    continue;
                }
                filter(item.getPath());
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
            case "depth" -> f.filterDepth(file, a.getIvalue());
            case "maxdepth" -> f.filterMaxDepth(file, a.getIvalue());
            case "mindepth" -> f.filterMinDepth(file,a.getIvalue());
            case "name" -> f.filterName(file, a.getPvalue()) ;
            case "mtime" -> f.filterModificationTime(file, a.getLvalue(), a.getIdentifier());
            case "atimeolder" -> f.filterAccessTimeOlder(file, a.getLvalue());
            case "atimenewer" -> f.filterAccessTimeNewer(file, a.getLvalue());
            case "atimeequalmin" -> f.filterAccessTimeEqualMin(file, a.getLvalue());
            case "atimeequalday" -> f.filterAccessTimeEqualDay(file, a.getLvalue());
            case "newer" -> f.filterNewer(file, a.getSvalue(), hfs, a.getIdentifier());
            default -> false;
        };
    }
}
