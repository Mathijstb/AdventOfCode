package day17;

import fileUtils.FileReader;

import java.util.List;

public class Day17 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input17.csv");
        assert lines.size() == 1;
        var jetStream = readJetStream(lines.stream().findFirst().orElseThrow());
        List<Shape> shapes = List.of(ShapeType.createShape(ShapeType.HORIZONTAL),
                                     ShapeType.createShape(ShapeType.PLUS),
                                     ShapeType.createShape(ShapeType.DOWN_RIGHT),
                                     ShapeType.createShape(ShapeType.VERTICAL),
                                     ShapeType.createShape(ShapeType.BLOCK));
        dropRocks(shapes, jetStream);
    }

    private static JetStream readJetStream(String line) {
        return new JetStream(line.chars()
                .mapToObj(c -> switch (c) {
                    case '<' -> Direction.LEFT;
                    case '>' -> Direction.RIGHT;
                    default -> throw new IllegalArgumentException("Can not map direction");
                }).toList());
    }

    private static void dropRocks(List<Shape> rocks, JetStream jetStream) {
        //jetstream size 10091
        //shape size 5
        var game = new Game(rocks, jetStream);
        game.playRounds(2022);
        //game.playRounds(35);
        //game.playRounds(155);

        System.out.println("Tower height: " + game.getTowerHeight());
        getLargeHeight();
    }

    private static void getLargeHeight() {
        //Repetition starts at round 2010, of length 1695, with diff height 2634
        long result =  (1000_000_000_000L - 2010) % 1695 + 2010; //=2500
        //2500 rounds -> height 3884
        var height = ((1000_000_000_000L - result) / 1695L) * 2634 + 3884;
        System.out.println("Large height: " + height);
    }

}