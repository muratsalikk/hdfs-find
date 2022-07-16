import java.util.HashMap;
import java.util.Map;

public enum Enums {
    MAXDEPTH("maxdepth",
            "Descend at most levels (a non-negative integer) levels of directories below the starting-points.",
            "n"),
    MINDEPTH("mindepth",
            "Do not apply any tests or actions at levels less than levels (a non-negative integer).",
            "n"),
    
    NAME("name",
            "Base of file name (the path with the leading directories removed) matches shell pattern pattern.",
            "string"),
    INAME("iname",
            "Like -name, but the match is case insensitive.",
            "string"),
    REGEX("regex",
            "File name matches regular expression pattern.",
            "pattern"),
    IREGEX("iregex",
            "Like -regex, but the match is case insensitive.",
            "pattern"),

    AMIN("amin",
            "File was last accessed n minutes ago.",
            "n"),
    ANEWER("anewer",
            "Time  of  the last access of the current file is " +
                    "more recent than that of the last data modification of the reference file.",
            "reference"),
    ATIME("atime",
            "File was last accessed n*24 hours ago.",
            "n"),
    MMIN("mmin",
            "File's data was last modified n minutes ago.",
            "n"),
    MTIME("mtime",
            "File's data was last modified n*24 hours ago.",
            "n"),
    NEWER("newer",
            "Time  of the last data modification of the current file is " +
                    "more recent than that of the last data modification of the reference file.",
            "reference"),

    TYPE("type", 
            "File is of type: d (directory) f (regular file) l (symbolic link)",
            "d|f|l"),
    SIZE("size",
            "File uses n units of space, rounding up. b (byte) k (kilobytes) m (megabytes) g (gigabytes)",
             "n(b|k|m|g)"),

    PRINTF("printf",
            "Print format on the standard output, with '%' directives.",
            "format"),

    PRINT0("print0",
            "Print full paths with separated by a null char.",
            null),
    
    OR("o",
            "Or..",
            null),
    AND("a",
            "And..",
             null),
    HELP("h",
            "Print help.", null)    ;

    final String opt;
    final String argName;
    final String desc;

    Enums (String opt,  String desc,  String argName) {
        this.opt = opt;
        this.desc = desc;
        this.argName = argName;
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

enum FilterArgNames {
    MINDEPTH, MAXDEPTH,
    NAME,
    ACCESS_TIME_OLDER, ACCESS_TIME_NEWER, ACCESS_TIME_EQUAL_MIN, ACCESS_TIME_EQUAL_DAY,
    MODIFICATION_TIME_OLDER, MODIFICATION_TIME_NEWER, MODIFICATION_TIME_EQUAL_MIN, MODIFICATION_TIME_EQUAL_DAY,
    NEWER_ACCESS_TIME, NEWER_MODIFICATION_TIME,
    TYPE,
    SIZE_BIGGER, SIZE_SMALLER, SIZE_B_EQUAL, SIZE_KB_EQUAL, SIZE_MB_EQUAL, SIZE_GB_EQUAL,
    OR, AND
}
