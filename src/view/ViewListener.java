package view;

import model.Piece;

// the view calls these methods to tell the controller what the user just did
// the controller implements this interface and registers itself with the window
public interface ViewListener {
    // user clicked a cell on the board
    void onCellClicked(int row, int column);

    // user clicked the Rotate button
    void onRotateClicked();

    // user clicked the Flip button
    void onFlipClicked();

    // user clicked the Pass Turn button
    void onPassClicked();

    // user selected a piece from the inventory panel
    void onPieceSelected(Piece piece);
}
