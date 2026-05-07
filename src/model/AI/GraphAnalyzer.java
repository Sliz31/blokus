package model.AI;

import model.Board;
import model.Piece;
import java.util.*;

// graph-based calculations: distances, connected areas, articulation points
public class GraphAnalyzer {

    // BFS shortest path between player1's corners and player2's corners
    public int getShortestPathDistance(Board board, int player1Id, int player2Id) {
        List<int[]> player1Corners = board.getAvailableCorners(player1Id);
        List<int[]> player2Corners = board.getAvailableCorners(player2Id);
        if (player1Corners.isEmpty() || player2Corners.isEmpty()) return Integer.MAX_VALUE;

        int size = board.getSize();
        boolean[][] visited = new boolean[size][size];
        Queue<int[]> queue = new LinkedList<>();

        for (int[] corner : player1Corners) {
            queue.offer(new int[]{corner[0], corner[1], 0});
            visited[corner[0]][corner[1]] = true;
        }

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int row = current[0], column = current[1], distance = current[2];

            for (int[] player2Corner : player2Corners)
                if (row == player2Corner[0] && column == player2Corner[1]) return distance;

            int[][] directions = { {-1, 0}, {1, 0}, {0, -1}, {0, 1} };
            for (int[] direction : directions) {
                int neighborRow = row + direction[0];
                int neighborCol = column + direction[1];
                if (neighborRow >= 0 && neighborRow < size && neighborCol >= 0 && neighborCol < size)
                    if (!visited[neighborRow][neighborCol] && !board.getGrid()[neighborRow][neighborCol].isOccupied()) {
                        visited[neighborRow][neighborCol] = true;
                        queue.offer(new int[]{neighborRow, neighborCol, distance + 1});
                    }
            }
        }
        return Integer.MAX_VALUE;
    }

    // simulates placing a piece and returns how many new corners it opens up
    public int calculateNewConnections(Board board, Piece piece, int row, int column, int playerId) {
        int cornersBefore = board.getAvailableCorners(playerId).size();
        Board simulatedBoard = new Board(board);
        simulatedBoard.placePiece(piece, row, column, playerId);
        int cornersAfter = simulatedBoard.getAvailableCorners(playerId).size();
        return cornersAfter - cornersBefore;
    }

    // checks if blocking a cell would split the enemy's reachable area
    public boolean isCutVertexForOpponent(Board board, int row, int column, int enemyId) {
        if (board.getAvailableCorners(enemyId).isEmpty()) return false;
        int componentsBefore = countEnemyComponents(board, enemyId);
        Board simulatedBoard = new Board(board);
        simulatedBoard.getGrid()[row][column].setOccupied(true, 1);
        int componentsAfter = countEnemyComponents(simulatedBoard, enemyId);
        return componentsAfter > componentsBefore;
    }

    // counts how many separate reachable areas the enemy has
    private int countEnemyComponents(Board board, int enemyId) {
        int size = board.getSize();
        boolean[][] visited = new boolean[size][size];
        int componentCount = 0;
        for (int[] startPoint : board.getAvailableCorners(enemyId)) {
            int row = startPoint[0], column = startPoint[1];
            if (!visited[row][column]) {
                componentCount++;
                bfsMarkComponent(board, row, column, visited);
            }
        }
        return componentCount;
    }

    // BFS flood fill to mark all reachable cells from a starting point
    private void bfsMarkComponent(Board board, int startRow, int startColumn, boolean[][] visited) {
        int size = board.getSize();
        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[]{startRow, startColumn});
        visited[startRow][startColumn] = true;

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int row = current[0], column = current[1];
            int[][] directions = { {-1, 0}, {1, 0}, {0, -1}, {0, 1} };
            for (int[] direction : directions) {
                int neighborRow = row + direction[0];
                int neighborCol = column + direction[1];
                if (neighborRow >= 0 && neighborRow < size && neighborCol >= 0 && neighborCol < size)
                    if (!visited[neighborRow][neighborCol] && !board.getGrid()[neighborRow][neighborCol].isOccupied()) {
                        visited[neighborRow][neighborCol] = true;
                        queue.offer(new int[]{neighborRow, neighborCol});
                    }
            }
        }
    }
}
