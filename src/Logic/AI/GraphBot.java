package Logic.AI;

import Logic.Board;
import Logic.Player;
import Logic.Piece;
import java.util.List;

// the AI player - uses a finite state machine to choose between expansion, cut, and fill strategies
public class GraphBot extends Player {
    private BotState currentState;
    private GraphAnalyzer analyzer;

    public GraphBot(int id, String name) {
        super(id, name);
        this.currentState = new ExpansionState();
        this.analyzer = new GraphAnalyzer();
    }

    public BotState getCurrentState() {
        return currentState;
    }

    // called once per AI turn: check state, pick a move, apply it
    public void makeMove(Board board, Player enemy) {
        // check if we should switch to a different strategy
        BotState nextState = currentState.nextState(board, this, enemy, analyzer);
        if (nextState != null && nextState != currentState) {
            System.out.println("AI switching from " + currentState.getClass().getSimpleName() + " to " + nextState.getClass().getSimpleName());
            currentState = nextState;
        }

        // ask the current state to pick a move
        Move move = currentState.decideMove(board, this, enemy, analyzer);

        // apply the move if one was found
        if (move != null) {
            System.out.println("AI played Piece ID " + move.getPiece().getId() + " at (" + move.getRow() + ", " + move.getCol() + ")");
            board.placePiece(move.getPiece(), move.getRow(), move.getCol(), this.getId());

            // mark that piece as used in inventory
            for (Piece inventoryPiece : getInventory()) {
                if (inventoryPiece.getId() == move.getPiece().getId()) {
                    inventoryPiece.setUsed(true);
                    break;
                }
            }
            if (isFirstMove()) {
                setFirstMove(false);
            }
        } else {
            System.out.println("AI passes.");
        }
    }

    // generates every legal move for a player by trying all 8 variants of each available piece
    public static List<Move> getAllLegalMoves(Board board, Player player) {
        java.util.List<Move> validMoves = new java.util.ArrayList<>();
        List<Piece> availablePieces = player.getAvailablePieces();

        for (Piece basePiece : availablePieces) {
            // try all 8 geometric states (4 rotations x 2 mirror groups) from the cache
            for (int variant = 0; variant < 8; variant++) {
                Piece currentPiece = new Piece(basePiece.getId(), variant);

                for (int row = 0; row < board.getSize(); row++) {
                    for (int column = 0; column < board.getSize(); column++) {
                        if (board.isValidMove(currentPiece, row, column, player)) {
                            validMoves.add(new Move(currentPiece, row, column));
                        }
                    }
                }
            }
        }
        return validMoves;
    }
}
