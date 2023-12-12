package day5;

import fileUtils.FileReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Day5 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input5.csv");
        var seeds = parseLines(lines);
        var converted = convertSeeds(seeds);

        System.out.println("Seeds:            " + seeds);
        System.out.println("Converted values: " + converted);
        var lowestLocationNumber = converted.stream().min(Comparator.comparingLong(FoodValue::value)).orElseThrow();
        System.out.println("Lowest location number: " + lowestLocationNumber);

        //part 2
        var seedRanges = parseRanges(lines);
        var minRange = findLowestLocation(seedRanges);
        System.out.println();
        System.out.println("Minimal range: " + minRange + ", seed: " + minRange.start() + ", value: " + (minRange.start() + minRange.value()));

    }

    private static ConverterMap seedToSoilMap;
    private static ConverterMap soilToFertilizerMap;
    private static ConverterMap fertilizerToWaterMap;
    private static ConverterMap waterToLightMap;
    private static ConverterMap lightToTemperatureMap;
    private static ConverterMap temperatureToHumidityMap;
    private static ConverterMap humidityToLocationMap;

    private static List<FoodValue> parseLines(List<String> lines) {
        var splitLines = FileReader.getFileReader().splitLines(lines, String::isEmpty);
        var seeds = Arrays.stream(splitLines.get(0).get(0).split("seeds: ")[1].split(" "))
                .map(s -> new FoodValue(Long.parseLong(s))).toList();
        seedToSoilMap = parseSubLines(splitLines.get(1), "seed-to-soil map:");
        soilToFertilizerMap = parseSubLines(splitLines.get(2), "soil-to-fertilizer map:");
        fertilizerToWaterMap = parseSubLines(splitLines.get(3), "fertilizer-to-water map:");
        waterToLightMap = parseSubLines(splitLines.get(4), "water-to-light map:");
        lightToTemperatureMap = parseSubLines(splitLines.get(5), "light-to-temperature map:");
        temperatureToHumidityMap = parseSubLines(splitLines.get(6), "temperature-to-humidity map:");
        humidityToLocationMap = parseSubLines(splitLines.get(7), "humidity-to-location map:");
        return seeds;
    }

    private static List<SeedRange> parseRanges(List<String> lines) {
        var splitLines = FileReader.getFileReader().splitLines(lines, String::isEmpty);
        var numbers = Arrays.stream(splitLines.get(0).get(0).split("seeds: ")[1].split(" "))
                .map(Long::parseLong).toList();
        var seedRanges = new ArrayList<SeedRange>();
        int index = 0;
        while (index < numbers.size() -1) {
            seedRanges.add(new SeedRange(numbers.get(index), numbers.get(index) + numbers.get(index + 1) - 1, 0));
            index+=2;
        }
        return seedRanges;
    }

    private static ConverterMap parseSubLines(List<String> subLines, String expectedFirstLine) {
        assert subLines.get(0).equals(expectedFirstLine);
        return new ConverterMap(
                subLines.subList(1, subLines.size()).stream().map(line -> {
                    var numbers = Arrays.stream(line.split(" ")).map(Long::parseLong).toList();
                    return new Converter(numbers.get(0), numbers.get(1), numbers.get(2));
                }).sorted(Comparator.comparingLong(Converter::sourceStart)).toList()
        );
    }

    private static List<FoodValue> convertSeeds(List<FoodValue> seeds) {
        return seeds.stream().map(seed ->
                seed
                    .convert(seedToSoilMap)
                    .convert(soilToFertilizerMap)
                    .convert(fertilizerToWaterMap)
                    .convert(waterToLightMap)
                    .convert(lightToTemperatureMap)
                    .convert(temperatureToHumidityMap)
                    .convert(humidityToLocationMap)
        ).toList();
    }

    private static SeedRange findLowestLocation(List<SeedRange> seedRanges) {
        var nextRanges = seedRanges.stream()
                .map(seedRange -> getSubRanges(seedRange, seedToSoilMap)).flatMap(List::stream)
                .map(seedRange -> getSubRanges(seedRange, soilToFertilizerMap)).flatMap(List::stream)
                .map(seedRange -> getSubRanges(seedRange, fertilizerToWaterMap)).flatMap(List::stream)
                .map(seedRange -> getSubRanges(seedRange, waterToLightMap)).flatMap(List::stream)
                .map(seedRange -> getSubRanges(seedRange, lightToTemperatureMap)).flatMap(List::stream)
                .map(seedRange -> getSubRanges(seedRange, temperatureToHumidityMap)).flatMap(List::stream)
                .map(seedRange -> getSubRanges(seedRange, humidityToLocationMap)).flatMap(List::stream)
                .toList();
        return nextRanges.stream().min(Comparator.comparing(range -> range.start() + range.value())).orElseThrow();
    }

    private static List<SeedRange> getSubRanges(SeedRange seedRange, ConverterMap converterMap) {
        var result = new ArrayList<SeedRange>();
        var currentSeedRange = new SeedRange(seedRange.start(), seedRange.end(), seedRange.value());
        while (true) {
            var largestSubrange = getLargestSubrangeWithSingleMapping(currentSeedRange, converterMap);
            result.add(largestSubrange);
            if (largestSubrange.end() < currentSeedRange.end()) {
                currentSeedRange = new SeedRange(largestSubrange.end() + 1, currentSeedRange.end(), currentSeedRange.value());
            }
            else {
                break;
            }
        }
        return result;
    }

    private static SeedRange getLargestSubrangeWithSingleMapping(SeedRange seedRange, ConverterMap converterMap) {
        long mappedStart = seedRange.start() + seedRange.value();
        for (Converter converter : converterMap.converters()) {
            if (mappedStart < converter.sourceStart()) {
                return new SeedRange(seedRange.start(), Math.min(seedRange.end(), converter.sourceStart() - seedRange.value()), seedRange.value());
            } else if (mappedStart <= converter.getEnd()) {
                return new SeedRange(seedRange.start(), Math.min(seedRange.end(), converter.getEnd() - seedRange.value()), seedRange.value() + converter.getValue());
            }
        }
        return seedRange;
    }

}
