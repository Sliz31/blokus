package Logic.AI;

import Logic.Board;
import Logic.Player;

public interface BotState {
  Move decideMove(Board board, Player bot, Player enemy, GraphAnalyzer analyzer);

  BotState nextState(Board board, Player bot, Player enemy, GraphAnalyzer analyzer);
}
