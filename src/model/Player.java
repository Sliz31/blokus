package model;

import java.util.ArrayList;
import java.util.List;

// one player - stores name, id, piece inventory, and first-move flag
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
        this.isFirstMove = firstMove;
    }

    public List<Piece> getInventory() {
        return inventory;
    }

    // returns only pieces that haven't been placed yet
    public List<Piece> getAvailablePieces() {
        List<Piece> available = new ArrayList<>();
        for (Piece piece : inventory) {
            if (!piece.isUsed())
                available.add(piece);
        }
        return available;
    }

    // total cells in remaining pieces (lower = better at end)
    public int getRemainingSquares() {
        int total = 0;
        for (Piece piece : getAvailablePieces())
            total += piece.getSize();
        return total;
    }

    // give the player all 21 pieces at the start
    private void initializeInventory() {
        for (int pieceId = 1; pieceId <= 21; pieceId++) {
            inventory.add(new Piece(pieceId));
        }
    }
}
