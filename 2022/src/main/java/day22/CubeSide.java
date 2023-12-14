package day22;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class CubeSide {

    private final int index;

    public CubeSide(int index) {
        this.index = index;
    }

    public final Map<Facing, CubeSideAndFacing> cubeSideMap = new HashMap<>();
}
