package model.AI;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.Board;
import model.Piece;
import model.Player;
import model.Position;

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
    Set<Move> validMoves = new HashSet<>();
    List<Position> availableCorners = board.getAvailableCorners(player);
    if (availableCorners.isEmpty()) {
      return new ArrayList<>();
    }
    List<Piece> availablePieces = player.getAvailablePieces();

    for (Piece basePiece : availablePieces) {
      for (int variantIndex = 0; variantIndex < 8; variantIndex++) {
        Piece currentPiece = new Piece(basePiece.getId(), variantIndex);
        for (Position corner : availableCorners) {
          int minRow = corner.getRow() - currentPiece.getShape().rows();
          int maxRow = corner.getRow() + 1;
          int minColumn = corner.getColumn() - currentPiece.getShape().cols();
          int maxColumn = corner.getColumn() + 1;
          for (int row = minRow; row <= maxRow; row++) {
            for (int column = minColumn; column <= maxColumn; column++) {
              Position position = new Position(row, column);
              if (board.isValidMove(currentPiece, position, player)) {
                validMoves.add(new Move(currentPiece, position));
              }
            }
          }
        }
      }
    }

    return new ArrayList<>(validMoves);
  }
}
