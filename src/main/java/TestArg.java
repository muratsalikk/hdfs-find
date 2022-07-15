import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;

import java.util.regex.Pattern;

public class TestArg {
    private final Test test;
    private String initialPath;
    private final String cond;

    public TestArg(TestArgBuilder builder) {
        switch (builder.test) {
            case "name" -> this.test = new FilterName(builder.patternValue);
            case "maxdepth" -> this.test = new FilterMaxDepth(builder.intValue);
            case "mindepth" -> this.test = new FilterMinDepth(builder.intValue);
            case "atimeolder" -> this.test = new FilterAccessTimeOlder(builder.longValue);
            case "atimenewer" -> this.test = new FilterAccessTimeNewer(builder.longValue);
            case "atimeequalmin" -> this.test = new FilterAccessTimeEqualMin(builder.longValue);
            case "atimeequalday" -> this.test = new FilterAccessTimeEqualDay(builder.longValue);
            case "mtimeolder" -> this.test = new FilterModificationTimeOlder(builder.longValue);
            case "mtimenewer" -> this.test = new FilterModificationTimeNewer(builder.longValue);
            case "mtimeequalmin" -> this.test = new FilterModificationTimeEqualMin(builder.longValue);
            case "mtimeequalday" -> this.test = new FilterModificationTimeEqualDay(builder.longValue);
            case "newer" -> this.test = new FilterNewer(builder.fileValue);
            case "anewer" -> this.test = new FilterANewer(builder.fileValue);
            case "type" -> this.test = new FilterType(builder.charValue);
            case "sizebigger" -> this.test = new FilterSizeBigger(builder.longValue);
            case "sizesmaller" -> this.test = new FilterSizeSmaller(builder.longValue);
            case "sizebequal" -> this.test = new FilterSizeByteEqual(builder.longValue);
            case "sizekequal" -> this.test = new FilterSizeKilobyteEqual(builder.longValue);
            case "sizemequal" -> this.test = new FilterSizeMegabyteEqual(builder.longValue);
            case "sizegequal" -> this.test = new FilterSizeGigabyteEqual(builder.longValue);
            case "initialPath" -> {
                this.initialPath = builder.stringValue;
                this.test= new FilterTrue();
            }
            case "OR" -> this.test = new FilterOr();
            case "AND" -> this.test = new FilterAnd();
            default -> this.test = new FilterTrue();
        }
        this.cond= builder.test;
    }

    String getCond() {
        return cond;
    }

    String getInitialPath() {
        return initialPath;
    }

    Test test() {
        return test;
    }

    @Override
    public String toString() {
        return "Cond: " + cond + " test: " + test.getClass().getName();
    }

    public static class TestArgBuilder {
        private final String test;
        private String stringValue;
        private char charValue;
        private int intValue;
        private boolean booleanValue;
        private long longValue;
        private Pattern patternValue;
        private FileStatus fileValue;

        public TestArgBuilder(String test) {
            this.test=test;
        }

        public TestArgBuilder value(String value) {
            this.stringValue=value;
            return this;
        }
        public TestArgBuilder value(char value) {
            this.charValue=value;
            return this;
        }
        public TestArgBuilder value(int value) {
            this.intValue=value;
            return this;
        }
        public TestArgBuilder value(long value) {
            this.longValue=value;
            return this;
        }
        public TestArgBuilder value(boolean value) {
            this.booleanValue=value;
            return this;
        }
        public TestArgBuilder value(Pattern value) {
            this.patternValue=value;
            return this;
        }
        public TestArgBuilder value(FileStatus value) {
            this.fileValue=value;
            return this;
        }

        public TestArg build() {
            TestArg t = new TestArg(this);
            validateValue();
            return t;
        }

        private void validateValue() {

        }

        private void fileValue(String s) {
            FileStatus file = new Connect().getFileStatus(new Path(s));
        }

    }

}

