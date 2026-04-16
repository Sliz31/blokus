package Model;

public class Board {
  private final int SIZE = 14;

  // Each cell stores occupancy and owner information.
  private final Cell[][] grid;

  public Board() {
    grid = new Cell[SIZE][SIZE];
    for (int row = 0; row < SIZE; row++) {
      for (int column = 0; column < SIZE; column++) {
        grid[row][column] = new Cell(row, column);
      }
    }
  }

  public int getSize() {
    return SIZE;
  }

  public Cell[][] getGrid() {
    return grid;
  }
}
