package Logic.AI;

import Logic.Board;
import Logic.Player;

// fill state: AI is in the endgame and just tries to place as many cells as possible
public class FillState implements BotState {

    @Override
    public Move decideMove(Board board, Player bot, Player enemy, GraphAnalyzer analyzer) {
        java.util.List<Move> legalMoves = GraphBot.getAllLegalMoves(board, bot);
        if (legalMoves.isEmpty())
            return null;

        Move bestMove = null;
        int maxSize = -1;

        // pick the biggest piece we can still place
        for (Move move : legalMoves) {
            int pieceSize = move.getPiece().getSize();
            if (pieceSize > maxSize) {
                maxSize = pieceSize;
                bestMove = move;
            }
        }
        return bestMove;
    }

    @Override
    public BotState nextState(Board board, Player bot, Player enemy, GraphAnalyzer analyzer) {
        // fill is the last state - once here, AI stays here until game over
        return this;
    }
}
