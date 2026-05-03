package Model;

import java.util.*;

public class GameState {
  public static final int SIZE = Constants.BOARD_SIZE;
  public final int[][] board;
  public final List<Player> players;
  public final int currentPlayer;

  public GameState(int[][] board, List<Player> players, int currentPlayer) {
    this.board = board;
    this.players = players;
    this.currentPlayer = currentPlayer;
  }

  public GameState applyMove(Move move) {
    int[][] newBoard = copyBoard();
    Piece piece = move.piece;
    Shape shape = piece.getShape();

    for (int row = 0; row < piece.getRows(); row++) {
      for (int col = 0; col < piece.getColumns(); col++) {
        Position position = move.position.shift(row, col);
        newBoard[position.row()][position.col()] = move.player.getId();

      }
    }

    // копируем игроков
    List<Player> newPlayers = new ArrayList<>();
    for (Player player : players) {
      newPlayers.add(player.copy());
    }

    Player current = newPlayers
        .stream()
        .filter(player -> move.player.getId() == currentPlayer)
        .findFirst()
        .orElseThrow(() -> new RuntimeException("Player not found"));

    current.removePiece(move.pieceId);

    // обновляем углы
    current.updateCorners(newBoard);

    int nextPlayer = (currentPlayer + 1) % players.size();

    return new GameState(newBoard, newPlayers, nextPlayer);
  }

  private int[][] copyBoard() {
    int[][] copy = new int[SIZE][SIZE];
    for (int i = 0; i < SIZE; i++) {
      System.arraycopy(board[i], 0, copy[i], 0, SIZE);
    }
    return copy;
  }
}
