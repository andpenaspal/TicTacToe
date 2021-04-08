package tttHttp.models;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return moveCol == point.moveCol && moveRow == point.moveRow;
    }

    @Override
    public int hashCode() {
        return Objects.hash(moveCol, moveRow);
    }

    @Override
    public String toString() {
        return "Point{" +
                "moveCol=" + moveCol +
                ", moveRow=" + moveRow +
                '}';
    }
}
