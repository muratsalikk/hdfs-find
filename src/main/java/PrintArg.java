import org.apache.hadoop.fs.FileStatus;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PrintArg {
    String format;
    String type;
    List<ReplacePlaceHolder> pl = new ArrayList<>();
    public PrintArg(String[] format, String type) {
        for(String s : format) {
            this.format= s
                    .replace("\\t", "\t")
                    .replace("\\n", "\n")
                    .replace("\\r", "\r")
                    .replace("\\b", "\b")
                    .replace("\\s", "\s")
                    .replace("\\0", "\0")
                    .replace("\\'", "'")
                    .replace("\\\"", "\"");
        }
        this.type=type;
        processFormat();
    }

    public PrintArg(String type) {
        this.type=type;
        format="%P";
        processFormat();
    }

    void processFormat() {
        if(format.contains("%P"))
            pl.add(new ReplacePath());
        if(format.contains("%d"))
            pl.add(new ReplaceDepth());
        if(format.contains("%a"))
            pl.add(new ReplaceAccessTime());
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

class ReplaceDepth implements ReplacePlaceHolder {
    public String replacePlaceHolder(FileStatus file, String input) {
        String value = Integer.toString(file.getPath().depth());
        return input.replace("%d", value);
    }
}

class ReplaceAccessTime implements ReplacePlaceHolder {
    public String replacePlaceHolder(FileStatus file, String input) {
        DateFormat df = new SimpleDateFormat("dd MMM yyyy HH:mm:ss:SSS Z");
        String value = df.format(new Date(file.getAccessTime()));
        return  input.replace("%a", value);
    }
}
