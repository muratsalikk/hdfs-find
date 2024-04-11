package Filters;

import org.apache.hadoop.fs.FileStatus;

public class FilterOperator implements Filter {


    String operatorName;

    public FilterOperator(String operatorName){
        this.operatorName = operatorName;
    }
    public String getOperatorName() {
        return operatorName;
    }

    @Override
    public boolean evaluate(FileStatus file) {
        return false;
    }
}
