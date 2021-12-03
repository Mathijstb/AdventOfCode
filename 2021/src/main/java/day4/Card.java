package day4;

import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Value
public class Card {

    List<List<Integer>> grid;

    public List<Integer> getNumbers() {
        return grid.stream().flatMap(List::stream).collect(Collectors.toList());
    }

    public Optional<Integer> getOptionalWinningRound(List<Integer> draw) {
        List<List<Integer>> winningLines = getWinningLines(draw);
        return winningLines.size() == 0 ? Optional.empty() : Optional.of(getMinWinningRound(winningLines, draw));
    }

    private static int getMinWinningRound(List<List<Integer>> winningLines, List<Integer> draw) {
        return winningLines.stream().map(line -> getWinningRound(line, draw)).mapToInt(v -> v).min().orElseThrow();
    }

    private static int getWinningRound(List<Integer> winningLine, List<Integer> draw) {
        return winningLine.stream().map(draw::indexOf).mapToInt(v -> v).max().orElseThrow();
    }

    private List<List<Integer>> getWinningLines(List<Integer> draw) {
        List<List<Integer>> winningLines = new ArrayList<>();

        //check horizontal lines
        for (List<Integer> gridLine : grid) {
            if (draw.containsAll(gridLine)) {
                winningLines.add(gridLine);
            }
        }

        //check vertical lines
        for (int i = 0; i < grid.get(0).size(); i++) {
            int index = i;
            List<Integer> gridLine = grid.stream().map(line -> line.get(index)).collect(Collectors.toList());
            if (draw.containsAll(gridLine)) {
                winningLines.add(gridLine);
            }
        }

        return winningLines;
    }
}
