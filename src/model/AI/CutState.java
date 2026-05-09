package model.AI;

import model.Board;
import model.Player;
import model.Shape;
import model.Position;
import java.util.List;
import java.util.ArrayList;

// cut state: AI tries to block the enemy by occupying articulation points
public class CutState implements BotState {

  @Override
  public Move decideMove(Board board, Player bot, Player enemy, GraphAnalyzer analyzer) {
    List<Move> legalMoves = GraphBot.getAllLegalMoves(board, bot);
    if (legalMoves.isEmpty()) {
      return null;
    }

    List<Move> cutMoves = new ArrayList<>();

    // find all moves where at least one cell blocks a key point for the enemy
    for (Move move : legalMoves) {
      boolean isCut = false;
      Shape shape = move.getPiece().getShape();
      for (int row = 0; row < shape.rows(); row++) {
        for (int column = 0; column < shape.cols(); column++) {
          if (shape.cellAt(row, column) == 1) {
            Position pos = new Position(move.getPosition().getRow() + row,
                move.getPosition().getColumn() + column);
            if (analyzer.isCutVertexForOpponent(board, pos, enemy.getId())) {
              isCut = true;
              break;
            }
          }
        }
        if (isCut) {
          break;
        }
      }
      if (isCut) {
        cutMoves.add(move);
      }
    }

    if (!cutMoves.isEmpty()) {
      // among cut moves pick the one that also gives us most new corners
      Move bestMove = null;
      int maxCorners = -1;
      for (Move move : cutMoves) {
        int newCorners = analyzer.calculateNewConnections(board, move.getPiece(), move.getPosition(),
            bot.getId());
        if (newCorners > maxCorners) {
          maxCorners = newCorners;
          bestMove = move;
        }
      }
      return bestMove != null ? bestMove : cutMoves.get(0);
    }

    // no cut moves found - fall back to expansion logic
    return new ExpansionState().decideMove(board, bot, enemy, analyzer);
  }

  @Override
  public BotState nextState(Board board, Player bot, Player enemy, GraphAnalyzer analyzer) {
    // switch to fill if running out of corners
    if (board.getAvailableCorners(bot.getId()).size() < 5) {
      return new FillState();
    }

    // also switch to fill if no cut opportunities exist
    List<Move> legalMoves = GraphBot.getAllLegalMoves(board, bot);
    for (Move move : legalMoves) {
      Shape shape = move.getPiece().getShape();
      for (int row = 0; row < shape.rows(); row++) {
        for (int column = 0; column < shape.cols(); column++) {
          if (shape.cellAt(row, column) == 1) {
            Position pos = new Position(move.getPosition().getRow() + row,
                move.getPosition().getColumn() + column);
            if (analyzer.isCutVertexForOpponent(board, pos, enemy.getId())) {
              return this;
            }
          }
        }
      }
    }
    return new FillState();
  }
}
