package model.AI;

import model.Board;
import model.Player;
import model.Piece;
import java.util.List;

// the AI player - uses a state machine to switch between expansion, cut, and fill strategies
// GraphBot extends Player so it sits in the inventory just like a human player
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

  // computes the best move according to the current strategy - does NOT apply it
  // to the board
  // call this off the EDT, then apply the returned Move on the EDT
  public Move computeMove(Board board, Player enemy) {
    // check if strategy should change
    BotState nextState = currentState.nextState(board, this, enemy, analyzer);
    if (nextState != null && nextState != currentState) {
      currentState = nextState;
    }
    // ask the current strategy to pick a move
    return currentState.decideMove(board, this, enemy, analyzer);
  }

  // generates every legal move for a player by trying all 8 variants of each
  // piece
  public static List<Move> getAllLegalMoves(Board board, Player player) {
    java.util.List<Move> validMoves = new java.util.ArrayList<>();
    List<Piece> availablePieces = player.getAvailablePieces();

    for (Piece basePiece : availablePieces) {
      for (int variant = 0; variant < 8; variant++) {
        Piece currentPiece = new Piece(basePiece.getId(), variant);
        for (int row = 0; row < board.getSize(); row++) {
          for (int column = 0; column < board.getSize(); column++) {
            model.Position pos = new model.Position(row, column);
            if (board.isValidMove(currentPiece, pos, player)) {
              validMoves.add(new Move(currentPiece, pos));
            }
          }
        }
      }
    }
    return validMoves;
  }
}
