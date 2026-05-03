package Model;

public class Move {
  public final Player player;
  public final Piece piece;
  public final Position position;

  public Move(Player player, Piece piece, Position position) {
    this.player = player;
    this.piece = piece;
    this.position = position;
  }
}
