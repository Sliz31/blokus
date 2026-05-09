package model;

import model.AI.GraphBot;
import model.AI.Move;

// pure game state - no Swing, no GUI, no window references allowed here
// this class only stores data and applies game rules
public class GameModel {

    private Board board;
    private Player human;
    private GraphBot ai;
    private Player currentPlayer;
    private boolean isGameOver;
    private boolean humanConsecutivePass;
    private boolean aiConsecutivePass;

    public GameModel() {
        board = new Board();
        human = new Player(1, "Human Player");
        ai = new GraphBot(2, "GraphBot AI");
        currentPlayer = human;
        isGameOver = false;
        humanConsecutivePass = false;
        aiConsecutivePass = false;
    }

    // getters - controller reads these to decide what to show
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

    public boolean isHumanTurn() {
        return currentPlayer == human;
    }

    public boolean isHumanConsecutivePass() {
        return humanConsecutivePass;
    }

    public boolean isAIConsecutivePass() {
        return aiConsecutivePass;
    }

    public void setGameOver(boolean value) {
        isGameOver = value;
    }

    public void setHumanConsecutivePass(boolean value) {
        humanConsecutivePass = value;
    }

    public void setAIConsecutivePass(boolean value) {
        aiConsecutivePass = value;
    }

    // switch the turn between human and AI
    public void switchTurn() {
        currentPlayer = isHumanTurn() ? ai : human;
    }

    // try to place a piece for the human player - returns true if move was valid
    public boolean tryPlaceHumanPiece(Piece piece, Position position) {
        if (!board.isValidMove(piece, position, human))
            return false;

        board.placePiece(piece, position, human.getId());
        piece.setUsed(true);
        if (human.isFirstMove())
            human.setFirstMove(false);
        humanConsecutivePass = false;
        return true;
    }

    // runs AI computation and applies the resulting move to the board
    // safe to call off the EDT since it only touches data, not Swing components
    public Move makeAIMove() {
        Move move = ai.computeMove(board, human);
        if (move != null) {
            board.placePiece(move.getPiece(), move.getPosition(), ai.getId());
            // mark the piece as used in the AI's inventory
            for (Piece inventoryPiece : ai.getInventory()) {
                if (inventoryPiece.getId() == move.getPiece().getId()) {
                    inventoryPiece.setUsed(true);
                    break;
                }
            }
            if (ai.isFirstMove())
                ai.setFirstMove(false);
        }
        return move;
    }

    // checks if the human still has any legal move available
    public boolean humanHasLegalMoves() {
        return !GraphBot.getAllLegalMoves(board, human).isEmpty();
    }

    // checks if the AI still has any legal move available
    public boolean aiHasLegalMoves() {
        return !GraphBot.getAllLegalMoves(board, ai).isEmpty();
    }

    // builds the result string based on remaining squares (fewer = better)
    public String getWinnerMessage() {
        int humanSquares = human.getRemainingSquares();
        int aiSquares = ai.getRemainingSquares();
        if (humanSquares < aiSquares)
            return "*** HUMAN WINS! ***";
        if (aiSquares < humanSquares)
            return "*** AI WINS! ***";
        return "*** IT'S A TIE! ***";
    }
}
