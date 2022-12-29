package day7;

import fileUtils.FileReader;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day7 {

    public static void execute() {
        List<String> lists = FileReader.getFileReader().readFile("input7.csv");
        var ips = readIps(lists);

        //part a
        long numberTLS = findNumberOfIPsSupportingTLS(ips);
        System.out.println("Number of IP's that support TLS: " + numberTLS);

        //part b
        long numberSSL = findNumberOfIPsSupportingSSL(ips);
        System.out.println("Number of IP's that support SSL: " + numberSSL);
    }

    private static List<Ip> readIps(List<String> lists) {
        return lists.stream().map(list -> {
            List<String> superNetParts = new ArrayList<>();
            List<String> hyperNetParts = new ArrayList<>();
            Arrays.stream(list.split("\\[")).forEach(part -> {
                var parts = part.split("]");
                if (parts.length == 1) {
                    superNetParts.add(parts[0]);
                }
                else {
                    hyperNetParts.add(parts[0]);
                    superNetParts.add(parts[1]);
                }
            });
            return new Ip(superNetParts, hyperNetParts);
        }).toList();
    }

    private static long findNumberOfIPsSupportingTLS(List<Ip> ips) {
        return ips.stream().filter(Day7::supportsTLS).count();
    }

    private static long findNumberOfIPsSupportingSSL(List<Ip> ips) {
        return ips.stream().filter(Day7::supportsSSL).count();
    }

    private static boolean supportsTLS(Ip ip) {
        return ip.superNetParts().stream().anyMatch(Day7::containsABBA) &&
                ip.hyperNetParts().stream().noneMatch(Day7::containsABBA);
    }

    private static boolean containsABBA(String value) {
        var pattern = Pattern.compile("([a-z])((?!\\1)[a-z])\\2\\1");
        var matcher = pattern.matcher(value);
        return matcher.find();
    }

    private static boolean supportsSSL(Ip ip) {
        Set<String> abaSequences = new HashSet<>();
        ip.superNetParts().forEach(part -> abaSequences.addAll(findABASequences(part)));
        return abaSequences.stream().anyMatch(abaSequence -> {
            var a = abaSequence.charAt(0);
            var b = abaSequence.charAt(1);
            var babSequence = Stream.of(b, a, b).map(String::valueOf).collect(Collectors.joining());
            return ip.hyperNetParts().stream().anyMatch(part -> part.contains(babSequence));
        });
    }

    private static List<String> findABASequences(String value) {
        var pattern = Pattern.compile("([a-z])((?!\\1)[a-z])\\1");
        var matcher = pattern.matcher(value);
        List<String> result = new ArrayList<>();
        int i = 0;
        while(matcher.find(i)) { // set start index for "find"
            result.add(matcher.group());
            i = matcher.start() + 1; // update start index to start from beginning of last match + 1
        }
        return result;
    }

}