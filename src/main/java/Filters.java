import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Filters {
    public Filters() {
    }
        /*
    public boolean filterDepth(Path path, FilterArg f){
        return true;
    }

    public boolean filterType(FileStatus file){
        if (s.isType){
            return((s.TYPE==1 && file.isFile()) || ((s.TYPE==2 && file.isDirectory())));
        } else {return true;}
    }

    public boolean filterOwner(FileStatus file){
        if(s.isOwner){
            return(s.OWNER.equals(file.getOwner()));
        } else {return true;}
    }

    public boolean filterGroup(FileStatus file){
        if(s.isGroup){
            return(s.GROUP.equals(file.getGroup()));
        } else {return true;}
    }
    public boolean filterMtime(FileStatus file){
        if (s.isMtime){
            return(compareLongs(s.MTIME, file.getModificationTime(),true));
        }else {return true;}
    }

    public boolean filterMmin(FileStatus file){
        if (s.isMmin){
            return(compareLongs(s.MMIN, file.getModificationTime(),true));
        }else {return true;}
    }

    public boolean filterAtime(FileStatus file){
        if (s.isAtime){
            return(compareLongs(s.ATIME, file.getAccessTime(),true));
        }else {return true;}
    }

    public boolean filterAmin(FileStatus file){
        if (s.isAmin){
            return(compareLongs(s.AMIN, file.getAccessTime(),true));
        }else {return true;}
    }

    public boolean filterSize(FileStatus file){
        if (s.isSize){
            return(compareLongs(s.SIZE, file.getLen(),false));
        }else {return true;}
    }
*/

    boolean filterName(FileStatus file, Pattern p ) {
        return (p.matcher(file.getPath().getName()).matches());
    }

    boolean filterAccessTime(FileStatus file, long value, int identifier) {
        boolean r=false;
        if (identifier == 0) {
            r = (file.getAccessTime() >= value);
        } else if (identifier == 1) {
            r = (file.getAccessTime() <= value);
        } else if (identifier == 2) {
            long a= file.getAccessTime();
            a = a - a%60000;
            r = (a  == value);
        }else if (identifier == 3) {
            long a= file.getAccessTime();
            a = a - a%(60000*1440);
            long v = value - value%(60000*1440);
            r = (a  == v);
        }
        return r;
    }

    boolean filterModificationTime(FileStatus file, long value, int identifier) {
        boolean r=false;
        if (identifier == 0) {
            r = (file.getModificationTime() >= value);
        } else if (identifier == 1) {
            r = (file.getModificationTime() <= value);
        } else if (identifier == 2) {
            long a= file.getModificationTime();
            a = a - a%60000;
            r = (a  == value);
        } else if (identifier == 3) {
            long a= file.getModificationTime();
            a -= a % (60000 * 1440);
            long v = value - value%(60000*1440);
            r = (a  == v);
        }
        return r;
    }

    boolean filterNewer (FileStatus file, String reference, FileSystem hfs, int identifier) {
        boolean r= false;
        Path p = new Path(reference);
        FileStatus ref = null;

        try {
            ref = hfs.getFileStatus(p);
        } catch (IOException e) {
            System.out.println("reference file (" + reference + ") does not found.");
            System.exit(1);
        }
        if (identifier == 0) {
            r = (file.getAccessTime() > ref.getAccessTime());
        } else if (identifier == 1){
            r = (file.getModificationTime() > ref.getModificationTime());
        }
        return r;
    }




}
