package day22;

public class Node {

    private final int size;

    private int used;

    public Node(int size, int used) {
        this.size = size;
        this.used = used;
    }

    public int getSize() {
        return size;
    }

    public int getUsed() {
        return used;
    }

    public int getAvailable() {
        return getSize() - getUsed();
    }

    public int removeUsed() {
        int usedSize = used;
        this.used = 0;
        return usedSize;
    }

    public void addUsed(int used) {
        assert (used <= getAvailable());
        this.used += used;
    }
}
