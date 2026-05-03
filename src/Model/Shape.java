package Model;

import java.util.Arrays;

public final class Shape {
  private final int[][] cells;

  public Shape(int[][] cells) {
    this.cells = deepCopy(cells);
  }

  public int rows() {
    return cells.length;
  }

  public int cols() {
    return cells[0].length;
  }

  public boolean cellAt(int row, int col) {
    return cells[row][col] == 1;
  }

  public int countCells() {
    int count = 0;
    for (int[] row : cells)
      for (int cell : row)
        if (cell == 1)
          count++;
    return count;
  }

  public Shape rotate() {
    int rows = cells.length;
    int cols = cells[0].length;
    int[][] result = new int[cols][rows];
    for (int r = 0; r < rows; r++)
      for (int c = 0; c < cols; c++)
        result[c][rows - 1 - r] = cells[r][c];
    return new Shape(result);
  }

  public Shape mirror() {
    int rows = cells.length;
    int cols = cells[0].length;
    int[][] result = new int[rows][cols];
    for (int r = 0; r < rows; r++)
      for (int c = 0; c < cols; c++)
        result[r][cols - 1 - c] = cells[r][c];
    return new Shape(result);
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Shape other))
      return false;
    if (rows() != other.rows() || cols() != other.cols())
      return false;
    for (int r = 0; r < rows(); r++)
      if (!Arrays.equals(cells[r], other.cells[r]))
        return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (int[] row : cells) {
      for (int cell : row)
        sb.append(cell == 1 ? "█" : "·");
      sb.append("\n");
    }
    return sb.toString();
  }

  private static int[][] deepCopy(int[][] src) {
    int[][] copy = new int[src.length][];
    for (int r = 0; r < src.length; r++)
      copy[r] = Arrays.copyOf(src[r], src[r].length);
    return copy;
  }

}