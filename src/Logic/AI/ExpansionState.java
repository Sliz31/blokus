package Logic.AI;

import Logic.Board;
import Logic.Player;

// expansion state: AI tries to grow into as many corners as possible
public class ExpansionState implements BotState {

    @Override
    public Move decideMove(Board board, Player bot, Player enemy, GraphAnalyzer analyzer) {
        java.util.List<Move> legalMoves = GraphBot.getAllLegalMoves(board, bot);
        if (legalMoves.isEmpty())
            return null;

        Move bestMove = null;
        int maxCorners = -1;

        // pick the move that opens up the most new corners for us
        for (Move move : legalMoves) {
            int newCorners = analyzer.calculateNewConnections(board, move.getPiece(), move.getRow(), move.getCol(), bot.getId());
            if (newCorners > maxCorners) {
                maxCorners = newCorners;
                bestMove = move;
            }
        }

        if (bestMove == null) {
            return legalMoves.get(0);
        }
        return bestMove;
    }

    @Override
    public BotState nextState(Board board, Player bot, Player enemy, GraphAnalyzer analyzer) {
        int distanceToEnemy = analyzer.getShortestPathDistance(board, bot.getId(), enemy.getId());
        // if the enemy is getting close, switch to cut mode
        if (distanceToEnemy < 3) {
            return new CutState();
        }
        return this;
    }
}
