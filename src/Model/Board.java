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
    if (player.getStepNumber() == 0 && player.getPlayerId() == 0) {
      corners.add(new int[]{3, 3});
    }

    if (player.getStepNumber() == 0 && player.getPlayerId() == 1) {
      corners.add(new int[]{10, 10});
    }


    return corners;
  }

  public int setPiece(Piece piece, Player player, int row, int column) {
    // TODO: добавить проверки на возможность вставки piece в row, column
    // 1) не выходит ли Piece за границы Board
    if (row < 0 || row >= SIZE || column < 0 || column >= SIZE) {
      return 1;
    }

    int pieceWidth = piece.getColumns();
    int pieceHeight = piece.getRows();
    if (row + pieceWidth > SIZE || column + pieceHeight > SIZE) {
      return 1;
    }

    // 2) первый ли ход игрока
    if (player.getStepNumber() == 0) {

    }
    // 3) можем ли мы поставить фигуру по правилам игры (горизонтально от прошлой фигуры)
    // 4) не пытаемся ли мы кого-то перекрыть



    int[][] shape = piece.getShape();
    System.out.println(shape.length);
    for (int r = 0; r < shape.length; r++) {
      for (int c = 0; c < shape[r].length; c++) {
        grid[row + r][column + c].setOccupied(shape[r][c] == 1, player.getPlayerId());
      }
    }
  }
}
