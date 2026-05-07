package model;

// stores the grid of a piece (which cells are filled)
// this object never changes after creation
public final class Shape {

    private final int[][] cells;

    // deep copy so nobody can change the grid from outside
    public Shape(int[][] source) {
        int rowCount = source.length;
        int colCount = source[0].length;
        cells = new int[rowCount][colCount];
        for (int row = 0; row < rowCount; row++) {
            System.arraycopy(source[row], 0, cells[row], 0, colCount);
        }
    }

    public int rows() { return cells.length; }
    public int cols() { return cells[0].length; }
    public int cellAt(int row, int column) { return cells[row][column]; }

    // counts how many cells are filled (== 1)
    public int countCells() {
        int total = 0;
        for (int[] row : cells)
            for (int value : row)
                if (value == 1) total++;
        return total;
    }

    // returns a new Shape rotated 90 degrees clockwise
    public Shape rotateRight() {
        int rowCount = cells.length;
        int colCount = cells[0].length;
        int[][] rotatedShape = new int[colCount][rowCount];
        for (int row = 0; row < rowCount; row++)
            for (int column = 0; column < colCount; column++)
                rotatedShape[column][rowCount - 1 - row] = cells[row][column];
        return new Shape(rotatedShape);
    }

    // returns a new Shape mirrored left-to-right
    public Shape flip() {
        int rowCount = cells.length;
        int colCount = cells[0].length;
        int[][] flippedShape = new int[rowCount][colCount];
        for (int row = 0; row < rowCount; row++)
            for (int column = 0; column < colCount; column++)
                flippedShape[row][colCount - 1 - column] = cells[row][column];
        return new Shape(flippedShape);
    }

    // defensive copy for callers that need a raw grid
    public int[][] toCells() {
        int[][] copy = new int[cells.length][];
        for (int index = 0; index < cells.length; index++)
            copy[index] = cells[index].clone();
        return copy;
    }
}
