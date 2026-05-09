package model.AI;

import model.Board;
import model.Player;

// interface that all AI states must implement
public interface BotState {
  Move decideMove(Board board, Player bot, Player enemy, GraphAnalyzer analyzer);

  BotState nextState(Board board, Player bot, Player enemy, GraphAnalyzer analyzer);
}
