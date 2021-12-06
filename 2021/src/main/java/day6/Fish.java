package day6;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Optional;

@Data
@AllArgsConstructor
public class Fish {

    int creationTimer;

    public Optional<Fish> cycle() {
        if (creationTimer == 0) {
            creationTimer = 6;
            return Optional.of(new Fish(8));
        }
        else {
            creationTimer -= 1;
            return Optional.empty();
        }
    }
}
