package day7;

import fileUtils.FileReader;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class Day7 {

    public static void execute() {
        List<String> lines = FileReader.getFileReader().readFile("input7.csv");
        parseCommands(lines);
        System.out.println("Size of root directory: " + Directory.getRoot().getTotalSize());
        System.out.println("Size of root diectory with max size 100000: " + Directory.getRoot().getTotalSizeWithMaximum(100000));
        var directoryToBeDeleted = determineSizeOfDirectoryToBeDeleted();
        System.out.println("Size of directory to be deleted: " + directoryToBeDeleted.getTotalSize());
    }

    private static void parseCommands(List<String> lines) {
        Directory currentDir = Directory.getRoot();
        for (String line : lines) {
            if (line.startsWith("$")) {
                var command = readCommand(line);
                switch (command.type) {
                    case ROOT -> currentDir = Directory.getRoot();
                    case OUT -> currentDir = currentDir.getParent();
                    case IN -> currentDir = currentDir.getSubDirectory(command.parameter.orElseThrow());
                }
            } else {
                addContents(line, currentDir);
            }
        }
    }

    private static Command readCommand(String line) {
        var command = line.split("\\$ ")[1];
        if (command.startsWith("ls")) {
            return new Command(CommandType.LIST, Optional.empty());
        }
        else if (command.startsWith("cd ")) {
            var parameter = line.split("cd ")[1];
            return switch (parameter) {
                case "/" -> new Command(CommandType.ROOT, Optional.empty());
                case ".." -> new Command(CommandType.OUT, Optional.empty());
                default -> new Command(CommandType.IN, Optional.of(parameter));
            };
        }
        throw new IllegalArgumentException("Can not parse command");
    }

    private static void addContents(String line, Directory directory) {
        if (line.startsWith("dir")) {
            var dirName = line.split("dir ")[1];
            directory.addSubDirectory(dirName);
        }
        else {
            var sizeAndName = line.split(" ");
            var name = sizeAndName[1];
            var size = Long.parseLong(sizeAndName[0]);
            directory.addFile(name, size);
        }
    }

    private static Directory determineSizeOfDirectoryToBeDeleted() {
        var totalSpace = 70_000_000;
        var requiredFreeSpace = 30_000_000;
        var takenSpace = Directory.getRoot().getTotalSize();
        var extraSpaceNeeded = requiredFreeSpace - (totalSpace - takenSpace);
        return Directory.getRoot().getDirectoriesWithMinimumSize(extraSpaceNeeded).stream()
                .min(Comparator.comparingLong(Directory::getTotalSize)).orElseThrow();

    }
}
