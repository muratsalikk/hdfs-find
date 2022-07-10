import org.apache.hadoop.fs.FileStatus;

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

    boolean filterName(Pattern p, FileStatus file) {
        return (p.matcher(file.getPath().getName()).matches());
    }
    boolean filterModificationTime(long value, FileStatus file, int identifier) {
        return true;
    }
}
