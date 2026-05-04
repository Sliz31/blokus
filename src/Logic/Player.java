package Logic;

import java.util.ArrayList;
import java.util.List;

public class Player {
  private int id;
  private String name;
  private List<Piece> inventory;
  private boolean isFirstMove;

  public Player(int id, String name) {
    this.id = id;
    this.name = name;
    this.inventory = new ArrayList<>();
    this.isFirstMove = true;
    initializeInventory();
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public boolean isFirstMove() {
    return isFirstMove;
  }

  public void setFirstMove(boolean firstMove) {
    isFirstMove = firstMove;
  }

  public List<Piece> getInventory() {
    return inventory;
  }

  public List<Piece> getAvailablePieces() {
    List<Piece> available = new ArrayList<>();
    for (Piece p : inventory) {
      if (!p.isUsed()) {
        available.add(p);
      }
    }
    return available;
  }

  public int getRemainingSquares() {
    int count = 0;
    for (Piece piece : getAvailablePieces()) {
      count += piece.getSize();
    }
    return count;
  }

  private void initializeInventory() {
    for (int i = 0; i < Piece.SHAPES.length; i++) {
      int[][] shapeCopy = new int[Piece.SHAPES[i].length][Piece.SHAPES[i][0].length];
      for (int r = 0; r < Piece.SHAPES[i].length; r++) {
        System.arraycopy(Piece.SHAPES[i][r], 0, shapeCopy[r], 0, Piece.SHAPES[i][r].length);
      }
      inventory.add(new Piece(i + 1, shapeCopy));
    }
  }
}