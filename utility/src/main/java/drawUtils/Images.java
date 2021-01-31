package drawUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Images {

    private static final Map<String, BufferedImage> imageMap = new HashMap<>();

    public static BufferedImage getImage(String fileName) {
        try {
            if (!imageMap.containsKey(fileName)) {
                imageMap.put(fileName, ImageIO.read(Objects.requireNonNull(Images.class.getClassLoader().getResource(fileName), "Invalid filename: " + fileName)));
            }
            return imageMap.get(fileName);
        }
        catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
