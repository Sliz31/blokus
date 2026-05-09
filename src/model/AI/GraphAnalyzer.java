package model.AI;

import model.Board;
import model.Piece;
import model.Position;
import java.util.*;

// graph-based calculations: distances, connected areas, articulation points
public class GraphAnalyzer {

  private static class Node {
    Position position;
    int distance;

    Node(Position position, int distance) {
      this.position = position;
      this.distance = distance;
    }
  }

  // BFS shortest path between player1's corners and player2's corners
  public int getShortestPathDistance(Board board, int player1Id, int player2Id) {
    List<Position> player1Corners = board.getAvailableCorners(player1Id);
    List<Position> player2Corners = board.getAvailableCorners(player2Id);
    if (player1Corners.isEmpty() || player2Corners.isEmpty()) {
      return Integer.MAX_VALUE;
    }

    int size = board.getSize();
    boolean[][] visited = new boolean[size][size];
    Queue<Node> queue = new LinkedList<>();

    for (Position corner : player1Corners) {
      queue.offer(new Node(corner, 0));
      visited[corner.getRow()][corner.getColumn()] = true;
    }

    while (!queue.isEmpty()) {
      Node current = queue.poll();
      int row = current.position.getRow();
      int column = current.position.getColumn();
      int distance = current.distance;

      for (Position player2Corner : player2Corners) {
        if (row == player2Corner.getRow() && column == player2Corner.getColumn()) {
          return distance;
        }
      }

      int[][] directions = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
      for (int[] direction : directions) {
        int neighborRow = row + direction[0];
        int neighborCol = column + direction[1];
        if (neighborRow >= 0 && neighborRow < size && neighborCol >= 0 && neighborCol < size) {
          if (!visited[neighborRow][neighborCol] && !board.getGrid()[neighborRow][neighborCol].isOccupied()) {
            visited[neighborRow][neighborCol] = true;
            queue.offer(new Node(new Position(neighborRow, neighborCol), distance + 1));
          }
        }
      }
    }
    return Integer.MAX_VALUE;
  }

  // simulates placing a piece and returns how many new corners it opens up
  public int calculateNewConnections(Board board, Piece piece, Position position, int playerId) {
    int cornersBefore = board.getAvailableCorners(playerId).size();
    Board simulatedBoard = new Board(board);
    simulatedBoard.placePiece(piece, position, playerId);
    int cornersAfter = simulatedBoard.getAvailableCorners(playerId).size();
    return cornersAfter - cornersBefore;
  }

  // checks if blocking a cell would split the enemy's reachable area
  public boolean isCutVertexForOpponent(Board board, Position position, int enemyId) {
    if (board.getAvailableCorners(enemyId).isEmpty()) {
      return false;
    }
    int componentsBefore = countEnemyComponents(board, enemyId);
    Board simulatedBoard = new Board(board);
    simulatedBoard.getGrid()[position.getRow()][position.getColumn()].setOccupied(true, 1);
    int componentsAfter = countEnemyComponents(simulatedBoard, enemyId);
    return componentsAfter > componentsBefore;
  }

  // counts how many separate reachable areas the enemy has
  private int countEnemyComponents(Board board, int enemyId) {
    int size = board.getSize();
    boolean[][] visited = new boolean[size][size];
    int componentCount = 0;
    for (Position startPoint : board.getAvailableCorners(enemyId)) {
      int row = startPoint.getRow(), column = startPoint.getColumn();
      if (!visited[row][column]) {
        componentCount++;
        bfsMarkComponent(board, startPoint, visited);
      }
    }
    return componentCount;
  }

  // BFS flood fill to mark all reachable cells from a starting point
  private void bfsMarkComponent(Board board, Position startPosition, boolean[][] visited) {
    int size = board.getSize();
    Queue<Position> queue = new LinkedList<>();
    queue.offer(startPosition);
    visited[startPosition.getRow()][startPosition.getColumn()] = true;

    while (!queue.isEmpty()) {
      Position current = queue.poll();
      int row = current.getRow(), column = current.getColumn();
      int[][] directions = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
      for (int[] direction : directions) {
        int neighborRow = row + direction[0];
        int neighborCol = column + direction[1];
        if (neighborRow >= 0 && neighborRow < size && neighborCol >= 0 && neighborCol < size) {
          if (!visited[neighborRow][neighborCol] && !board.getGrid()[neighborRow][neighborCol].isOccupied()) {
            visited[neighborRow][neighborCol] = true;
            queue.offer(new Position(neighborRow, neighborCol));
          }
        }
      }
    }
  }
}
