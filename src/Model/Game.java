package Model;

import java.util.ArrayList;
import java.util.List;

public class Game {
  private Board board;
  private List<Player> players;
  private int currentPlayerId;

  public void applyMove(Move move) {
  };

  public List<Move> getLegalMoves() {

    // Заглушка
    return new ArrayList<Move>();

  };

  public boolean isGameOver() {
    return false;
  };
}
