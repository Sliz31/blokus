package Model;

public class Cell {
  // Stored mainly for clarity/debugging.
  private Position position;

  private int playerId;

  public Cell(Position position) {
    this.position = position;
    this.playerId = 0;
  }

  public Position getPosition() {
    return this.position;
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
