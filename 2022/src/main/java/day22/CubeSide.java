package day22;

import java.util.HashMap;
import java.util.Map;

public class CubeSide {

    private final int index;

    public CubeSide(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public final Map<Facing, CubeSideAndFacing> cubeSideMap = new HashMap<>();
}
