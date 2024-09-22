package petespike.model;

public class Position {
    private int row;
    private int col;
    
    public Position (int row, int col){
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Position) {
            Position other = (Position) obj;
            return this.row == other.row && this.col == other.col;
        }
        return false;
    }

    @Override
    public String toString() {
        return "(" + row + ", " + col + ")";
    }
}
