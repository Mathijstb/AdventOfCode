package fileUtils;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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

    public static FileReader getFileReader() {
        return new FileReader();
    }
}
