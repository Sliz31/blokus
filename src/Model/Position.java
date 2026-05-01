package Model;

public class Position {
  private final int row;
  private final int column;

  public Position(int row, int column) {
    this.row = row;
    this.column = column;
  }

  public Position(int coords[]) {
    this.row = coords[0];
    this.column = coords[1];
  }

  public int row() {
    return row;
  }

  public int col() {
    return column;
  }

  @Override
  public String toString() {
    return "[" + row() + ", " + col() + "]";
  }
}