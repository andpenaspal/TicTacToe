package tttHttp.models;

public class Point {
    private int moveCol;
    private int moveRow;

    public Point() {
    }

    public Point(int moveCol, int moveRow){
        this.moveCol = moveCol;
        this.moveRow = moveRow;
    }

    public int getMoveCol() {
        return moveCol;
    }

    public void setMoveCol(int moveCol) {
        this.moveCol = moveCol;
    }

    public int getMoveRow() {
        return moveRow;
    }

    public void setMoveRow(int moveRow) {
        this.moveRow = moveRow;
    }
}
