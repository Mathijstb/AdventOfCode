import fileUtils.FileReader;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class Day4 {

    @Data
    public static class Passport {
        String birthYear;
        String issueYear;
        String expirationYear;
        String height;
        String hairColor;
        String eyeColor;
        String passportId;
        String countryId;
    }

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input4.csv");

        List<Passport> passports = new ArrayList<>();

        Passport passport = new Passport();
        for (String line: lines) {
            if (line.isEmpty() || line.isBlank()) {
                passports.add(passport);
                passport = new Passport();
            }
            else {
                String birthYear = getValueOfField(line, "byr");
                String issueYear = getValueOfField(line, "iyr");
                String expirationYear = getValueOfField(line, "eyr");
                String height = getValueOfField(line, "hgt");
                String hairColor = getValueOfField(line, "hcl");
                String eyeColor = getValueOfField(line, "ecl");
                String passportId = getValueOfField(line, "pid");
                String countryId = getValueOfField(line, "cid");
                if (birthYear != null) passport.setBirthYear(birthYear);
                if (issueYear != null) passport.setIssueYear(issueYear);
                if (expirationYear != null) passport.setExpirationYear(expirationYear);
                if (height != null) passport.setHeight(height);
                if (hairColor != null) passport.setHairColor(hairColor);
                if (eyeColor != null) passport.setEyeColor(eyeColor);
                if (passportId != null) passport.setPassportId(passportId);
                if (countryId != null) passport.setCountryId(countryId);
            }
        }
        passports.add(passport);
        countNumberOfValidPassports(passports);
    }

    private static String getValueOfField(String line, String fieldName) {
       int index = line.indexOf(fieldName);
       if (index < 0) return null;

       int startIndex = line.indexOf(":", index) + 1;
       int endIndex = line.indexOf(" ", startIndex);
       if (endIndex < 0) {
           return line.substring(startIndex);
       }
       return line.substring(startIndex, endIndex);
    }

    private static void countNumberOfValidPassports(List<Passport> passports) {
        long numberValid = passports.stream().filter(passport ->
            validateBirthYear(passport.getBirthYear()) &&
            validateIssueYear(passport.getIssueYear()) &&
            validateExpirationYear(passport.getExpirationYear()) &&
            validateHeight(passport.getHeight()) &&
            validateHairColor(passport.getHairColor()) &&
            validateEyeColor(passport.getEyeColor()) &&
            validatePassportId(passport.getPassportId())
        ).count();
        System.out.println("Number of valid passports: " + numberValid);
    }

    private static boolean validateBirthYear(String birthYear) {
        if (birthYear == null) return false;
        if (birthYear.length() != 4) return false;
        int year = Integer.parseInt(birthYear);
        return year >= 1920 && year <= 2002;
    }

    private static boolean validateIssueYear(String issueYear) {
        if (issueYear == null) return false;
        if (issueYear.length() != 4) return false;
        int year = Integer.parseInt(issueYear);
        return year >= 2010 && year <= 2020;
    }

    private static boolean validateExpirationYear(String expirationYear) {
        if (expirationYear == null) return false;
        if (expirationYear.length() != 4) return false;
        int year = Integer.parseInt(expirationYear);
        return year >= 2020 && year <= 2030;
    }

    private static boolean validateHeight(String height) {
        if (height == null) return false;
        int indexCm = height.indexOf("cm");
        if (indexCm > 0) {
            int numberOfCm = Integer.parseInt(height.substring(0, indexCm));
            return numberOfCm >= 150 && numberOfCm <= 193;
        }
        int indexIn = height.indexOf("in");
        if (indexIn > 0) {
            int numberOfInch = Integer.parseInt(height.substring(0, indexIn));
            return numberOfInch >= 59 && numberOfInch <= 76;
        }
        return false;
    }

    private static boolean validateHairColor(String hairColor) {
        if (hairColor == null) return false;
        return Pattern.matches("#[0-9a-f]{6}", hairColor);
    }

    private static final List<String> eyeColors = Arrays.asList("amb", "blu", "brn", "gry", "grn", "hzl", "oth");

    private static boolean validateEyeColor(String eyeColor) {
        return eyeColors.contains(eyeColor);
    }

    private static boolean validatePassportId(String passportId) {
        if (passportId == null) return false;
        return Pattern.matches("[0-9]{9}", passportId);
    }
}
