package day5;

public record Converter(long destinationStart, long sourceStart, long rangeLength) {

    public long getEnd() {
        return sourceStart + rangeLength - 1;
    }

    public long getValue() {
        return destinationStart - sourceStart;
    }
}
