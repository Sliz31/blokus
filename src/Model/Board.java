package Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Board {
  private final int SIZE = 14;

  // Each cell stores occupancy and owner information.
  private final Cell[][] grid;

  public Board() {
    grid = new Cell[SIZE][SIZE];
    for (int row = 0; row < SIZE; row++) {
      for (int column = 0; column < SIZE; column++) {
        grid[row][column] = new Cell(row, column);
      }
    }
  }

  public int getSize() {
    return SIZE;
  }

  public Cell[][] getGrid() {
    return grid;
  }

  // TODO: дописать метод
  // Поиск свободных углов
  public List<int[]> getAvailableCorners(Player player) {
    List<int[]> corners = new ArrayList<>();
    if (player.isFirstMove() && player.getPlayerId() == Constants.FIRSTPLAYER_ID) {
      corners.add(new int[] { 3, 3 });

      return corners;
    }

    if (player.isFirstMove() && player.getPlayerId() == Constants.SECONDPLAYER_ID) {
      corners.add(new int[] { 10, 10 });

      return corners;
    }

    for (int row = 0; row < SIZE; row++) {
      for (int column = 0; column < SIZE; column++) {
        if (grid[row][column].getPlayerId() != player.getPlayerId()) {
          continue;
        }

        List<int[]> diagonals = new ArrayList<>();
        // лево вверх
        int[] topLeft = new int[] { row - 1, column - 1 };
        if (isCoordinatesInGrid(topLeft)) {
          diagonals.add(topLeft);
        }

        // лево низ
        int[] bottomLeft = new int[] { row + 1, column - 1 };
        if (isCoordinatesInGrid(bottomLeft)) {
          diagonals.add(bottomLeft);
        }

        // право вверх
        int[] topRight = new int[] { row - 1, column + 1 };
        if (isCoordinatesInGrid(topRight)) {
          diagonals.add(topRight);
        }

        // право низ
        int[] bottomRight = new int[] { row + 1, column + 1 };
        if (isCoordinatesInGrid(bottomRight)) {
          diagonals.add(bottomRight);
        }

        List<int[]> freeDiagonals = diagonals.stream()
            .filter(diagonal -> !grid[diagonal[0]][diagonal[1]].isOccupied())
            .filter(diagonal -> isCoordinatesInGrid(diagonal))
            .toList();

        List<int[]> freeCorners = freeDiagonals.stream()
            .filter(freeDiagonal -> {
              int r = freeDiagonal[0];
              int c = freeDiagonal[1];

              int[] top = new int[] { r - 1, c };
              if (isCoordinatesInGrid(top)
                  && (top[0] >= 0 && grid[top[0]][top[1]].getPlayerId() == player.getPlayerId())) {
                return false;
              }

              int[] bottom = new int[] { r + 1, c }; // { 5, 13 }
              if (isCoordinatesInGrid(bottom)
                  && (bottom[0] >= 0 && grid[bottom[0]][bottom[1]].getPlayerId() == player.getPlayerId())) {
                return false;
              }

              int[] right = new int[] { r, c + 1 }; // { 4, 14 }
              if (isCoordinatesInGrid(right)
                  && (right[0] >= 0 && grid[right[0]][right[1]].getPlayerId() == player.getPlayerId())) {
                return false;
              }

              int[] left = new int[] { r, c - 1 };
              if (isCoordinatesInGrid(left)
                  && (left[0] >= 0 && grid[left[0]][left[1]].getPlayerId() == player.getPlayerId())) {
                return false;
              }

              return true;
            })
            .toList();

        corners.addAll(freeCorners);
      }
    }

    return corners;
  }

  public boolean isValidMove(Piece piece, Player player, int row, int column) {
    int[][] shape = piece.getShape();
    int pieceRows = piece.getRows();
    int pieceCols = piece.getColumns();

    // 1) не выходит ли Piece за границы Board
    if (row < 0 || row + pieceRows > SIZE || column < 0 || column + pieceCols > SIZE) {
      return false;
    }

    boolean isFirstMove = player.isFirstMove();
    boolean touchesCorner = false;
    boolean coversStart = false;

    // В текущем алгоритме getAvailableCorners стартовые углы: (3,3) и (10,10)
    int startRow = player.getPlayerId() == Constants.FIRSTPLAYER_ID ? 3 : 10;
    int startCol = player.getPlayerId() == Constants.FIRSTPLAYER_ID ? 3 : 10;

    for (int r = 0; r < pieceRows; r++) {
      for (int c = 0; c < pieceCols; c++) {
        if (shape[r][c] == 1) {
          int boardR = row + r;
          int boardC = column + c;

          // 2) Клетка на доске должна быть свободна
          if (grid[boardR][boardC].isOccupied()) {
            return false;
          }

          // Если это первый ход, фигура должна закрывать стартовую клетку
          if (isFirstMove && boardR == startRow && boardC == startCol) {
            coversStart = true;
          }

          // 3) Фигура не должна касаться фигур своего цвета по граням
          if (boardR > 0 && grid[boardR - 1][boardC].getPlayerId() == player.getPlayerId())
            return false;
          if (boardR < SIZE - 1 && grid[boardR + 1][boardC].getPlayerId() == player.getPlayerId())
            return false;
          if (boardC > 0 && grid[boardR][boardC - 1].getPlayerId() == player.getPlayerId())
            return false;
          if (boardC < SIZE - 1 && grid[boardR][boardC + 1].getPlayerId() == player.getPlayerId())
            return false;

          // 4) Фигура должна касаться углом фигуры своего цвета
          if (!touchesCorner) {
            if (boardR > 0 && boardC > 0 && grid[boardR - 1][boardC - 1].getPlayerId() == player.getPlayerId())
              touchesCorner = true;
            if (boardR > 0 && boardC < SIZE - 1 && grid[boardR - 1][boardC + 1].getPlayerId() == player.getPlayerId())
              touchesCorner = true;
            if (boardR < SIZE - 1 && boardC > 0 && grid[boardR + 1][boardC - 1].getPlayerId() == player.getPlayerId())
              touchesCorner = true;
            if (boardR < SIZE - 1 && boardC < SIZE - 1
                && grid[boardR + 1][boardC + 1].getPlayerId() == player.getPlayerId())
              touchesCorner = true;
          }
        }
      }
    }

    if (isFirstMove) {
      return coversStart;
    }

    return touchesCorner;
  }

  public void setPiece(Piece piece, Player player, int row, int column) {
    int[][] shape = piece.getShape();
    System.out.println(shape.length);
    for (int r = 0; r < shape.length; r++) {
      for (int c = 0; c < shape[r].length; c++) {
        grid[row + r][column + c].setOccupied(shape[r][c] == 1, player.getPlayerId());
      }
    }
  }

  private boolean isCoordinatesInGrid(int[] coordinates) {
    return !(coordinates[0] < 0 || coordinates[1] < 0 || coordinates[0] > SIZE - 1 || coordinates[1] > SIZE - 1);
  }
}
