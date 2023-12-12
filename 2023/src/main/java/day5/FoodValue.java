package day5;

public record FoodValue(long value) {

    public FoodValue convert(ConverterMap converterMap) {
        return new FoodValue(converterMap.convert(this.value));
    }
}
