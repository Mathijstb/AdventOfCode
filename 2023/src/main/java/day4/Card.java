package day4;

import java.util.List;

public record Card(int id, List<Integer> winningNumbers, List<Integer> ownedNumbers) {
}
