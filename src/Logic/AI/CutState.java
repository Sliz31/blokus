package Logic.AI;

import Logic.Board;
import Logic.Player;
import java.util.List;
import java.util.ArrayList;

public class CutState implements BotState {

  @Override
  public Move decideMove(Board board, Player bot, Player enemy, GraphAnalyzer analyzer) {
    List<Move> legalMoves = GraphBot.getAllLegalMoves(board, bot);
    if (legalMoves.isEmpty())
      return null;

    List<Move> cutMoves = new ArrayList<>();

    for (Move move : legalMoves) {
      // A move spans multiple cells. If any of the piece's newly occupied cells acts
      // as a cut vertex
      // it's a very strong move.
      boolean isCut = false;
      int[][] shape = move.getPiece().getShape();
      for (int r = 0; r < shape.length; r++) {
        for (int c = 0; c < shape[0].length; c++) {
          if (shape[r][c] == 1) {
            if (analyzer.isCutVertexForOpponent(board, move.getRow() + r, move.getCol() + c, enemy.getId())) {
              isCut = true;
              break;
            }
          }
        }
        if (isCut)
          break;
      }
      if (isCut) {
        cutMoves.add(move);
      }
    }

    if (!cutMoves.isEmpty()) {
      // Pick the cut move that maximizes our own corners
      Move bestMove = null;
      int maxCorners = -1;
      for (Move move : cutMoves) {
        int newCorners = analyzer.calculateNewConnections(board, move.getPiece(), move.getRow(), move.getCol(),
            bot.getId());
        if (newCorners > maxCorners) {
          maxCorners = newCorners;
          bestMove = move;
        }
      }
      return bestMove != null ? bestMove : cutMoves.get(0);
    }

    // Fallback to ExpansionState logic
    return new ExpansionState().decideMove(board, bot, enemy, analyzer);
  }

  @Override
  public BotState nextState(Board board, Player bot, Player enemy, GraphAnalyzer analyzer) {
    if (board.getAvailableCorners(bot.getId()).size() < 5) {
      return new FillState();
    }

    // Also transition if NO cut vertices are possible. This is expensive to check
    // across all moves perfectly,
    // but simplified: if the enemy has 0 or 1 component and we can't cut it, maybe
    // we should fill.
    // As per prompt: "if no articulation points can be found", we transition.
    List<Move> legalMoves = GraphBot.getAllLegalMoves(board, bot);
    boolean possibleCutFound = false;
    for (Move move : legalMoves) {
      int[][] shape = move.getPiece().getShape();
      for (int r = 0; r < shape.length; r++) {
        for (int c = 0; c < shape[0].length; c++) {
          if (shape[r][c] == 1) {
            if (analyzer.isCutVertexForOpponent(board, move.getRow() + r, move.getCol() + c, enemy.getId())) {
              possibleCutFound = true;
              break;
            }
          }
        }
        if (possibleCutFound)
          break;
      }
      if (possibleCutFound)
        break;
    }

    if (!possibleCutFound) {
      return new FillState();
    }

    return this;
  }
}
