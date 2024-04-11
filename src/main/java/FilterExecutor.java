import Filters.Filter;
import Filters.FilterOperator;
import Logger.Logger;
import org.apache.hadoop.fs.FileStatus;

import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;
import static Enums.OptionsEnum.*;
import java.util.List;


public class FilterExecutor {
    private static final Logger logger = new Logger(FilterExecutor.class);
    List<Filter> postfixFilterList;
    Stack<Filter> stack = new Stack<>();

    public FilterExecutor(List<Filter> infixFilterList){
        logger.debug(inFixToString(infixFilterList));
        this.postfixFilterList = new ArrayList<>();
        this.postfixFilterList = infix2postfixConverter(infixFilterList);
        logger.debug(postFixToString());

    }

    public boolean filter(FileStatus fileStatus) {
        logger.debug("Executing filters on: " + fileStatus.getPath().toString());
        Stack<Boolean> executionStack = new Stack<>();
        boolean a, b;
        for (Filter token : postfixFilterList) {
            if (token instanceof FilterOperator) {
                if (token.getOperatorName().equals(AND.opt)) {
                    a = executionStack.pop();
                    b = executionStack.pop();
                    executionStack.push( (a && b ) );
                } else if (token.getOperatorName().equals(OR.opt)) {
                    a = executionStack.pop();
                    b = executionStack.pop();
                    executionStack.push( (a || b) );
                } else if (token.getOperatorName().equals(NOT.opt)) {
                    a = executionStack.pop();
                    executionStack.push(!a);
                } else {
                    logger.error("Operator error");
                }
            } else {
                a = token.evaluate(fileStatus);
                executionStack.push(a);
                logger.debug("Evaluating filter: " + token.getClass().getName() + " Result: " + a );
            }

        }
        return executionStack.pop();
    }


    //Shunting yard algorithm
    List<Filter> infix2postfixConverter(List<Filter> infixFilterList) {

        Map<String, Integer> precedence = Map.of(
                AND.opt, 2,
                OR.opt, 1
        );


        for (Filter token : infixFilterList ) {

            if (token.getOperatorName().equals(LEFT_PARENTHESIS.opt)) {
                stack.push(token);
            } else if (token.getOperatorName().equals(RIGHT_PARENTHESIS.opt)) {
                while (!stack.isEmpty() && !stack.peek().getOperatorName().equals(LEFT_PARENTHESIS.opt)) {
                    postfixFilterList.add(stack.pop());
                }
                if (!stack.isEmpty() && stack.peek().getOperatorName().equals(LEFT_PARENTHESIS.opt)) {
                    stack.pop();
                }
            } else if (precedence.containsKey(token.getOperatorName())) {
                while (!stack.isEmpty() && precedence.getOrDefault(stack.peek().getOperatorName(), 0) >= precedence.get(token.getOperatorName())) {
                    postfixFilterList.add(stack.pop());
                }
                stack.push(token);
            } else {
                postfixFilterList.add(token);
            }

        }
        while (!stack.isEmpty()) {
            postfixFilterList.add(stack.pop());
        }

        return postfixFilterList;

    }


    String inFixToString(List<Filter> infixFilterList) {
        String a = "InFix list: ";
        for ( Filter token : infixFilterList ) {
            a += token.getClass().getName()  + ":" + token.getOperatorName() + " - " ;
        }
        return a;
    }


    String postFixToString() {
        String a = "PostFix list: ";
        for ( Filter token : postfixFilterList ) {
            a += token.getClass().getName()  + ":" + token.getOperatorName() + " - " ;
        }
        return a;
    }




}
