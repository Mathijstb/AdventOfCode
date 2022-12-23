package day22;

import java.util.HashMap;
import java.util.Map;

public class Cube {

    private final Map<Integer, CubeSide> cubeSides = new HashMap<>();

    public Cube() {
        for (int i = 1; i <= 6; i++) {
            cubeSides.put(i, new CubeSide(i));
        }
        cubeSides.values().forEach(cubeSide -> {
            var index = cubeSide.getIndex();
            //  1 2
            //  4
            //5 6
            //3
            switch (index) {
                case 1 -> {
                    for (Facing facing : Facing.values()) {
                        switch (facing) {
                            case LEFT -> cubeSide.cubeSideMap.put(facing, new CubeSideAndFacing(cubeSides.get(5), Facing.RIGHT));
                            case RIGHT -> cubeSide.cubeSideMap.put(facing, new CubeSideAndFacing(cubeSides.get(2), Facing.RIGHT));
                            case UP -> cubeSide.cubeSideMap.put(facing, new CubeSideAndFacing(cubeSides.get(3), Facing.RIGHT));
                            case DOWN -> cubeSide.cubeSideMap.put(facing, new CubeSideAndFacing(cubeSides.get(4), Facing.DOWN));
                        }
                    }
                }
                case 2 -> {
                    for (Facing facing : Facing.values()) {
                        switch (facing) {
                            case LEFT -> cubeSide.cubeSideMap.put(facing, new CubeSideAndFacing(cubeSides.get(1), Facing.LEFT));
                            case RIGHT -> cubeSide.cubeSideMap.put(facing, new CubeSideAndFacing(cubeSides.get(6), Facing.LEFT));
                            case UP -> cubeSide.cubeSideMap.put(facing, new CubeSideAndFacing(cubeSides.get(3), Facing.UP));
                            case DOWN -> cubeSide.cubeSideMap.put(facing, new CubeSideAndFacing(cubeSides.get(4), Facing.LEFT));
                        }
                    }
                }
                case 3 -> {
                    for (Facing facing : Facing.values()) {
                        switch (facing) {
                            case LEFT -> cubeSide.cubeSideMap.put(facing, new CubeSideAndFacing(cubeSides.get(1), Facing.DOWN));
                            case RIGHT -> cubeSide.cubeSideMap.put(facing, new CubeSideAndFacing(cubeSides.get(6), Facing.UP));
                            case UP -> cubeSide.cubeSideMap.put(facing, new CubeSideAndFacing(cubeSides.get(5), Facing.UP));
                            case DOWN -> cubeSide.cubeSideMap.put(facing, new CubeSideAndFacing(cubeSides.get(2), Facing.DOWN));
                        }
                    }
                }
                case 4 -> {
                    for (Facing facing : Facing.values()) {
                        switch (facing) {
                            case LEFT -> cubeSide.cubeSideMap.put(facing, new CubeSideAndFacing(cubeSides.get(5), Facing.DOWN));
                            case RIGHT -> cubeSide.cubeSideMap.put(facing, new CubeSideAndFacing(cubeSides.get(2), Facing.UP));
                            case UP -> cubeSide.cubeSideMap.put(facing, new CubeSideAndFacing(cubeSides.get(1), Facing.UP));
                            case DOWN -> cubeSide.cubeSideMap.put(facing, new CubeSideAndFacing(cubeSides.get(6), Facing.DOWN));
                        }
                    }
                }
                case 5 -> {
                    for (Facing facing : Facing.values()) {
                        switch (facing) {
                            case LEFT -> cubeSide.cubeSideMap.put(facing, new CubeSideAndFacing(cubeSides.get(1), Facing.RIGHT));
                            case RIGHT -> cubeSide.cubeSideMap.put(facing, new CubeSideAndFacing(cubeSides.get(6), Facing.RIGHT));
                            case UP -> cubeSide.cubeSideMap.put(facing, new CubeSideAndFacing(cubeSides.get(4), Facing.RIGHT));
                            case DOWN -> cubeSide.cubeSideMap.put(facing, new CubeSideAndFacing(cubeSides.get(3), Facing.DOWN));
                        }
                    }
                }
                case 6 -> {
                    for (Facing facing : Facing.values()) {
                        switch (facing) {
                            case LEFT -> cubeSide.cubeSideMap.put(facing, new CubeSideAndFacing(cubeSides.get(5), Facing.LEFT));
                            case RIGHT -> cubeSide.cubeSideMap.put(facing, new CubeSideAndFacing(cubeSides.get(2), Facing.LEFT));
                            case UP -> cubeSide.cubeSideMap.put(facing, new CubeSideAndFacing(cubeSides.get(4), Facing.UP));
                            case DOWN -> cubeSide.cubeSideMap.put(facing, new CubeSideAndFacing(cubeSides.get(3), Facing.LEFT));
                        }
                    }
                }
            }
        });
    }

    public int getOppositeSideIndex(int sideIndex, Facing facing) {
        return cubeSides.get(sideIndex).cubeSideMap.get(facing).cubeSide().getIndex();
    }

    public Facing getOppositeFacing(int sideIndex, Facing facing) {
        return cubeSides.get(sideIndex).cubeSideMap.get(facing).facing();
    }
}
