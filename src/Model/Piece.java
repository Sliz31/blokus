package Model;

public class Piece {
  private final int id;
  private int[][] shape;
  private boolean isUsed;

  // All 21 standard Blokus pieces.
  // 1 means the square belongs to the polyomino, 0 means empty padding.
  public static final int[][][] SHAPES = {
    { { 1 } }, // 1: I1
    { { 1, 1 } }, // 2: I2
    { { 1, 1, 1 } }, // 3: I3
    { { 1, 1 }, { 1, 0 } }, // 4: V3
    { { 1, 1, 1, 1 } }, // 5: I4
    { { 1, 0, 0 }, { 1, 1, 1 } }, // 6: L4
    { { 1, 1, 1 }, { 0, 1, 0 } }, // 7: T4
    { { 1, 1 }, { 1, 1 } }, // 8: O4
    { { 1, 1, 0 }, { 0, 1, 1 } }, // 9: Z4
    { { 0, 1, 1 }, { 1, 1, 0 }, { 0, 1, 0 } }, // 10: F5
    { { 0, 1, 0 }, { 1, 1, 1 }, { 0, 1, 0 } }, // 11: X5
    { { 1, 1 }, { 1, 1 }, { 1, 0 } }, // 12: P5
    { { 1, 0, 0 }, { 1, 1, 0 }, { 0, 1, 1 } }, // 13: W5
    { { 1, 1, 0 }, { 0, 1, 0 }, { 0, 1, 1 } }, // 14: Z5
    { { 0, 1, 0, 0 }, { 1, 1, 1, 1 } }, // 15: Y5
    { { 1, 0, 0, 0 }, { 1, 1, 1, 1 } }, // 16: L5
    { { 1, 0, 1 }, { 1, 1, 1 } }, // 17: U5
    { { 1, 1, 1 }, { 0, 1, 0 }, { 0, 1, 0 } }, // 18: T5
    { { 1, 1, 1 }, { 1, 0, 0 }, { 1, 0, 0 } }, // 19: V5
    { { 1, 1, 0, 0 }, { 0, 1, 1, 1 } }, // 20: N5
    { { 1, 1, 1, 1, 1 } } // 21: I5
  };

  public Piece(int id) {
    this.id = id;

    int rows = Piece.SHAPES[id].length;
    int[][] newShape = new int[rows][];
    System.arraycopy(Piece.SHAPES[id], 0, newShape, 0, Piece.SHAPES[id].length);

    this.shape = newShape;
    this.isUsed = false;
  }

  public int getId() {
    return id;
  }

  public int[][] getShape() {
    return shape;
  }

  public boolean isUsed() {
    return isUsed;
  }

  public void setUsed(boolean used) {
    this.isUsed = used;
  }

  public int getSize() {
    int count = 0;
    for (int[] row : shape) {
      for (int cell : row) {
        if (cell == 1)
          count++;
      }
    }
    return count;
  }

  public int getRows() {
    return shape.length;
  }

  public int getColumns() {
    return shape[0].length;
  }

  // Rotates the piece 90 degrees clockwise by building a new matrix.
  public void rotate() {
    int rows = shape.length;
    int cols = shape[0].length;
    int[][] rotated = new int[cols][rows];

    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        rotated[c][rows - 1 - r] = shape[r][c];
      }
    }
    shape = rotated;
  }

  // Mirrors the piece horizontally by reversing every row.
  public void flip() {
    int rows = shape.length;
    int cols = shape[0].length;
    int[][] flipped = new int[rows][cols];

    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        flipped[r][cols - 1 - c] = shape[r][c];
      }
    }
    shape = flipped;
  }
}



