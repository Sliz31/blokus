package Logic.AI;

import Logic.Piece;

public class Move {
  private Piece piece;
  private int row;
  private int col;

  // We pass the fully rotated/flipped piece in here.
  public Move(Piece piece, int row, int col) {
    this.piece = piece;
    this.row = row;
    this.col = col;
  }

  public Piece getPiece() {
    return piece;
  }

  public int getRow() {
    return row;
  }

  public int getCol() {
    return col;
  }
}
