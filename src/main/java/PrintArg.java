import org.apache.hadoop.fs.FileStatus;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PrintArg {
    String format;
    String type;
    List<ReplacePlaceHolder> pl = new ArrayList<>();
    public PrintArg(String[] format, String type) {
        for(String s : format) {
            this.format= s + s;
        }
        this.type=type;
        chooseFormatType();
    }

    public PrintArg(String type) {
        this.type=type;
        chooseFormatType();
    }

    void chooseFormatType() {
        if (type.equals("pritf")) {
            format = format.replace("\\t", "\t")
                    .replace("\\n", "\n")
                    .replace("\\r", "\r")
                    .replace("\\b", "\b")
                    .replace("\\s", "\s")
                    .replace("\\0", "\0")
                    .replace("\\'", "'")
                    .replace("\\\"", "\"");
        } else if (type.equals("ls")) {
            format = "%y%M\t%u\t%g\t%t\t%P\n";
        } else if (type.equals("default")) {
            format = "%P\n";
        }
        processFormat();

    }


    void processFormat() {
        if(format.contains("%P"))
            pl.add(new ReplacePath());
        if(format.contains("%d"))
            pl.add(new ReplaceDepth());
        if(format.contains("%a"))
            pl.add(new ReplaceAccessTime());
        if(format.contains("%t"))
            pl.add(new ReplaceModificationTime());
        if(format.contains("%u"))
            pl.add(new ReplaceUser());
        if(format.contains("%g"))
            pl.add(new ReplaceGroup());
        if(format.contains("%y"))
            pl.add(new ReplaceType());
        if(format.contains("%s"))
            pl.add(new ReplaceSize());
        if(format.contains("%m"))
            pl.add(new ReplacePermissionOctal());
        if(format.contains("%M"))
            pl.add(new ReplacePermissionSymbolic());
    }

    void print(FileStatus file) {
        String f = format;
        for (ReplacePlaceHolder rpl : pl) {
            f = rpl.replacePlaceHolder(file, f);
        }
        if (type.equals("default")) {
            System.out.println(f.replace(new Connect().getUri().toString(), ""));
        } else {
            System.out.print(f);
        }
    }
}

interface ReplacePlaceHolder {
    String replacePlaceHolder(FileStatus file, String input);
}

class ReplacePath implements ReplacePlaceHolder {
    public String replacePlaceHolder(FileStatus file, String input) {
        String value=file.getPath().toString();
        return input.replace("%P", value);
    }
}

class ReplaceAccessTime implements ReplacePlaceHolder {
    public String replacePlaceHolder(FileStatus file, String input) {
        DateFormat df = new SimpleDateFormat("dd MMM yyyy HH:mm:ss:SSS Z");
        String value = df.format(new Date(file.getAccessTime()));
        return  input.replace("%a", value);
    }
}

class ReplaceModificationTime implements ReplacePlaceHolder {
    public String replacePlaceHolder(FileStatus file, String input) {
        DateFormat df = new SimpleDateFormat("dd MMM yyyy HH:mm:ss:SSS Z");
        String value = df.format(new Date(file.getModificationTime()));
        return  input.replace("%t", value);
    }
}

class ReplaceUser implements ReplacePlaceHolder {
    public String replacePlaceHolder(FileStatus file, String input) {
        String value = file.getOwner();
        return  input.replace("%u", value);
    }
}

class ReplaceGroup implements ReplacePlaceHolder {
    public String replacePlaceHolder(FileStatus file, String input) {
        String value = file.getGroup();
        return  input.replace("%g", value);
    }
}

class ReplaceType implements ReplacePlaceHolder {
    public String replacePlaceHolder(FileStatus file, String input) {
        String value = "U";
        if (file.isFile()) {
            value = "-";
        } else if (file.isDirectory()) {
            value = "d";
        } else if (file.isSymlink()) {
            value = "l";
        }
        return  input.replace("%y", value);
    }
}

class ReplaceSize implements ReplacePlaceHolder {
    public String replacePlaceHolder(FileStatus file, String input) {
        String value = Long.toString(file.getLen());
        return  input.replace("%s", value);
    }
}

class ReplacePermissionOctal implements ReplacePlaceHolder {
    public String replacePlaceHolder(FileStatus file, String input) {
        String value = Short.toString(file.getPermission().toOctal());
        return  input.replace("%m", value);
    }
}

class ReplacePermissionSymbolic implements ReplacePlaceHolder {
    public String replacePlaceHolder(FileStatus file, String input) {
        String value = file.getPermission().toString();
        return  input.replace("%M", value);
    }
}


class ReplaceDepth implements ReplacePlaceHolder {
    public String replacePlaceHolder(FileStatus file, String input) {
        String value = Integer.toString(file.getPath().depth());
        return input.replace("%d", value);
    }
}