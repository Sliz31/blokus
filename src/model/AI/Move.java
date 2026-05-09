package model.AI;

import java.util.Objects;

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

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (object == null || getClass() != object.getClass()) {
      return false;
    }
    Move other = (Move) object;
    return Objects.equals(piece, other.piece) && Objects.equals(position, other.position);
  }

  @Override
  public int hashCode() {
    return Objects.hash(piece, position);
  }
}
