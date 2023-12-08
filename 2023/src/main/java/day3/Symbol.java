package day3;

public record Symbol(Character character) {

    boolean isDigit() {
        return Character.isDigit(character);
    }

    boolean isSymbol() {
        return !isDigit() && !character.equals('.');
    }

}
