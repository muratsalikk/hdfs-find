import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;

import java.util.regex.Pattern;

public class TestArg {
    private final Test test;
    private final String cond;

    public TestArg(Builder builder) {
        switch (builder.test) {
            case NAME -> this.test = new FilterName(builder.patternValue);
            case PATH -> this.test = new FilterPath(builder.patternValue);
            case MAXDEPTH -> this.test = new FilterMaxDepth(builder.intValue);
            case MINDEPTH -> this.test = new FilterMinDepth(builder.intValue);
            case ACCESS_TIME_OLDER -> this.test = new FilterAccessTimeOlder(builder.longValue);
            case ACCESS_TIME_NEWER -> this.test = new FilterAccessTimeNewer(builder.longValue);
            case ACCESS_TIME_EQUAL_MIN -> this.test = new FilterAccessTimeEqualMin(builder.longValue);
            case ACCESS_TIME_EQUAL_DAY -> this.test = new FilterAccessTimeEqualDay(builder.longValue);
            case MODIFICATION_TIME_OLDER -> this.test = new FilterModificationTimeOlder(builder.longValue);
            case MODIFICATION_TIME_NEWER -> this.test = new FilterModificationTimeNewer(builder.longValue);
            case MODIFICATION_TIME_EQUAL_MIN -> this.test = new FilterModificationTimeEqualMin(builder.longValue);
            case MODIFICATION_TIME_EQUAL_DAY -> this.test = new FilterModificationTimeEqualDay(builder.longValue);
            case NEWER_MODIFICATION_TIME -> this.test = new FilterNewer(builder.fileValue);
            case NEWER_ACCESS_TIME -> this.test = new FilterANewer(builder.fileValue);
            case TYPE -> this.test = new FilterType(builder.charValue);
            case EMPTY -> this.test = new FilterEmpty(builder.booleanValue);
            case GROUP -> this.test = new FilterGroup(builder.stringValue);
            case USER -> this.test = new FilterUser(builder.stringValue);
            case SIZE_BIGGER -> this.test = new FilterSizeBigger(builder.longValue);
            case SIZE_SMALLER-> this.test = new FilterSizeSmaller(builder.longValue);
            case SIZE_B_EQUAL -> this.test = new FilterSizeByteEqual(builder.longValue);
            case SIZE_KB_EQUAL -> this.test = new FilterSizeKilobyteEqual(builder.longValue);
            case SIZE_MB_EQUAL -> this.test = new FilterSizeMegabyteEqual(builder.longValue);
            case SIZE_GB_EQUAL -> this.test = new FilterSizeGigabyteEqual(builder.longValue);
            case OR -> this.test = new FilterOr();
            case AND -> this.test = new FilterAnd();
            default -> this.test = new FilterTrue();
        }
        this.cond= builder.test.toString();
    }

    String getCond() {
        return cond;
    }

    Test test() {
        return test;
    }
    TestArg getTestArg () {
        return this;
    }

    @Override
    public String toString() {
        return "Cond: " + cond + " test: " + test.getClass().getName() ;
    }

    public static class Builder {
        private final FilterArgNames test;
        private String stringValue;
        private char charValue;
        private int intValue;
        private boolean booleanValue;
        private long longValue;
        private Pattern patternValue;
        private FileStatus fileValue;

        public Builder(FilterArgNames test) {
            this.test=test;
        }

        public Builder value(String value) {
            this.stringValue=value;
            return this;
        }
        public Builder value(char value) {
            this.charValue=value;
            return this;
        }
        public Builder value(int value) {
            this.intValue=value;
            return this;
        }
        public Builder value(long value) {
            this.longValue=value;
            return this;
        }
        public Builder value(boolean value) {
            this.booleanValue=value;
            return this;
        }
        public Builder value(Pattern value) {
            this.patternValue=value;
            return this;
        }
        public Builder value(FileStatus value) {
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

