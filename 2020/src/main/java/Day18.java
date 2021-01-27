import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Day18 {

    private enum Operation {
        ADDITION('+'),
        MULTIPLICATION('*');

        private final char character;

        Operation(char character) {
            this.character = character;
        }
    }

    @Data
    @AllArgsConstructor
    private static class Expression {
        List<Expression> expressions;
        List<Operation> operations;
        Integer value;

        public long result() {
            if (expressions.size() == 0) return value;

            assert (expressions.size() == operations.size() + 1);
            long result = expressions.get(0).result();
            for (int i = 0; i < operations.size(); i++) {
                Operation operation = operations.get(i);
                Expression expression = expressions.get(i + 1);
                switch (operation) {
                    case ADDITION: result += expression.result(); break;
                    case MULTIPLICATION: result *= expression.result(); break;
                }
            }
            return result;
        }

        public void simplify() {
            if (value != null) return;
            if (expressions.size() <= 1) return;

            int index = 0;
            List<Expression> newExpressionList = new ArrayList<>();
            List<Operation> newOperationList = new ArrayList<>();
            while (index < expressions.size()) {

                Expression newExpression = new Expression(new ArrayList<>(), new ArrayList<>(), null);
                newExpression.expressions.add(expressions.get(index));
                for (int i = index; i < operations.size(); i++) {
                    Operation operation = operations.get(i);
                    Expression expression = expressions.get(i + 1);
                    if (operation == Operation.ADDITION) {
                        newExpression.operations.add(operation);
                        newExpression.expressions.add(expression);
                    }
                    else {
                        newOperationList.add(operation);
                        break;
                    }
                }
                index += newExpression.expressions.size();
                //unbox
                if (newExpression.expressions.size() == 1 && newExpression.expressions.get(0).value != null) {
                    newExpression = newExpression.expressions.get(0);
                }
                newExpression.expressions.forEach(Expression::simplify);
                newExpressionList.add(newExpression);
            }
            expressions = newExpressionList;
            operations = newOperationList;
        }

        public String toString() {
            return toString(false);
        }

        public String toString(boolean addBrackets) {
            if (value != null) return value.toString();

            assert (expressions.size() == operations.size() + 1);
            StringBuilder result = new StringBuilder("");
            if (addBrackets) result.append("(");
            result.append(expressions.get(0).toString(true));
            for (int i = 0; i < operations.size(); i++) {
                result.append(" ").append(operations.get(i).character);
                result.append(" ").append(expressions.get(i + 1).toString(true));
            }
            if (addBrackets) result.append(")");
            return result.toString();
        }
    }

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input18.csv");
        List<Expression> expressions = getExpressions(lines);
        for (Expression expression: expressions) {
            System.out.println(expression.toString() + " = " + expression.result());
            expression.simplify();
            System.out.println(expression.toString() + " = " + expression.result());
            System.out.println();
        }
        long sum = expressions.stream().map(Expression::result).reduce(0L, Long::sum);
        System.out.println("Sum: " + sum);
    }

    private static List<Expression> getExpressions(List<String> lines) {
        return lines.stream().map(Day18::getExpression).collect(Collectors.toList());
    }

    private static Expression getExpression(String string) {

        List<Operation> operations = new ArrayList<>();
        List<Expression> expressions = new ArrayList<>();
        int operandIndex = 0;
        List<String> operands = Arrays.asList(string.split(" "));
        while(operandIndex < operands.size()) {
            String operand = operands.get(operandIndex);
            if (operand.matches("[0-9]+")) {
                expressions.add(new Expression(Collections.emptyList(), Collections.emptyList(), Integer.parseInt(operand)));
            } else if (operand.matches("[*+]")) {
                operations.add(Arrays.stream(Operation.values()).filter(s -> s.character == operand.charAt(0)).findFirst().orElseThrow());
            } else {
                assert (operand.charAt(0) == '(');
                int lastOperandIndex = getIndexOfLastOperand(operands, operandIndex);
                String expressionWithBrackets = String.join(" ", operands.subList(operandIndex, lastOperandIndex + 1));
                expressions.add(getExpression(expressionWithBrackets.substring(1, expressionWithBrackets.length() - 1)));
                operandIndex = lastOperandIndex;
            }
            operandIndex += 1;
        }
        return new Expression(expressions, operations, null);
    }

    private static int getIndexOfLastOperand(List<String> operands, int startIndex) {
        int bracketBalance = 0;
        for (int i = startIndex; i < operands.size(); i++) {
            String operand = operands.get(i);
            bracketBalance += operand.chars().filter(c -> c == '(').count();
            bracketBalance -= operand.chars().filter(c -> c == ')').count();
            if (bracketBalance == 0) return i;
        }
        throw new IllegalStateException("invalid operands: " + operands.toString());
    }
}
