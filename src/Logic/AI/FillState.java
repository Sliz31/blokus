package Logic.AI;

import Logic.Board;
import Logic.Player;
import java.util.List;

public class FillState implements BotState {

  @Override
  public Move decideMove(Board board, Player bot, Player enemy, GraphAnalyzer analyzer) {
    List<Move> legalMoves = GraphBot.getAllLegalMoves(board, bot);
    if (legalMoves.isEmpty())
      return null;

    Move bestMove = null;
    int maxSize = -1;

    for (Move move : legalMoves) {
      int size = move.getPiece().getSize();
      if (size > maxSize) {
        maxSize = size;
        bestMove = move;
      }
    }
    return bestMove;
  }

  @Override
  public BotState nextState(Board board, Player bot, Player enemy, GraphAnalyzer analyzer) {
    return this; // Terminal state, simply keeps packing until game over
  }
}
