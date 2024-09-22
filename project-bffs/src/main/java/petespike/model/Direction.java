package petespike.model;

public enum Direction {
    UP(new int[]{-1, 0}),
    DOWN(new int[]{1, 0}),
    LEFT(new int[]{0, -1}),
    RIGHT(new int[]{0, 1});

    private final int[] movement;

    Direction(int[] movement) {
        this.movement = movement;
    }

    public int[] getMovement() {
        return this.movement;
    }
}
