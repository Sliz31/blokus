package Model;

import java.util.ArrayList;
import java.util.List;

public class Board {
  private final int SIZE = Constants.BOARD_SIZE;

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
    return grid[position.row()][position.col()];
  }

  public List<Position> getAvailableCorners(Player player) {
    List<Position> corners = new ArrayList<>();
    if (player.isFirstMove() && player.getId() == Constants.FIRSTPLAYER_ID) {
      corners.add(Constants.FIRST_PLAYER_START_POSITION);

      return corners;
    }

    if (player.isFirstMove() && player.getId() == Constants.SECONDPLAYER_ID) {
      corners.add(Constants.SECOND_PLAYER_START_POSITION);

      return corners;
    }

    for (int row = 0; row < SIZE; row++) {
      for (int column = 0; column < SIZE; column++) {
        Position cellPos = new Position(row, column);
        if (getCell(cellPos).getPlayerId() != player.getId()) {
          continue;
        }

        List<Position> diagonals = List.of(
            cellPos.shift(-1, -1),
            cellPos.shift(1, -1),
            cellPos.shift(-1, 1),
            cellPos.shift(1, 1))
            .stream()
            .filter(position -> isCoordinatesInGrid(position))
            .filter(position -> !getCell(position).isOccupied())
            .filter(position -> doesPositionMatchCrossConditions(position, player))
            .toList();

        corners.addAll(diagonals);
      }
    }

    return corners;
  }

  public boolean isValidMove(Piece piece, Player player, Position position) {
    // TODO: добавить проверки на возможность вставки piece в row, column
    // 1) не выходит ли Piece за границы Board
    int pieceWidth = piece.getColumns();
    int pieceHeight = piece.getRows();
    Position shiftedPosition = position.shift(pieceWidth, pieceHeight);

    if (position.row() < 0
        || position.col() < 0
        || shiftedPosition.col() > SIZE
        || shiftedPosition.row() > SIZE) {
      return false;
    }

    // 2) первый ли ход игрока
    List<Position> availableCorners = getAvailableCorners(player);
    boolean isAvailableCorners = availableCorners.contains(position);

    // 3) можем ли мы поставить фигуру по правилам игры (горизонтально от прошлой
    // фигуры)
    // 4) не пытаемся ли мы кого-то перекрыть
    if (!getCell(position).isOccupied()) {
      return true;
    }

    return false;
  }

  public void setPiece(Piece piece, Player player, Position position) {
    Shape shape = piece.getShape();
    for (int row = 0; row < shape.rows(); row++) {
      for (int col = 0; col < shape.cols(); col++) {
        Position shiftedPosition = position.add(new Position(row, col));
        getCell(shiftedPosition).setOccupied(shape.cellAt(row, col), player.getId());
      }
    }
  }

  private boolean isCoordinatesInGrid(Position position) {
    return !(position.row() < 0
        || position.col() < 0
        || position.row() >= SIZE
        || position.col() >= SIZE);
  }

  private boolean doesPositionMatchCrossConditions(Position position, Player player) {
    List<Position> crossPositions = List.of(
        position.shift(-1, 0),
        position.shift(1, 0),
        position.shift(0, 1),
        position.shift(0, -1));

    for (Position pos : crossPositions) {
      if (!isCoordinatesInGrid(pos)) {
        continue;
      }

      if (getCell(pos).getPlayerId() == player.getId()) {
        return false;
      }
    }

    return true;
  }
}
