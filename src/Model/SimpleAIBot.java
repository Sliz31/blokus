package Model;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SimpleAIBot extends Player {

  // Состояния конечного автомата (FSM)
  public enum State {
    EARLY_GAME, MID_GAME, LATE_GAME
  }

  private State currentState;

  public SimpleAIBot(int id, String name) {
    super(id, name);
    this.currentState = State.EARLY_GAME;
  }

  // Метод возвращает массив объектов: [Piece, X, Y] для хода
  public Object[] makeMove(Board board) {
    List<Piece> inventory = getInventory();
    if (inventory == null || inventory.isEmpty()) return null;

    switch (currentState) {
      case EARLY_GAME:
        // Логика первого хода: берем самую большую деталь
        Object[] startMove = findFirstValidMove(board, inventory);
        if (startMove != null) {
          currentState = State.MID_GAME; // Смена состояния FSM
          return startMove;
        }
        break;

      case MID_GAME:
        // Ищем ход с максимальным результатом BFS
        Object[] bestMidMove = null;
        int maxScore = -1;

        for (Piece p : inventory) {
          if (p.isUsed()) {
            continue; // Скипаем юзаные фигуры
          }
          if (p.getShape().length < 3) {
            continue; // Используем ТОЛЬКО крупные детали
          }

          for (int x = 0; x < 14; x++) {
            for (int y = 0; y < 14; y++) {
              Piece testPiece = new Piece(p.getId());
              
              for (int flip = 0; flip < 2; flip++) {
                for (int rot = 0; rot < 4; rot++) {
                  if (board.isValidMove(testPiece, this, x, y)) {
                    // Используем BFS для оценки score (балов)
                    int score = evaluateTerritoryBFS(board, x, y);
                    if (score > maxScore) {
                      maxScore = score;
                      // Делаем копию для применения на доску
                      Piece winningPiece = new Piece(p.getId());
                      if (flip == 1) {
                        winningPiece.flip();
                      }
                      for (int r = 0; r < rot; r++) {
                        winningPiece.rotate();
                      }
                      bestMidMove = new Object[]{winningPiece, x, y};
                    }
                  }
                  testPiece.rotate();
                }
                testPiece.flip();
              }
            }
          }
        }

        if (bestMidMove != null) {
          return bestMidMove;
        } else {
          currentState = State.LATE_GAME; // Если крупных ходов нет, то меняем состояние
        }

      case LATE_GAME:
        // Заполняем свободные места мелкими фигурами
        return findFirstValidMove(board, inventory);
    }
    return null;
  }

  // BFS
  // Считает количество пустых клеток вокруг потенциального хода в радиусе 3 шагов
  private int evaluateTerritoryBFS(Board board, int startX, int startY) {
    Queue<int[]> queue = new LinkedList<>();
    boolean[][] visited = new boolean[14][14];

    queue.add(new int[]{startX, startY});
    visited[startX][startY] = true;

    int emptyCellsCount = 0;
    int maxDepth = 3;
    int currentDepth = 0;

    // Векторы направлений: вверх, вниз, влево, вправо
    int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    while (!queue.isEmpty() && currentDepth < maxDepth) {
      int levelSize = queue.size();
      for (int i = 0; i < levelSize; i++) {
        int[] curr = queue.poll();

        for (int[] dir : directions) {
          int nx = curr[0] + dir[0];
          int ny = curr[1] + dir[1];

          // Проверка границ доски и посещенности
          if (nx >= 0 && nx < 14 && ny >= 0 && ny < 14 && !visited[nx][ny]) {
            visited[nx][ny] = true;

            // Если клетка пустая, добавляем в очередь.
            if (!board.getGrid()[nx][ny].isOccupied()) {
              queue.add(new int[]{nx, ny});
              emptyCellsCount++;
            }
          }
        }
      }
      currentDepth++;
    }
    return emptyCellsCount;
  }

  // Вспомогательный метод для прямолинейного поиска хода
  private Object[] findFirstValidMove(Board board, List<Piece> inventory) {
    for (Piece p : inventory) {
      if (p.isUsed()) {
        continue; // Пропускаем использованные
      }
      
      for (int x = 0; x < 14; x++) {
        for (int y = 0; y < 14; y++) {
          // Создаем копию фигуры для вращений
          Piece testPiece = new Piece(p.getId());
          
          for (int flip = 0; flip < 2; flip++) {
            for (int rot = 0; rot < 4; rot++) {
              if (board.isValidMove(testPiece, this, x, y)) {
                Piece winningPiece = new Piece(p.getId());
                if (flip == 1) {
                  winningPiece.flip();
                }
                for (int r = 0; r < rot; r++) {
                  winningPiece.rotate();
                }
                return new Object[]{winningPiece, x, y};
              }
              testPiece.rotate();
            }
            testPiece.flip();
          }
        }
      }
    }
    return null;
  }
}