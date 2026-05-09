package model.AI;

import model.Piece;
import model.Position;

// a single move: which piece to place and where
public class Move {
  private Piece piece;
  private Position position;

  public Move(Piece piece, Position position) {
    this.piece = piece;
    this.position = position;
  }

  public Piece getPiece() {
    return piece;
  }

  public Position getPosition() {
    return position;
  }
}
