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

  private Player(int playerId, String name, List<Piece> inventory, int stepNumber) {
    this.playerId = playerId;
    this.name = name;
    this.inventory = inventory;
    this.stepNumber = stepNumber;
  }

  public int getId() {
    return playerId;
  }

  public String getName() {
    return name;
  }

  public int getStepNumber() {
    return stepNumber;
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
        for (int c = 0; c < shapeCols; c++) {
          // TODO: заменить на System.arraycopy
          shapeCopy[r][c] = Piece.SHAPES[i][r][c];
        }
      }
      inventory.add(new Piece(i));
    }
  }

  public final Player copy() {
    int newPlayerId = playerId;
    String newName = name;
    List<Piece> newInventory = new ArrayList<Piece>();
    int newStepNumber = this.stepNumber;

    for (Piece piece : this.inventory) {
      // TODO: copy pieces
    }

    return new Player(newPlayerId, newName, newInventory, stepNumber);
  }
}
