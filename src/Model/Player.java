package Model;

import java.util.ArrayList;
import java.util.List;

public class Player {
  private final int playerId;
  private final String name;
  private final List<Piece> inventory;
  private int stepNumber;

  public Player(int playerId, String name) {
    this.playerId = playerId;
    this.name = name;
    this.inventory = new ArrayList<>();
    this.stepNumber = 0;
    initializeInventory();
  }

  public int getPlayerId() {
    return playerId;
  }

  public String getName() {
    return name;
  }

  public int getStepNumber() {
    return stepNumber;
  }

  public List<Piece> getInventory() {
    return inventory;
  }

  public void setStepNumber(int stepNumber) {
    this.stepNumber = stepNumber;
  }

  public boolean isFirstMove() {
    return stepNumber == 0;
  }

  private void initializeInventory() {
    for (int i = 0; i < Piece.SHAPES.length; i++) {
      int shapeRows = Piece.SHAPES[i].length;
      int shapeCols = Piece.SHAPES[i][0].length;
      int[][] shapeCopy = new int[shapeRows][shapeCols];

      for (int r = 0; r < shapeRows; r++) {
        System.arraycopy(Piece.SHAPES[i][r], 0, shapeCopy[r], 0, shapeCols);
      }

      inventory.add(new Piece(i));
    }
  }
}
