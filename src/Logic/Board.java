package Logic;

import java.util.ArrayList;
import java.util.List;

public class Board {
  private final int size = 14;
  private Cell[][] grid;

  public Board() {
    grid = new Cell[size][size];
    for (int r = 0; r < size; r++) {
      for (int c = 0; c < size; c++) {
        grid[r][c] = new Cell(r, c);
      }
    }
  }

  public Board(Board other) {
    this();
    for (int r = 0; r < size; r++) {
      for (int c = 0; c < size; c++) {
        if (other.grid[r][c].isOccupied()) {
          this.grid[r][c].setOccupied(true, other.grid[r][c].getPlayerId());
        }
      }
    }
  }

  public int getSize() {
    return size;
  }

  public Cell[][] getGrid() {
    return grid;
  }

  public boolean isValidMove(Piece piece, int startRow, int startCol, Player player) {
    int[][] shape = piece.getShape();
    boolean cornerTouch = false;
    boolean isOnStartCorner = false;

    for (int r = 0; r < shape.length; r++) {
      for (int c = 0; c < shape[0].length; c++) {
        if (shape[r][c] == 1) {
          int boardRow = startRow + r;
          int boardCol = startCol + c;

          if (boardRow < 0 || boardRow >= size || boardCol < 0 || boardCol >= size) {
            return false;
          }

          if (grid[boardRow][boardCol].isOccupied()) {
            return false;
          }

          if (hasOrthogonalNeighbor(boardRow, boardCol, player.getId())) {
            return false;
          }

          if (hasDiagonalNeighbor(boardRow, boardCol, player.getId())) {
            cornerTouch = true;
          }

          // Blokus Duo starting corners: (4, 4) and (9, 9)
          if (isStartCorner(boardRow, boardCol)) {
            isOnStartCorner = true;
          }
        }
      }
    }

    if (player.isFirstMove()) {
      return isOnStartCorner;
    }

    return cornerTouch;
  }

  private boolean isStartCorner(int row, int col) {
    return (row == 4 && col == 4) || (row == 9 && col == 9);
  }

  private boolean hasOrthogonalNeighbor(int row, int col, int playerId) {
    int[][] directions = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
    for (int[] d : directions) {
      int r = row + d[0];
      int c = col + d[1];
      if (r >= 0 && r < size && c >= 0 && c < size) {
        if (grid[r][c].isOccupied() && grid[r][c].getPlayerId() == playerId) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean hasDiagonalNeighbor(int row, int col, int playerId) {
    int[][] directions = { { -1, -1 }, { -1, 1 }, { 1, -1 }, { 1, 1 } };
    for (int[] d : directions) {
      int r = row + d[0];
      int c = col + d[1];
      if (r >= 0 && r < size && c >= 0 && c < size) {
        if (grid[r][c].isOccupied() && grid[r][c].getPlayerId() == playerId) {
          return true;
        }
      }
    }
    return false;
  }

  public void placePiece(Piece piece, int startRow, int startCol, int playerId) {
    int[][] shape = piece.getShape();
    for (int r = 0; r < shape.length; r++) {
      for (int c = 0; c < shape[0].length; c++) {
        if (shape[r][c] == 1) {
          grid[startRow + r][startCol + c].setOccupied(true, playerId);
        }
      }
    }
  }

  // Returns a list of cell coordinates [row, col] that represent available
  // corners for the given player
  public List<int[]> getAvailableCorners(int playerId) {
    List<int[]> corners = new ArrayList<>();
    for (int r = 0; r < size; r++) {
      for (int c = 0; c < size; c++) {
        if (!grid[r][c].isOccupied() && !hasOrthogonalNeighbor(r, c, playerId)) {
          // Check if it's the first move context (needs to handle logic if called
          // initially)
          if (hasDiagonalNeighbor(r, c, playerId) || isStartCorner(r, c)) {
            corners.add(new int[] { r, c });
          }
        }
      }
    }
    return corners;
  }

  public void printBoard() {
    System.out.println("  0 1 2 3 4 5 6 7 8 9 0 1 2 3");
    for (int r = 0; r < size; r++) {
      System.out.print((r % 10) + " ");
      for (int c = 0; c < size; c++) {
        if (!grid[r][c].isOccupied()) {
          System.out.print(". ");
        } else {
          int pId = grid[r][c].getPlayerId();
          System.out.print((pId == 1 ? "X" : "O") + " ");
        }
      }
      System.out.println();
    }
  }
}