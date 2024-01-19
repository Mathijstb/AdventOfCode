package day19;

import fileUtils.FileReader;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static day19.RangeType.*;

public class Day19 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input19.csv");
        var splitLines = FileReader.splitLines(lines, String::isEmpty);
        var workflows = parseWorkflows(splitLines.get(0));
        var machineParts = parseMachineParts(splitLines.get(1));
        processMachineParts(machineParts, workflows);

        //part 2
        var ranges = new Ranges(Map.of(X, new Range(1, 4000), M, new Range(1, 4000), A, new Range(1, 4000), S, new Range(1, 4000)), "in");
        var rangesList = processRanges(ranges, workflows);
        var numberOfAcceptedRanges = calculateNumberOfAcceptedRanges(rangesList);
        System.out.println("Number of accepted ranges: " + numberOfAcceptedRanges);
    }

    private static long calculateNumberOfAcceptedRanges(List<Ranges> rangesList) {
        return rangesList.stream().filter(ranges -> ranges.target().equals("A"))
                .map(ranges -> ranges.rangeMap().values().stream()
                        .mapToLong(range -> range.to() - range.from() + 1)
                        .reduce(1L, (a,b) -> a * b))
                .reduce(0L, Long::sum);
    }

    private static List<Ranges> processRanges(Ranges ranges, List<Workflow> workflows) {
        var workflowMap = workflows.stream().collect(Collectors.toMap(Workflow::name, w -> w));
        List<Ranges> current = new ArrayList<>(List.of(ranges));
        List<Ranges> result = new ArrayList<>();
        while(!current.isEmpty()) {
            var next = current.remove(0);
            var workflow = workflowMap.get(next.target());
            var rangesList = processRanges(next, workflow);
            for (Ranges r : rangesList) {
                if (List.of("R", "A").contains(r.target())) {
                    result.add(r);
                }
                else {
                    current.add(r);
                }
            }
        }
        return result;
    }

    private static List<Ranges> processRanges(Ranges ranges, Workflow workflow) {
        var result = new ArrayList<Ranges>();
        var current = ranges;
        for(Rule rule : workflow.rules()) {
            var successRanges = rule.determineSuccessResult(current);
            successRanges.ifPresent(result::add);
            var failRanges = rule.determineFailResult(current);
            if (failRanges.isEmpty()) {
                break;
            }
            else {
                current = failRanges.get();
            }
        }
        return result;
    }

    private static void processMachineParts(List<MachinePart> machineParts, List<Workflow> workflows) {
        var workflowMap = workflows.stream().collect(Collectors.toMap(Workflow::name, w -> w));
        var resultMap = machineParts.stream().collect(Collectors.toMap(Function.identity(), m -> process(workflowMap, m)));
        var totalAcceptedRating = resultMap.entrySet().stream()
                .filter(entry -> entry.getValue().equals(ProcessResult.ACCEPTED))
                .map(entry -> getRating(entry.getKey()))
                .reduce(0, Integer::sum);
        System.out.println("Total accepted rating: " + totalAcceptedRating);
    }

    private static int getRating(MachinePart machinePart) {
        return machinePart.x() + machinePart.m() + machinePart.a() + machinePart.s();
    }

    private static ProcessResult process(Map<String, Workflow> workflowMap, MachinePart machinePart) {
        var current = "in";
        while (!List.of("R", "A").contains(current)) {
            current = workflowMap.get(current).determineResult(machinePart);
        }
        return current.equals("R") ? ProcessResult.REJECTED : ProcessResult.ACCEPTED;
    }

    private static List<Workflow> parseWorkflows(List<String> lines) {
        return lines.stream().map(line -> {
            var workFlowAndRulesList = line.split("\\{");
            var rules = Arrays.stream(workFlowAndRulesList[1].replace("}", "").split(",")).map(s -> {
                if (s.contains(":")) {
                    var conditionAndTarget = s.split(":");
                    var splitCondition = conditionAndTarget[0].splitWithDelimiters("[<>]", -1);
                    var condition = new Condition(RangeType.of(splitCondition[0]), Operator.of(splitCondition[1].charAt(0)), Integer.parseInt(splitCondition[2]));
                    return new Rule(Optional.of(condition), conditionAndTarget[1]);
                }
                else {
                    return new Rule(Optional.empty(), s);
                }
            }).toList();
            return new Workflow(workFlowAndRulesList[0], rules);
        }).toList();
    }

    private static List<MachinePart> parseMachineParts(List<String> lines) {
        return lines.stream().map(line -> {
            var properties = line.replace("{", "")
                    .replace("}", "")
                    .split(",");
            return new MachinePart(
                    Integer.parseInt(properties[0].split("x=")[1]),
                    Integer.parseInt(properties[1].split("m=")[1]),
                    Integer.parseInt(properties[2].split("a=")[1]),
                    Integer.parseInt(properties[3].split("s=")[1]));
        }).toList();
    }
}
