package Logic;

import Logic.AI.GraphBot;
import GUI.BlokusWindow;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

// main game controller - connects the board, players, and GUI together
public class Game {
    private Board board;
    private boolean isGameOver;
    private Player human;
    private GraphBot ai;
    private Player currentPlayer;
    private BlokusWindow window;

    // the piece the human has selected from their inventory
    private Piece selectedPiece = null;

    // track consecutive passes so we know when both players are stuck
    private boolean humanConsecutivePass = false;
    private boolean aiConsecutivePass = false;

    public Game() {
        this.board = new Board();
        this.human = new Player(1, "Human Player");
        this.ai = new GraphBot(2, "GraphBot AI");
        this.currentPlayer = human;

        // open the window on the Swing thread
        SwingUtilities.invokeLater(() -> {
            window = new BlokusWindow(this);
            window.setVisible(true);
            window.logMessage("====== Blokus Game Started ======");
            window.logMessage("Player 1 (Blue) is Human");
            window.logMessage("Player 2 (Red) is GraphBot AI");
            window.updateBoard(board);
            window.updateInventory(human.getAvailablePieces());
            checkTurn();
        });
    }

    public Board getBoard()          { return board; }
    public Player getHuman()         { return human; }
    public GraphBot getAi()          { return ai; }
    public boolean isGameOver()      { return isGameOver; }
    public Piece getSelectedPiece()  { return selectedPiece; }
    public void setSelectedPiece(Piece piece) { this.selectedPiece = piece; }

    // checks whose turn it is and handles forced passes
    private void checkTurn() {
        if (isGameOver) return;

        boolean humanHasMoves = !GraphBot.getAllLegalMoves(board, human).isEmpty();
        boolean aiHasMoves = !GraphBot.getAllLegalMoves(board, ai).isEmpty();

        // if both players passed back to back the game is over
        if (humanConsecutivePass && aiConsecutivePass) {
            window.logMessage("Both players passed simultaneously. End Game!");
            isGameOver = true;
            declareWinner();
            return;
        }

        // force pass if human has no moves
        if (currentPlayer == human && !humanHasMoves) {
            window.logMessage(">>> Human has no legal moves and is forced to pass.");
            humanConsecutivePass = true;
            switchTurn();
            triggerAITurn();
            return;
        }

        // force pass if AI has no moves
        if (currentPlayer == ai && !aiHasMoves) {
            window.logMessage(">>> AI has no legal moves and is forced to pass.");
            aiConsecutivePass = true;
            switchTurn();
            return;
        }
    }

    // called when the human clicks the pass button
    public void humanPass() {
        if (isGameOver || currentPlayer != human) return;
        window.logMessage("Human voluntarily passed.");
        humanConsecutivePass = true;
        setSelectedPiece(null);
        window.clearSelection();
        switchTurn();
        checkTurn();

        if (!isGameOver && currentPlayer == ai) {
            triggerAITurn();
        }
    }

    // called when the human clicks a cell on the board
    public void handleCellClick(int row, int column) {
        if (isGameOver || currentPlayer != human) return;

        if (selectedPiece == null) {
            window.logMessage("Please select a piece from your inventory first.");
            return;
        }

        if (board.isValidMove(selectedPiece, row, column, human)) {
            board.placePiece(selectedPiece, row, column, human.getId());
            selectedPiece.setUsed(true);
            if (human.isFirstMove()) human.setFirstMove(false);

            humanConsecutivePass = false;

            window.logMessage("Human played Piece ID " + selectedPiece.getId() + " at (" + row + ", " + column + ")");
            setSelectedPiece(null);
            window.clearSelection();
            window.updateBoard(board);
            window.updateInventory(human.getAvailablePieces());

            switchTurn();
            checkTurn();

            if (!isGameOver && currentPlayer == ai) {
                triggerAITurn();
            }
        } else {
            window.logMessage("Invalid move! Fails Blokus rules.");
        }
    }

    // runs the AI move on a background thread so the UI doesn't freeze
    private void triggerAITurn() {
        if (isGameOver || currentPlayer != ai) return;
        window.logMessage("--- AI's Turn ---");

        new Thread(() -> {
            try { Thread.sleep(200); } catch (Exception ignored) {}

            int aiSquaresBefore = ai.getRemainingSquares();
            ai.makeMove(board, human);

            SwingUtilities.invokeLater(() -> {
                // if the AI placed something, reset its pass counter
                if (ai.getRemainingSquares() < aiSquaresBefore) {
                    aiConsecutivePass = false;
                } else {
                    aiConsecutivePass = true;
                }

                window.updateBoard(board);
                switchTurn();
                checkTurn();

                // if human is stuck the loop continues with the AI again
                if (!isGameOver && currentPlayer == ai) {
                    triggerAITurn();
                }
            });
        }).start();
    }

    private void switchTurn() {
        currentPlayer = (currentPlayer == human) ? ai : human;
    }

    // shows who won based on fewest remaining squares
    private void declareWinner() {
        window.logMessage("\n====== Game Over ======");
        int humanSquares = human.getRemainingSquares();
        int aiSquares = ai.getRemainingSquares();

        window.logMessage("Human unplaced squares: " + humanSquares);
        window.logMessage("AI unplaced squares: " + aiSquares);

        String result;
        if (humanSquares < aiSquares) {
            result = "*** HUMAN WINS! ***";
        } else if (aiSquares < humanSquares) {
            result = "*** AI WINS! ***";
        } else {
            result = "*** IT'S A TIE! ***";
        }
        window.logMessage(result);

        JOptionPane.showMessageDialog(window,
            "Human unplaced squares: " + humanSquares + "\nAI unplaced squares: " + aiSquares + "\n\n" + result,
            "Game Over",
            JOptionPane.INFORMATION_MESSAGE);
    }
}