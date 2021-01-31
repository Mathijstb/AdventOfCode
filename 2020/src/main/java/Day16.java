import fileUtils.FileReader;
import lombok.Value;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day16 {

    @Value
    private static class Constraint {
        int minValue;
        int maxValue;
    }

    @Value
    private static class Rule {
        String name;
        List<Constraint> constraints;
    }

    @Value
    private static class Ticket {
        List<Integer> values;
    }

    private static List<Rule> rules;
    private static Ticket yourTicket;
    private static List<Ticket> nearbyTickets;

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input16.csv");
        rules = getRules(lines);
        yourTicket = getYourTicket(lines);
        nearbyTickets = getNearbyTickets(lines);

        List<Ticket> validTickets = findValidTickets();
        validTickets.add(yourTicket);
        Map<Integer, Set<Rule>> fieldToRulesMap = findFieldToRuleMap(validTickets);
        Map<Integer, Rule> fieldToRuleMap = eliminateRules(fieldToRulesMap);
        findMultiplicationOfDepartureFields(fieldToRuleMap);
    }

    private static void findMultiplicationOfDepartureFields(Map<Integer, Rule> fieldToRuleMap) {
        List<Integer> values = yourTicket.values;

        List<Integer> departureIndices = IntStream.range(0, values.size())
                .filter(index -> fieldToRuleMap.get(index).name.contains("departure")).boxed().collect(Collectors.toList());
        long mult = 1;

        fieldToRuleMap.forEach((index, rule) -> {
            System.out.println(fieldToRuleMap.get(index).name + ": " + values.get(index));
        });

        System.out.println();
        for (int index : departureIndices) {
            mult *= values.get(index);
            System.out.println(fieldToRuleMap.get(index).name + ": " + values.get(index));
        }
        System.out.println("Multiplication: " + mult);
    }

    private static Map<Integer, Rule> eliminateRules(Map<Integer, Set<Rule>> fieldToRuleMap) {
        while(true) {
            boolean mapUpdated = false;
            for (int i = 0; i < fieldToRuleMap.size(); i++) {
                Set<Rule> rules = fieldToRuleMap.get(i);
                if (rules.size() == 1) {
                    Rule rule = rules.stream().findFirst().orElseThrow();
                    for (int j = 0; j < fieldToRuleMap.size(); j++) {
                        if (i != j && fieldToRuleMap.get(j).contains(rule)) {
                            fieldToRuleMap.get(j).remove(rule);
                            mapUpdated = true;
                        }
                    }
                }
            }
            if (!mapUpdated) break;
        }
        Map<Integer, Rule> result = new HashMap<>();
        fieldToRuleMap.forEach((index, rules) -> result.put(index, rules.stream().findFirst().orElseThrow()));
        return result;
    }

    private static Map<Integer, Set<Rule>> findFieldToRuleMap(List<Ticket> validTickets) {
        Map<Integer, Set<Rule>> fieldToRulesMap = new HashMap<>();
        for (int i = 0; i < rules.size(); i++) {
            fieldToRulesMap.put(i, new HashSet<>(rules));
        }
        validTickets.forEach(ticket -> {
            List<Integer> values = ticket.values;
            IntStream.range(0, values.size()).forEach(i -> {
                int value = values.get(i);
                rules.forEach(rule -> {
                    if (!validValue(rule, value)) {
                        fieldToRulesMap.get(i).remove(rule);
                    }
                });
            });
        });
        return fieldToRulesMap;
    }

    private static boolean validValue(Rule rule, int value) {
        return rule.constraints.stream().anyMatch(constraint -> constraint.minValue <= value && constraint.maxValue >= value);
    }

    private static List<Ticket> findValidTickets() {
        return nearbyTickets.stream().filter(ticket -> ticket.values.stream()
                .allMatch(value -> rules.stream()
                        .anyMatch(rule -> validValue(rule, value))
                )
        ).collect(Collectors.toList());
    }

    private static void findInvalidValues() {
        List<Integer> invalidValues = nearbyTickets.stream().flatMap(ticket -> ticket.values.stream()
                .filter(value -> rules.stream()
                        .noneMatch(rule -> rule.constraints.stream()
                                .anyMatch(constraint -> constraint.minValue <= value && constraint.maxValue >= value)
                        )
                )
        ).collect(Collectors.toList());
        int sum = invalidValues.stream().reduce(0, Integer::sum);
        System.out.println("Sum of invalid values: " + sum);
    }

    private static List<Rule> getRules(List<String> lines) {
        List<Rule> rules = new ArrayList<>();
        for (String line : lines) {
            if (line.isEmpty()) break;

            String[] nameAndConstraintsSplit = line.split(": ");
            List<Constraint> constraints = Arrays.stream(nameAndConstraintsSplit[1].split(" or ")).map(c -> {
                String[] splitConstraints = c.split("-");
                return new Constraint(Integer.parseInt(splitConstraints[0]), Integer.parseInt(splitConstraints[1]));
            }).collect(Collectors.toList());
            rules.add(new Rule(nameAndConstraintsSplit[0], constraints));
        }
        return rules;
    }

    private static Ticket getYourTicket(List<String> lines) {
        int yourTicketIndex = IntStream.range(0, lines.size()).filter(i -> lines.get(i).contains("your ticket:")).findFirst().orElseThrow() + 1;
        return new Ticket(Arrays.stream(lines.get(yourTicketIndex).split(",")).map(Integer::parseInt).collect(Collectors.toList()));
    }

    private static List<Ticket> getNearbyTickets(List<String> lines) {
        int nearbyTicketsIndex = IntStream.range(0, lines.size()).filter(i -> lines.get(i).contains("nearby tickets:")).findFirst().orElseThrow() + 1;
        List<String> nearbyTicketLines = IntStream.range(nearbyTicketsIndex, lines.size()).mapToObj(lines::get).collect(Collectors.toList());
        return nearbyTicketLines.stream().map(line -> new Ticket(Arrays.stream(line.split(","))
                .map(Integer::parseInt).collect(Collectors.toList())))
                .collect(Collectors.toList());
    }
}
