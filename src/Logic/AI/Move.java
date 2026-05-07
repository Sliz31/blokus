package Logic.AI;

import Logic.Piece;

// a single move: which piece to place and where on the board
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
