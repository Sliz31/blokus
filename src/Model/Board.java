package Model;

import java.util.ArrayList;
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
  public List<int[]> getAvailableCorners(Player player) {
    List<int[]> corners = new ArrayList<>();
    if (player.isFirstMove() && player.getPlayerId() == 0) {
      corners.add(new int[]{3, 3});

      return corners;
    }

    if (player.isFirstMove() && player.getPlayerId() == 1) {
      corners.add(new int[]{10, 10});

      return corners;
    }

    for (int row = 0; row < SIZE; row++) {
      for (int column = 0; column < SIZE; column++) {
        if (grid[row][column].getPlayerId() != player.getPlayerId()) {
          continue;
        }

        List<int[]> diagonals = new ArrayList<>();
        // Todo: переписать
        // лево вверх
        if (row - 1 >= 0 && column - 1 >= 0) {
          diagonals.add(new int[]{row - 1, column - 1});
        }
        // лево низ
        if (row + 1 < SIZE && column - 1 >= 0) {
          diagonals.add(new int[]{row + 1, column - 1});
        }
        // право вверх
        if (row - 1 >= 0 && column + 1 < SIZE) {
          diagonals.add(new int[]{row - 1, column + 1});
        }
        // право низ
        int[] bottomRight = new int[]{row + 1, column + 1};
        if (isCoordinatesInGrid(bottomRight)) {
          diagonals.add(bottomRight);
        }

        List<int[]> freeDiagonals = diagonals.stream()
          .filter(diagonal -> !grid[diagonal[0]][diagonal[1]].isOccupied())
          .toList();

        List<int[]> freeCorners = freeDiagonals.stream()
          .filter(freeDiagonal -> {
            int r = freeDiagonal[0];
            int c = freeDiagonal[1];

            int[] top = new int[]{r - 1, c};
            if (top[0] >= 0 && grid[top[0]][top[1]].getPlayerId() == player.getPlayerId()) {
              return false;
            }

            int[] bottom = new int[]{r + 1, c};
            if (bottom[0] >= 0 && grid[bottom[0]][bottom[1]].getPlayerId() == player.getPlayerId()) {
              return false;
            }

            int[] right = new int[]{r, c + 1};
            if (right[0] >= 0 && grid[right[0]][right[1]].getPlayerId() == player.getPlayerId()) {
              return false;
            }

            int[] left = new int[]{r, c - 1};
            if (left[0] >= 0 && grid[left[0]][left[1]].getPlayerId() == player.getPlayerId()) {
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
    // TODO: добавить проверки на возможность вставки piece в row, column
    // 1) не выходит ли Piece за границы Board
    int pieceWidth = piece.getColumns();
    int pieceHeight = piece.getRows();

    if (row < 0 || row + pieceWidth > SIZE || column < 0 || column + pieceHeight > SIZE) {
      return false;
    }

    // 2) первый ли ход игрока
    List<int[]> availableCorners = getAvailableCorners(player);
    boolean isAvailableCorners = availableCorners.contains(new int[]{row, column});



    // 3) можем ли мы поставить фигуру по правилам игры (горизонтально от прошлой фигуры)
    // 4) не пытаемся ли мы кого-то перекрыть
    if (!grid[row][column].isOccupied()) {
      return true;
    }
    return false;
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
    return coordinates[0] < 0 || coordinates[1] < 0 || coordinates[0] > SIZE || coordinates[1] > SIZE;
  }
}
