package Logic.AI;

import Logic.Board;
import Logic.Piece;
import java.util.*;

// graph-based calculations: distances, connected areas, and articulation points
public class GraphAnalyzer {

    // finds the shortest path between player 1's corners and player 2's corners using BFS
    public int getShortestPathDistance(Board board, int player1Id, int player2Id) {
        List<int[]> player1Corners = board.getAvailableCorners(player1Id);
        List<int[]> player2Corners = board.getAvailableCorners(player2Id);

        if (player1Corners.isEmpty() || player2Corners.isEmpty())
            return Integer.MAX_VALUE;

        int size = board.getSize();
        boolean[][] visited = new boolean[size][size];
        Queue<int[]> queue = new LinkedList<>();

        // start BFS from all of player 1's corners at the same time
        for (int[] corner : player1Corners) {
            queue.offer(new int[]{corner[0], corner[1], 0});
            visited[corner[0]][corner[1]] = true;
        }

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int row = current[0];
            int column = current[1];
            int distance = current[2];

            // check if we reached any of player 2's corners
            for (int[] player2Corner : player2Corners) {
                if (row == player2Corner[0] && column == player2Corner[1]) {
                    return distance;
                }
            }

            int[][] directions = { {-1, 0}, {1, 0}, {0, -1}, {0, 1} };
            for (int[] direction : directions) {
                int neighborRow = row + direction[0];
                int neighborCol = column + direction[1];
                if (neighborRow >= 0 && neighborRow < size && neighborCol >= 0 && neighborCol < size) {
                    if (!visited[neighborRow][neighborCol] && !board.getGrid()[neighborRow][neighborCol].isOccupied()) {
                        visited[neighborRow][neighborCol] = true;
                        queue.offer(new int[]{neighborRow, neighborCol, distance + 1});
                    }
                }
            }
        }
        return Integer.MAX_VALUE;
    }

    // simulates placing a piece and returns how many new corners it opens up
    public int calculateNewConnections(Board board, Piece piece, int row, int column, int playerId) {
        int cornersBefore = board.getAvailableCorners(playerId).size();

        // use a copy of the board so the real one is not changed
        Board simulatedBoard = new Board(board);
        simulatedBoard.placePiece(piece, row, column, playerId);

        int cornersAfter = simulatedBoard.getAvailableCorners(playerId).size();
        return cornersAfter - cornersBefore;
    }

    // checks if blocking a specific cell would split the enemy's reachable area
    public boolean isCutVertexForOpponent(Board board, int row, int column, int enemyId) {
        List<int[]> enemyCorners = board.getAvailableCorners(enemyId);
        if (enemyCorners.isEmpty())
            return false;

        // count how many disconnected areas the enemy has now
        int componentsBefore = countEnemyComponents(board, enemyId);

        // block that cell and count again
        Board simulatedBoard = new Board(board);
        simulatedBoard.getGrid()[row][column].setOccupied(true, 1);

        int componentsAfter = countEnemyComponents(simulatedBoard, enemyId);

        // if blocking created more areas, this cell was a bridge
        return componentsAfter > componentsBefore;
    }

    // counts how many separate reachable areas the enemy has
    private int countEnemyComponents(Board board, int enemyId) {
        int size = board.getSize();
        boolean[][] visited = new boolean[size][size];
        int componentCount = 0;

        List<int[]> startingPoints = board.getAvailableCorners(enemyId);
        for (int[] startPoint : startingPoints) {
            int row = startPoint[0];
            int column = startPoint[1];
            if (!visited[row][column]) {
                componentCount++;
                bfsMarkComponent(board, row, column, visited);
            }
        }
        return componentCount;
    }

    // BFS flood fill to mark all cells reachable from a starting point
    private void bfsMarkComponent(Board board, int startRow, int startColumn, boolean[][] visited) {
        int size = board.getSize();
        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[]{startRow, startColumn});
        visited[startRow][startColumn] = true;

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int row = current[0];
            int column = current[1];

            int[][] directions = { {-1, 0}, {1, 0}, {0, -1}, {0, 1} };
            for (int[] direction : directions) {
                int neighborRow = row + direction[0];
                int neighborCol = column + direction[1];
                if (neighborRow >= 0 && neighborRow < size && neighborCol >= 0 && neighborCol < size) {
                    if (!visited[neighborRow][neighborCol] && !board.getGrid()[neighborRow][neighborCol].isOccupied()) {
                        visited[neighborRow][neighborCol] = true;
                        queue.offer(new int[]{neighborRow, neighborCol});
                    }
                }
            }
        }
    }
}
