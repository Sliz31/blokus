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
      for (int col = 0; col < SIZE; col++) {
        Position position = new Position(row, col);
        grid[row][col] = new Cell(position);
      }
    }
  }

  public int getSize() {
    return SIZE;
  }

  public Cell[][] getGrid() {
    return grid;
  }

  public Cell getCell(Position position) {
    return this.grid[position.row()][position.col()];
  }

  public List<Position> getAvailableCorners(Player player) {
    List<Position> corners = new ArrayList<>();
    if (player.isFirstMove() && player.getPlayerId() == Constants.FIRSTPLAYER_ID) {
      corners.add(Constants.FIRST_PLAYER_START_POSITION);

      return corners;
    }

    if (player.isFirstMove() && player.getPlayerId() == Constants.SECONDPLAYER_ID) {
      corners.add(Constants.SECOND_PLAYER_START_POSITION);

      return corners;
    }

    for (int row = 0; row < SIZE; row++) {
      for (int column = 0; column < SIZE; column++) {
        if (grid[row][column].getPlayerId() != player.getPlayerId()) {
          continue;
        }

        List<Position> diagonals = List.of(
            new Position(row - 1, column - 1),
            new Position(row + 1, column - 1),
            new Position(row - 1, column + 1),
            new Position(row + 1, column + 1))
            .stream()
            .filter(position -> isCoordinatesInGrid(position))
            .filter(position -> !getCell(position).isOccupied())
            .filter(position -> {
              int r = position.row();
              int c = position.col();

              Position top = new Position(r - 1, c);
              if (isCoordinatesInGrid(top)
                  && (top.row() >= 0 && getCell(top).getPlayerId() == player.getPlayerId())) {
                return false;
              }

              Position bottom = new Position(r + 1, c);
              if (isCoordinatesInGrid(bottom)
                  && (bottom.row() >= 0
                      && getCell(bottom).getPlayerId() == player.getPlayerId())) {
                return false;
              }

              Position right = new Position(r, c + 1);
              if (isCoordinatesInGrid(right)
                  && (right.row() >= 0 && getCell(right).getPlayerId() == player.getPlayerId())) {
                return false;
              }

              Position left = new Position(r, c - 1);
              if (isCoordinatesInGrid(left)
                  && (left.row() >= 0 && getCell(left).getPlayerId() == player.getPlayerId())) {
                return false;
              }

              return true;
            })
            .toList();

        corners.addAll(diagonals);
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
    List<Position> availableCorners = getAvailableCorners(player);
    boolean isAvailableCorners = availableCorners.contains(new Position(row, column));

    // 3) можем ли мы поставить фигуру по правилам игры (горизонтально от прошлой
    // фигуры)
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

  private boolean isCoordinatesInGrid(Position position) {
    return !(position.row() < 0 ||
        position.col() < 0 ||
        position.row() >= SIZE ||
        position.col() >= SIZE);
  }
}
