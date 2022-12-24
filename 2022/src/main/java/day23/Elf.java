package day23;

import java.awt.*;

public class Elf {

    public int id;

    public Point position;

    public int decisionIndex = 0;

    public Point proposal;

    public Elf(int id, Point position) {
        this.id = id;
        this.position = position;
        this.proposal = position;
    }

    public int getDecisionIndex() {
        return decisionIndex;
    }
}
