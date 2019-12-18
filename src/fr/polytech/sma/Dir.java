package fr.polytech.sma;

public enum  Dir {

    RIGHT(0, 1),
    LEFT(0, -1),
    TOP(-1, 0),
    BOT(1, 0);

    private int moveX;
    private int moveY;

    private Dir(int moveX, int moveY) {
        this.moveX = moveX;
        this.moveY = moveY;
    }

    public int getMoveX() {
        return moveX;
    }

    public int getMoveY() {
        return moveY;
    }

    public static Dir getOpposite(Dir dir) {
        if (dir == Dir.LEFT || dir == Dir.RIGHT) {
            return Dir.TOP;
        } else {
            return Dir.RIGHT;
        }
    }
}
