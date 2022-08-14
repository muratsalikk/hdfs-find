import org.apache.hadoop.fs.FileStatus;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.HashMap;
import java.util.Map;

public class PrintArg {
    String format;
    String type;
    List<ReplacePlaceHolder> formatterList = new ArrayList<>();
    public PrintArg(String[] format, String type) {
        //for printf
        StringBuilder sBuilder = new StringBuilder();
        for(String s : format) {
            sBuilder.append(s);
        }
        this.format= sBuilder.toString();
        this.type=type;
        chooseFormatType();
    }

    public PrintArg(String type) {
        //for others
        this.type=type;
        chooseFormatType();
    }

    void chooseFormatType() {
        System.out.println("Chooseformattype: " + type);
        if (type.equals("printf")) {
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
        } else if (type.equals("print0")) {
            format = "%P\0";
        } else if (type.equals("default")) {
            format = "%P\n";
        }
        processFormat();
    }

    void processFormat() {
        for (PlaceHolders p : PlaceHolders.values()) {
            if (format.contains(p.placeHolder))
                formatterList.add(p.replacerClass);
        }
    }

    void print(FileStatus file) {
        String finalFormat = format;
        for (ReplacePlaceHolder rpl : formatterList) {
            finalFormat = rpl.replacePlaceHolder(file, finalFormat);
        }

        if (type.equals("default")) {
            System.out.print(finalFormat);
        } else {
            System.out.print(finalFormat);
        }
    }
}


enum PlaceHolders {
    PHPATH("%P", new ReplacePath(), ""),
    PHDEPTH("%d", new ReplaceDepth(), ""),
    PHACCESS("%a", new ReplaceAccessTime(), ""),
    PHMODIFICATON("%t", new ReplaceModificationTime(), ""),
    PHUSER("%u", new ReplaceUser(), ""),
    PHGROUP("%g", new ReplaceGroup(), ""),
    PHTYPE("%y", new ReplaceType(), ""),
    PHSIZE("%s", new ReplaceSize(), ""),
    PHPERMOCTAL("%m", new ReplacePermissionOctal(), ""),
    PHPERMSYM("%M", new ReplacePermissionSymbolic(), ""),

        PHTIMEH("%TH", new ReplaceTimeWithFormat("%TH"), "hour (00..23)"),
        PHTIMEI("%TI", new ReplaceTimeWithFormat("%TI"), ""),
        PHTIMEk("%Tk", new ReplaceTimeWithFormat("%Tk"), ""),
        PHTIMEl("%Tl", new ReplaceTimeWithFormat("%Tl"), ""),
        PHTIMEM("%TM", new ReplaceTimeWithFormat("%TM"), ""),
        PHTIMEp("%Tp", new ReplaceTimeWithFormat("%Tp"), ""),
        PHTIMEr("%Tr", new ReplaceTimeWithFormat("%Tr"), ""),
        PHTIMES("%TS", new ReplaceTimeWithFormat("%TS"), ""),
        PHTIMET("%TT", new ReplaceTimeWithFormat("%TT"), ""),
        PHTIMEPLUS("%T+", new ReplaceTimeWithFormat("%T+"), ""),
        PHTIMEa("%Ta", new ReplaceTimeWithFormat("%Ta"), ""),
        PHTIMEA("%TA", new ReplaceTimeWithFormat("%TA"), ""),
        PHTIMEb("%Tb", new ReplaceTimeWithFormat("%Tb"), ""),
        PHTIMEB("%TB", new ReplaceTimeWithFormat("%TB"), ""),
        PHTIMEd("%Td", new ReplaceTimeWithFormat("%Td"), ""),
        PHTIMED("%TD", new ReplaceTimeWithFormat("%TD"), ""),
        PHTIMEh("%Th", new ReplaceTimeWithFormat("%Th"), ""),
        PHTIMEj("%Tj", new ReplaceTimeWithFormat("%Tj"), ""),
        PHTIMEm("%Tm", new ReplaceTimeWithFormat("%Tm"), ""),
        PHTIMEw("%Tw", new ReplaceTimeWithFormat("%Tw"), ""),
        PHTIMEW("%TW", new ReplaceTimeWithFormat("%TW"), ""),
        PHTIMEy("%Ty", new ReplaceTimeWithFormat("%Ty"), ""),
        PHTIMEY("%TY", new ReplaceTimeWithFormat("%TY"), ""),


    ;

    final String placeHolder;
    final ReplacePlaceHolder replacerClass;
    final String desc;

    PlaceHolders(String placeHolder,ReplacePlaceHolder replacerClass, String desc) {
        this.placeHolder = placeHolder;
        this.replacerClass = replacerClass;
        this.desc = desc;
    }
    private static final Map<String,Enums> map;
    static {
        map = new HashMap<String,Enums>();
        for (Enums v : Enums.values()) {
            map.put(v.opt, v);
        }
    }
    public static Enums findByOpt(String opt) {
        return map.get(opt);
    }

}
interface ReplacePlaceHolder {
    String replacePlaceHolder(FileStatus file, String input);
}

class ReplacePath implements ReplacePlaceHolder {
    public String replacePlaceHolder(FileStatus file, String input) {
        String value=file.getPath().toString();
        return input.replace("%P", value.replace(new Connect().getUri().toString(), ""));
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
    String timeFormat="dd MMM yyyy HH:mm:ss.SSS";
    public String replacePlaceHolder(FileStatus file, String input) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(timeFormat);
        LocalDateTime cvDate = Instant.ofEpochMilli(file.getModificationTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        String value = cvDate.format(dateTimeFormatter);
        return  input.replace("%t", value);
    }
}


class ReplaceUser implements ReplacePlaceHolder {
    @Override
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
    @Override
    public String replacePlaceHolder(FileStatus file, String input) {
        String value = Integer.toString(file.getPath().depth());
        return input.replace("%d", value);
    }
}

class ReplaceTimeWithFormat implements ReplacePlaceHolder {
    String format;
    String placeHolder;
    public ReplaceTimeWithFormat(String placeHolder) {
        this.placeHolder=placeHolder;
        char givenHolder = placeHolder.charAt(placeHolder.length()-1);
        this.format= new FormatTimeParameter().formatTimeParameter(givenHolder);
    }

    @Override
    public String replacePlaceHolder(FileStatus file, String input) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
        LocalDateTime cvDate = Instant.ofEpochMilli(file.getModificationTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        String value = cvDate.format(dateTimeFormatter);
        return  input.replace(placeHolder, value);
    }

}

class FormatTimeParameter {
    public String formatTimeParameter(char givenHolder) {
        String format ="";
        if (givenHolder == 'H') {
            format="HH";
        } else if (givenHolder == 'I') {
            format="KK";
        } else if (givenHolder == 'k') {
            format="H";
        } else if (givenHolder == 'l') {
            format="K";
        } else if (givenHolder == 'M') {
            format="mm";
        } else if (givenHolder == 'p') {
            format="a";
        } else if (givenHolder == 'r') {
            format="HH:mm:ss a";
        } else if (givenHolder == 'S') {
            format="ss.SS";
        } else if (givenHolder == 'T') {
            format="HH:mm:ss.SSSS";
        } else if (givenHolder == '+') {
            format="yyyy-MM-dd+HH:mm:ss.SSSS";
        } else if (givenHolder == 'a') {
            format="EE";
        } else if (givenHolder == 'A') {
            format="EEEE";
        } else if (givenHolder == 'b') {
            format="MMM";
        } else if (givenHolder == 'B') {
            format="MMMM";
        } else if (givenHolder == 'd') {
            format="dd";
        } else if (givenHolder == 'D') {
            format="dd/MM/yy";
        } else if (givenHolder == 'h') {
            format="MMM";
        } else if (givenHolder == 'j') {
            format="D";
        } else if (givenHolder == 'm') {
            format="MM";
        } else if (givenHolder == 'w') {
            format="e";
        } else if (givenHolder == 'W') {
            format="ww";
        } else if (givenHolder == 'y') {
            format="yy";
        } else if (givenHolder == 'Y') {
            format="yyyy";
        }
        return format;
    }
}