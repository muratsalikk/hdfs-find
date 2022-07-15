import org.apache.hadoop.fs.FileStatus;

import java.util.ArrayList;
import java.util.List;

public class PrintArg {
    String format;
    String type;
    List<ReplacePlaceHolder> pl = new ArrayList<>();
    public PrintArg(String[] format, String type) {
        for(String s : format) {
            this.format=new StringBuilder().append(s).append(" ").toString();
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

