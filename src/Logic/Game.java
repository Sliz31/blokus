package Logic;

import Logic.AI.GraphBot;
import GUI.BlokusWindow;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

public class Game {
  private Board board;
  private boolean isGameOver;
  private Player human;
  private GraphBot ai;
  private Player currentPlayer;
  private BlokusWindow window;

  private Piece selectedPiece = null;

  // Pass tracking for endgame conditions
  private boolean humanConsecutivePass = false;
  private boolean aiConsecutivePass = false;

  public Game() {
    this.board = new Board();
    this.human = new Player(1, "Human Player");
    this.ai = new GraphBot(2, "GraphBot AI");
    this.currentPlayer = human;

    // Launch GUI
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

  public Board getBoard() {
    return board;
  }

  public Player getHuman() {
    return human;
  }

  public GraphBot getAi() {
    return ai;
  }

  public boolean isGameOver() {
    return isGameOver;
  }

  public Piece getSelectedPiece() {
    return selectedPiece;
  }

  public void setSelectedPiece(Piece piece) {
    this.selectedPiece = piece;
  }

  public int[][] getSelectedPieceMatrix() {
    return (selectedPiece != null) ? selectedPiece.getShape() : null;
  }

  private void checkTurn() {
    if (isGameOver)
      return;

    boolean humanHasMoves = !GraphBot.getAllLegalMoves(board, human).isEmpty();
    boolean aiHasMoves = !GraphBot.getAllLegalMoves(board, ai).isEmpty();

    if (humanConsecutivePass && aiConsecutivePass) {
      window.logMessage("Both players passed simultaneously. End Game!");
      isGameOver = true;
      declareWinner();
      return;
    }

    if (currentPlayer == human && !humanHasMoves) {
      window.logMessage(">>> Human has no legal moves and is forced to pass.");
      humanConsecutivePass = true;
      switchTurn();
      triggerAITurn();
      return;
    }

    if (currentPlayer == ai && !aiHasMoves) {
      window.logMessage(">>> AI has no legal moves and is forced to pass.");
      aiConsecutivePass = true;
      switchTurn();
      return;
    }
  }

  public void humanPass() {
    if (isGameOver || currentPlayer != human)
      return;
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

  public void handleCellClick(int row, int col) {
    if (isGameOver || currentPlayer != human)
      return;

    if (selectedPiece == null) {
      window.logMessage("Please select a piece from your inventory first.");
      return;
    }

    if (board.isValidMove(selectedPiece, row, col, human)) {
      board.placePiece(selectedPiece, row, col, human.getId());
      selectedPiece.setUsed(true);
      if (human.isFirstMove())
        human.setFirstMove(false);

      humanConsecutivePass = false; // Reset block logic

      window.logMessage("Human played Piece ID " + selectedPiece.getId() + " at (" + row + ", " + col + ")");
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

  private void triggerAITurn() {
    if (isGameOver || currentPlayer != ai)
      return;
    window.logMessage("--- AI's Turn ---");

    new Thread(() -> {
      try {
        Thread.sleep(200);
      } catch (Exception e) {
      }

      int aiRemainingBefore = ai.getRemainingSquares();
      ai.makeMove(board, human);

      SwingUtilities.invokeLater(() -> {
        if (ai.getRemainingSquares() < aiRemainingBefore) {
          aiConsecutivePass = false; // Reset ai pass tracker on successful placement
        } else {
          aiConsecutivePass = true;
        }

        window.updateBoard(board);
        switchTurn();
        checkTurn();

        // If it switched back to AI because human is locked physically forcing loop
        if (!isGameOver && currentPlayer == ai) {
          triggerAITurn();
        }
      });
    }).start();
  }

  private void switchTurn() {
    currentPlayer = (currentPlayer == human) ? ai : human;
  }

  private void declareWinner() {
    window.logMessage("\n====== Game Over ======");
    int humanSquares = human.getRemainingSquares();
    int aiSquares = ai.getRemainingSquares();

    window.logMessage("Human unplaced squares: " + humanSquares);
    window.logMessage("AI unplaced squares: " + aiSquares);

    String resultMsg;
    if (humanSquares < aiSquares) {
      resultMsg = "*** HUMAN WINS! ***";
    } else if (aiSquares < humanSquares) {
      resultMsg = "*** AI WINS! ***";
    } else {
      resultMsg = "*** IT'S A TIE! ***";
    }
    window.logMessage(resultMsg);

    JOptionPane.showMessageDialog(window,
        "Human unplaced squares: " + humanSquares + "\nAI unplaced squares: " + aiSquares + "\n\n" + resultMsg,
        "Game Over",
        JOptionPane.INFORMATION_MESSAGE);
  }
}