package Model;

public class Cell {
  // Whether the board square is occupied by any piece.
  private boolean isOccupied;

  // Stored mainly for clarity/debugging.
  private final int column;
  private final int row;

  private int playerId;

  public Cell(int row, int column) {
    // Standard matrix convention: row first, then column.
    this.row = row;
    this.column = column;
    this.playerId = 0;
  }

  public boolean isOccupied() {
    return playerId != 0;
  }

  public int getPlayerId() {
    return playerId;
  }

  public void setOccupied(boolean occupied, int playerId) {
    if (occupied) {
      this.playerId = playerId;
    }
  }
}
