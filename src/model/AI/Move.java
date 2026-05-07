package model.AI;

import model.Piece;

// a single move: which piece to place and where
public class Move {
    private Piece piece;
    private int row;
    private int col;

    public Move(Piece piece, int row, int col) {
        this.piece = piece;
        this.row = row;
        this.col = col;
    }

    public Piece getPiece() { return piece; }
    public int getRow()     { return row; }
    public int getCol()     { return col; }
}
