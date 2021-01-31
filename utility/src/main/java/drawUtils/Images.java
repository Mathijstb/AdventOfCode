package drawUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Images {

    public static BufferedImage getImage(String fileName) {
        try {
            return ImageIO.read(Objects.requireNonNull(Images.class.getClassLoader().getResource(fileName), "Invalid filename: " + fileName));
        }
        catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
