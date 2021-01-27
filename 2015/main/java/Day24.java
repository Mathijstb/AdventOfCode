import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Day24 {

    @Data
    @AllArgsConstructor
    private static class Balance {
        Integer[] weights1;
        Integer[] weights2;
        Integer[] weights3;
        Integer[] weights4;
        int sum1;
        int sum2;
        int sum3;
        int sum4;

        private int getSize() {
            return Math.min(Math.min(Math.min(weights1.length, weights2.length), weights3.length), weights4.length);
        }

        private List<Integer[]> getSortedWeights() {
            List<Integer[]> sortedWeights = new ArrayList<>(List.of(weights1, weights2, weights3, weights4));
            sortedWeights.sort(Comparator.comparing(weights -> weights.length));
            return sortedWeights;
        }

        private long getQuantumEntanglement() {
            List<Integer[]> sortedWeights = getSortedWeights();
            Integer[] weights = sortedWeights.get(0);
            long product = 1;
            for (Integer weight : weights) {
                product *= weight;
            }
            return product;
        }
    }

    private static Integer[] weights;

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input24.csv");
        readWeights(lines);
        int totalWeight = 0;
        for (Integer weight : weights) {
            totalWeight += weight;
        }
        maxWeight = totalWeight / 4;
        //Total 1548
        //Total per package: 516

        System.out.println("Sum of weights: " + totalWeight);
        System.out.println("Weight per compartment: " + maxWeight);
        findBalancedWeights(weights);
        List<Balance> balances = getBalanceWithSmallesQuantumEntanglement();
    }

    private static List<Balance> getBalanceWithSmallesQuantumEntanglement() {
        long minQuantum = balancedWeights.stream().min(Comparator.comparing(Balance::getQuantumEntanglement)).orElseThrow().getQuantumEntanglement();
        return balancedWeights.stream().filter(balance -> balance.getQuantumEntanglement() == minQuantum).collect(Collectors.toList());
    }

    private static void findBalancedWeights(Integer[] weights) {
        addNextWeight(new Balance(new Integer[]{}, new Integer[]{}, new Integer[]{}, new Integer[]{}, 0, 0, 0, 0), weights);
    }

    private static int maxWeight;
    private static int minSize = 999;
    private static List<Balance> balancedWeights = new ArrayList<>();

    private static void addNextWeight(Balance balance, Integer[] remainingWeights) {
        if (balance.sum1 <= maxWeight && balance.sum2 <= maxWeight && balance.sum3 <= maxWeight && balance.sum4 <= maxWeight) {
            if (balance.getSize() <= minSize) {
                if (remainingWeights.length == 0) {
                    int size = balance.getSize();
                    if (size <= minSize) {
                        if (size < minSize)
                            balancedWeights.clear();
                        minSize = size;
                        balancedWeights.add(balance);
                    }
                } else {
                    int newWeight = remainingWeights[remainingWeights.length - 1];
                    Integer[] newRemainingWeights = ArrayUtils.remove(remainingWeights, remainingWeights.length - 1);

                    Balance newBalance1 = new Balance(ArrayUtils.add(balance.weights1, newWeight), balance.weights2, balance.weights3, balance.weights4,
                            balance.sum1 + newWeight, balance.sum2, balance.sum3, balance.sum4);
                    addNextWeight(newBalance1, newRemainingWeights);

                    Balance newBalance2 = new Balance(balance.weights1, ArrayUtils.add(balance.weights2, newWeight), balance.weights3, balance.weights4,
                            balance.sum1, balance.sum2 + newWeight, balance.sum3, balance.sum4);
                    addNextWeight(newBalance2, newRemainingWeights);

                    Balance newBalance3 = new Balance(balance.weights1, balance.weights2, ArrayUtils.add(balance.weights3, newWeight), balance.weights4,
                            balance.sum1, balance.sum2, balance.sum3 + newWeight, balance.sum4);
                    addNextWeight(newBalance3, newRemainingWeights);

                    Balance newBalance4 = new Balance(balance.weights1, balance.weights2, balance.weights3, ArrayUtils.add(balance.weights4, newWeight),
                            balance.sum1, balance.sum2, balance.sum3, balance.sum4 + newWeight);
                    addNextWeight(newBalance4, newRemainingWeights);
                }
            }
        }
    }

    public static void readWeights(List<String> lines) {
        List<Integer> list = lines.stream().map(Integer::parseInt).collect(Collectors.toList());
        weights = new Integer[list.size()];
        list.toArray(weights);
    }

}
