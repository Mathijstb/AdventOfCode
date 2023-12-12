package fileUtils;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class FileReader {

    public List<String> readFile(String filename) {
        URL url = ClassLoaderUtil.getResource(filename, FileReader.class);
        try {
            Path path = Paths.get(url.toURI());
            return Files.readAllLines(path);
        }
        catch (Exception e) {
            throw new RuntimeException("read error");
        }
    }

    public List<List<String>> splitLines(List<String> lines, Predicate<String> separatorPredicate) {
        var result = new ArrayList<List<String>>();
        var subList = new ArrayList<String>();
        for (String line : lines) {
            if (separatorPredicate.test(line)) {
                result.add(subList);
                subList = new ArrayList<>();
            } else {
                subList.add(line);
            }
        }
        if (!subList.isEmpty()) result.add(subList);
        return result;
    }

    public static FileReader getFileReader() {
        return new FileReader();
    }
}
