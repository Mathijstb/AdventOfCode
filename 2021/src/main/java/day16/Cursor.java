package day16;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Cursor {

    private int value;

    public void move(int amount) {
        value -= amount;
    }
}
