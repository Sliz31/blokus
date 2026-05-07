package model.AI;

import model.Board;
import model.Player;

// fill state: AI is in the endgame and just places the biggest pieces possible
public class FillState implements BotState {

    @Override
    public Move decideMove(Board board, Player bot, Player enemy, GraphAnalyzer analyzer) {
        java.util.List<Move> legalMoves = GraphBot.getAllLegalMoves(board, bot);
        if (legalMoves.isEmpty()) return null;

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
        // fill is the last state - stay here until game over
        return this;
    }
}
