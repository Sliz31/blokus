package Logic.AI;

import Logic.Board;
import Logic.Player;

// interface that all AI states must implement
public interface BotState {
    // pick the best move for this state
    Move decideMove(Board board, Player bot, Player enemy, GraphAnalyzer analyzer);

    // check if we should switch to a different state
    BotState nextState(Board board, Player bot, Player enemy, GraphAnalyzer analyzer);
}
