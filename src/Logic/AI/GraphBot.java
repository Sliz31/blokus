package Logic.AI;

import Logic.Board;
import Logic.Player;
import Logic.Piece;
import java.util.List;

public class GraphBot extends Player {
  private BotState currentState;
  private GraphAnalyzer analyzer;

  public GraphBot(int id, String name) {
    super(id, name);
    this.currentState = new ExpansionState();
    this.analyzer = new GraphAnalyzer();
  }

  public BotState getCurrentState() {
    return currentState;
  }

  public void makeMove(Board board, Player enemy) {
    // 1. Transition state
    BotState next = currentState.nextState(board, this, enemy, analyzer);
    if (next != null && next != currentState) {
      System.out.println("AI transitioning from " + currentState.getClass().getSimpleName() + " to "
          + next.getClass().getSimpleName());
      currentState = next;
    }

    // 2. Decide move
    Move move = currentState.decideMove(board, this, enemy, analyzer);

    // 3. Apply move
    if (move != null) {
      System.out.println(
          "AI played Piece ID " + move.getPiece().getId() + " at (" + move.getRow() + ", " + move.getCol() + ")");
      board.placePiece(move.getPiece(), move.getRow(), move.getCol(), this.getId());
      // Mark piece as used in inventory
      for (Piece p : getInventory()) {
        if (p.getId() == move.getPiece().getId()) {
          p.setUsed(true);
          break;
        }
      }
      if (isFirstMove()) {
        setFirstMove(false);
      }
    } else {
      System.out.println("AI passes.");
    }
  }

  public static List<Move> getAllLegalMoves(Board board, Player player) {
    java.util.List<Move> validMoves = new java.util.ArrayList<>();
    List<Piece> available = player.getAvailablePieces();

    for (Piece basePiece : available) {
      // Need to test distinct geometries to save time, but brute forcing 4 rots x 2
      // flips is fine for 14x14
      for (int flip = 0; flip < 2; flip++) {
        for (int rot = 0; rot < 4; rot++) {
          Piece p = new Piece(basePiece.getId(), basePiece.getShape());
          if (flip == 1)
            p.flip();
          for (int i = 0; i < rot; i++)
            p.rotate();

          for (int r = 0; r < board.getSize(); r++) {
            for (int c = 0; c < board.getSize(); c++) {
              if (board.isValidMove(p, r, c, player)) {
                validMoves.add(new Move(p, r, c));
              }
            }
          }
        }
      }
    }
    return validMoves;
  }
}
