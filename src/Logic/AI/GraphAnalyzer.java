package Logic.AI;

import Logic.Board;
import Logic.Piece;
import java.util.*;

public class GraphAnalyzer {

  // BFS shortest distance between Player 1's available corners and Player 2's
  // available corners
  public int getShortestPathDistance(Board board, int player1Id, int player2Id) {
    List<int[]> p1Corners = board.getAvailableCorners(player1Id);
    List<int[]> p2Corners = board.getAvailableCorners(player2Id);

    if (p1Corners.isEmpty() || p2Corners.isEmpty())
      return Integer.MAX_VALUE;

    int size = board.getSize();
    boolean[][] visited = new boolean[size][size];
    Queue<int[]> queue = new LinkedList<>();

    for (int[] corner : p1Corners) {
      queue.offer(new int[] { corner[0], corner[1], 0 });
      visited[corner[0]][corner[1]] = true;
    }

    while (!queue.isEmpty()) {
      int[] curr = queue.poll();
      int r = curr[0];
      int c = curr[1];
      int dist = curr[2];

      for (int[] p2c : p2Corners) {
        if (r == p2c[0] && c == p2c[1]) {
          return dist;
        }
      }

      int[][] dirs = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
      for (int[] d : dirs) {
        int nr = r + d[0];
        int nc = c + d[1];
        if (nr >= 0 && nr < size && nc >= 0 && nc < size) {
          if (!visited[nr][nc] && !board.getGrid()[nr][nc].isOccupied()) {
            visited[nr][nc] = true;
            queue.offer(new int[] { nr, nc, dist + 1 });
          }
        }
      }
    }
    return Integer.MAX_VALUE;
  }

  // Simulates placing a piece and returns how many NEW valid corners it creates
  public int calculateNewConnections(Board board, Piece p, int row, int col, int playerId) {
    int initialCorners = board.getAvailableCorners(playerId).size();

    // Deep copy board and simulate
    Board simBoard = new Board(board);
    simBoard.placePiece(p, row, col, playerId);

    int newCorners = simBoard.getAvailableCorners(playerId).size();

    return newCorners - initialCorners;
  }

  // Articulation Point check for a specific cell, against enemy reachable spaces
  public boolean isCutVertexForOpponent(Board board, int row, int col, int enemyId) {
    List<int[]> enemyCorners = board.getAvailableCorners(enemyId);
    if (enemyCorners.isEmpty())
      return false;

    // Base connected components without the piece placed
    int initialComponents = countEnemyComponents(board, enemyId);

    // Place a temporary block at (row, col) to represent the potential move taking
    // this cell
    Board simBoard = new Board(board);
    simBoard.getGrid()[row][col].setOccupied(true, 1); // Mock occupancy of the cell

    int newComponents = countEnemyComponents(simBoard, enemyId);
    return newComponents > initialComponents;
  }

  // Counts number of disconnected areas reachable by the enemy
  private int countEnemyComponents(Board board, int enemyId) {
    int size = board.getSize();
    boolean[][] visited = new boolean[size][size];
    int components = 0;

    List<int[]> startingPoints = board.getAvailableCorners(enemyId);

    for (int[] start : startingPoints) {
      int r = start[0];
      int c = start[1];
      if (!visited[r][c]) {
        components++;
        bfsMarkComponent(board, r, c, visited);
      }
    }
    return components;
  }

  private void bfsMarkComponent(Board board, int startR, int startC, boolean[][] visited) {
    int size = board.getSize();
    Queue<int[]> queue = new LinkedList<>();
    queue.offer(new int[] { startR, startC });
    visited[startR][startC] = true;

    while (!queue.isEmpty()) {
      int[] curr = queue.poll();
      int r = curr[0];
      int c = curr[1];

      int[][] dirs = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
      for (int[] d : dirs) {
        int nr = r + d[0];
        int nc = c + d[1];
        if (nr >= 0 && nr < size && nc >= 0 && nc < size) {
          if (!visited[nr][nc] && !board.getGrid()[nr][nc].isOccupied()) {
            visited[nr][nc] = true;
            queue.offer(new int[] { nr, nc });
          }
        }
      }
    }
  }
}
