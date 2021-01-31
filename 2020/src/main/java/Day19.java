import fileUtils.FileReader;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

public class Day19 {

    @Data
    @AllArgsConstructor
    private static class Rule {
        int ruleNumber;
        String pattern;
    }

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input19b.csv");
        Map<Integer, Rule> rules = getRules(lines);
        Map<Integer, Rule> improvedRules = improveRules(rules);
        List<String> messages = getMessages(lines);
        Rule rule0 = rules.get(0);
        long numberOfMatches = messages.stream().filter(message -> Pattern.matches(rule0.pattern, message)).count();
        System.out.println("number of matches: " + numberOfMatches);
    }

    private static Map<Integer, Rule> getRules(List<String> lines) {
        Map<Integer, Rule> ruleMap = new HashMap<>();
        for (String line : lines) {
            if (line.isEmpty()) break;
            String[] numberAndPattern = line.split(": ");
            int ruleNumber = Integer.parseInt(numberAndPattern[0]);
            ruleMap.put(ruleNumber, new Rule(ruleNumber, numberAndPattern[1]));
        }
        return ruleMap;
    }

    private static List<String> getMessages(List<String> lines) {
        int emptyLineIndex = -1;
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).isEmpty()) {
                emptyLineIndex = i;
                break;
            }
        }
        return lines.subList(emptyLineIndex + 1, lines.size());
    }

    private static Map<Integer, Rule> improveRules(Map<Integer, Rule> rules) {
        AtomicBoolean ruleImproved = new AtomicBoolean(true);
        while (ruleImproved.get()) {
            ruleImproved.set(false);
            rules.values().forEach(rule -> {
                if (Pattern.matches("\\(*(\".\")\\)*([ |(]+\".\"\\)*)*", rule.pattern)) {
                    int ruleNumber = rule.ruleNumber;
                    rules.values().forEach(otherRule -> {
                        String newPattern = rule.pattern.contains("|") ? "(" + rule.pattern + ")" : rule.pattern;
                        String replacement = otherRule.pattern.replaceAll("\\b" + ruleNumber + "\\b", newPattern);
                        if (!replacement.equals(otherRule.pattern)){
                            otherRule.pattern = replacement;
                            ruleImproved.set(true);
                        }
                    });
                }
            });
        }
        for (int i = 0; i < 4; i++) {
            rules.get(8).pattern = rules.get(8).pattern.replaceAll("\\b" + 8 + "\\b", "(" + rules.get(8).pattern + ")");
            rules.get(11).pattern = rules.get(11).pattern.replaceAll("\\b" + 11 + "\\b", "(" + rules.get(11).pattern + ")");
        }
        rules.get(0).pattern = rules.get(0).pattern.replaceAll("\\b" + 8 + "\\b", "(" + rules.get(8).pattern + ")");
        rules.get(0).pattern = rules.get(0).pattern.replaceAll("\\b" + 11 + "\\b", "(" + rules.get(11).pattern + ")");

        rules.values().forEach(rule -> rule.pattern = rule.pattern.replaceAll("\"", "").replaceAll(" ", ""));
        return rules;
    }
}
