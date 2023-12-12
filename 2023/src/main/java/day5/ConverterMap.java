package day5;

import java.util.List;

public record ConverterMap(List<Converter> converters) {

    public Long convert(long number) {
            return converters.stream()
                .filter(c -> (number >= c.sourceStart()) && (number <= c.sourceStart() + c.rangeLength()  - 1))
                .findFirst()
                .map(c -> number + c.destinationStart() - c.sourceStart())
                .orElse(number);
    }
}
