package Logic.AI;

import Logic.Board;
import Logic.Player;
import java.util.List;

public class ExpansionState implements BotState {

  @Override
  public Move decideMove(Board board, Player bot, Player enemy, GraphAnalyzer analyzer) {
    List<Move> legalMoves = GraphBot.getAllLegalMoves(board, bot);
    if (legalMoves.isEmpty())
      return null;

    Move bestMove = null;
    int maxCorners = -1;

    for (Move move : legalMoves) {
      int newCorners = analyzer.calculateNewConnections(board, move.getPiece(), move.getRow(), move.getCol(),
          bot.getId());
      if (newCorners > maxCorners) {
        maxCorners = newCorners;
        bestMove = move;
      }
    }

    // Fallback or best
    if (bestMove == null) {
      return legalMoves.get(0);
    }
    return bestMove;
  }

  @Override
  public BotState nextState(Board board, Player bot, Player enemy, GraphAnalyzer analyzer) {
    int dist = analyzer.getShortestPathDistance(board, bot.getId(), enemy.getId());
    if (dist < 3) {
      return new CutState();
    }
    return this;
  }
}
