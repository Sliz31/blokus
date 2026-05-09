package controller;

import model.GameModel;
import model.Piece;
import model.Position;
import model.AI.Move;
import view.BlokusWindow;
import view.ViewListener;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

// the controller - sits between the model and the view
// it receives events from the view, applies them to the model, and tells the view to update
public class GameController implements ViewListener {

    private GameModel model;
    private BlokusWindow window;

    // the piece the human currently has selected from their inventory
    private Piece selectedPiece = null;

    public GameController() {
        model = new GameModel();

        // build the window on the EDT and pass 'this' as the listener
        SwingUtilities.invokeLater(() -> {
            window = new BlokusWindow(this);
            window.setVisible(true);
            window.showLogMessage("====== Blokus Game Started ======");
            window.showLogMessage("Player 1 (Blue) is Human");
            window.showLogMessage("Player 2 (Red) is GraphBot AI");
            window.updateBoardState(model.getBoard());
            window.updateInventory(model.getHuman().getAvailablePieces());
            window.updateScores(model.getHuman().getRemainingSquares(), model.getAi().getRemainingSquares());
            checkTurn();
        });
    }

    // user selected a piece from the inventory panel
    @Override
    public void onPieceSelected(Piece piece) {
        selectedPiece = piece;
        window.setSelectedPiece(piece);
        window.highlightSelectedPiece(piece);
        window.showLogMessage("Selected Piece " + piece.getId());
    }

    // user clicked the Rotate button
    @Override
    public void onRotateClicked() {
        if (selectedPiece == null)
            return;
        selectedPiece.rotate();
        window.repaintSelectedPieceAndGhost();
        window.showLogMessage("Piece rotated.");
    }

    // user clicked the Flip button
    @Override
    public void onFlipClicked() {
        if (selectedPiece == null)
            return;
        selectedPiece.flip();
        window.repaintSelectedPieceAndGhost();
        window.showLogMessage("Piece flipped.");
    }

    // user clicked the Pass Turn button
    @Override
    public void onPassClicked() {
        if (model.isGameOver() || !model.isHumanTurn())
            return;
        window.showLogMessage("Human voluntarily passed.");
        model.setHumanConsecutivePass(true);
        clearSelection();
        model.switchTurn();
        checkTurn();
        if (!model.isGameOver() && !model.isHumanTurn()) {
            triggerAITurn();
        }
    }

    // user clicked a cell on the board
    @Override
    public void onCellClicked(Position position) {
        if (model.isGameOver() || !model.isHumanTurn())
            return;

        if (selectedPiece == null) {
            window.showLogMessage("Please select a piece from your inventory first.");
            return;
        }

        // ask the model to try placing the piece
        boolean placed = model.tryPlaceHumanPiece(selectedPiece, position);
        if (placed) {
            window.showLogMessage("Human played Piece ID " + selectedPiece.getId() + " at (" + position.getRow() + ", "
                    + position.getColumn() + ")");
            clearSelection();
            window.updateBoardState(model.getBoard());
            window.updateInventory(model.getHuman().getAvailablePieces());
            window.updateScores(model.getHuman().getRemainingSquares(), model.getAi().getRemainingSquares());
            model.switchTurn();
            checkTurn();
            if (!model.isGameOver() && !model.isHumanTurn()) {
                triggerAITurn();
            }
        } else {
            window.showLogMessage("Invalid move! Fails Blokus rules.");
        }
    }

    // checks whose turn it is and handles forced passes or end of game
    private void checkTurn() {
        if (model.isGameOver())
            return;

        boolean humanHasMoves = model.humanHasLegalMoves();
        boolean aiHasMoves = model.aiHasLegalMoves();

        // if both players passed back to back the game ends
        if (model.isHumanConsecutivePass() && model.isAIConsecutivePass()) {
            window.showLogMessage("Both players passed. Game Over!");
            model.setGameOver(true);
            declareWinner();
            return;
        }

        // forced pass for human
        if (model.isHumanTurn() && !humanHasMoves) {
            window.showLogMessage(">>> Human has no legal moves and is forced to pass.");
            model.setHumanConsecutivePass(true);
            model.switchTurn();
            triggerAITurn();
            return;
        }

        // forced pass for AI
        if (!model.isHumanTurn() && !aiHasMoves) {
            window.showLogMessage(">>> AI has no legal moves and is forced to pass.");
            model.setAIConsecutivePass(true);
            model.switchTurn();
        }
    }

    // runs the AI computation in the background using SwingWorker
    // SwingWorker keeps the UI responsive: doInBackground runs off the EDT,
    // done() runs back on the EDT so we can safely update Swing components
    private void triggerAITurn() {
        if (model.isGameOver() || model.isHumanTurn())
            return;
        window.showLogMessage("--- AI's Turn ---");

        new SwingWorker<Move, Void>() {
            @Override
            protected Move doInBackground() throws Exception {
                // pause briefly so the human can see the board before AI moves
                Thread.sleep(200);
                // computeMove + board mutation both happen off the EDT here
                return model.makeAIMove();
            }

            @Override
            protected void done() {
                // this runs on the EDT - safe to update Swing components
                try {
                    Move move = get();
                    if (move != null) {
                        model.setAIConsecutivePass(false);
                        window.showLogMessage("AI played Piece ID " + move.getPiece().getId()
                                + " at (" + move.getPosition().getRow() + ", " + move.getPosition().getColumn() + ")");
                    } else {
                        model.setAIConsecutivePass(true);
                        window.showLogMessage("AI passes.");
                    }
                    window.updateBoardState(model.getBoard());
                    window.updateScores(model.getHuman().getRemainingSquares(), model.getAi().getRemainingSquares());
                    model.switchTurn();
                    checkTurn();
                    // if human is still stuck, loop back to AI
                    if (!model.isGameOver() && !model.isHumanTurn()) {
                        triggerAITurn();
                    }
                } catch (Exception error) {
                    window.showLogMessage("AI error: " + error.getMessage());
                }
            }
        }.execute();
    }

    // shows who won and displays the result dialog
    private void declareWinner() {
        int humanSquares = model.getHuman().getRemainingSquares();
        int aiSquares = model.getAi().getRemainingSquares();
        String result = model.getWinnerMessage();

        window.showLogMessage("\n====== Game Over ======");
        window.showLogMessage("Human unplaced squares: " + humanSquares);
        window.showLogMessage("AI unplaced squares: " + aiSquares);
        window.showLogMessage(result);
        window.showGameOverDialog(humanSquares, aiSquares, result);
    }

    // clears the selected piece in both controller and view
    private void clearSelection() {
        selectedPiece = null;
        window.setSelectedPiece(null);
        window.clearSelectionHighlight();
    }
}
