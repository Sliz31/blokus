package Logic;

// one square on the board - knows if it's taken and by whom
public class Cell {
    private boolean isOccupied;

    // position on the board
    private int column;
    private int row;

    // 0 = empty, 1 = player 1, 2 = player 2
    private int playerId;

    public Cell(int row, int column) {
        this.row = row;
        this.column = column;
        this.isOccupied = false;
        this.playerId = 0;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setOccupied(boolean occupied, int playerId) {
        this.isOccupied = occupied;
        this.playerId = playerId;
    }
}