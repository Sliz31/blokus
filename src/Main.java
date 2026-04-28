import Model.Board;
import Model.Cell;
import Model.Piece;
import Model.Player;
import Model.SimpleAIBot;
import View.CellPanel;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;
import java.util.Arrays;

public int FIRSTPLAYER_ID = 1;
public int SECONDPLAYER_ID = 2;

void refreshBoardUI(JPanel centerPanel, Board board, Player firstPlayer, SimpleAIBot aiPlayer, Color emptyCellColor, Color firstPlayerColor, Color secondPlayerColor) {
  centerPanel.removeAll();

  List<int[]> availableCorners = board.getAvailableCorners(firstPlayer);
  System.out.println(">>>> availableCorners.length: " + availableCorners.size());
  availableCorners.forEach(c -> System.out.println(Arrays.toString(c)));

  for (int i = 0; i < board.getSize(); i++) {
    for (int j = 0; j < board.getSize(); j++) {
      Cell cell = board.getGrid()[i][j];
      Color color;
      if (!cell.isOccupied()) {
        color = emptyCellColor;
      } else if (cell.getPlayerId() == FIRSTPLAYER_ID) {
        color = firstPlayerColor;
      } else {
        color = secondPlayerColor;
      }

      Color circle = null;
      for (int[] coordinates : availableCorners) {
        if (coordinates[0] == i && coordinates[1] == j) {
          circle = firstPlayerColor;
          break;
        }
      }

      CellPanel panel = new CellPanel(color, circle);

      int x = i;
      int y = j;
      panel.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          System.out.println("Clicked: " + x + "," + y);

          Piece humanPiece = null;
          for (Piece p : firstPlayer.getInventory()) {
            if (!p.isUsed()) {
              humanPiece = p;
              break;
            }
          }

          if (humanPiece != null && board.isValidMove(humanPiece, firstPlayer, x, y)) {
            // Ход человека
            board.setPiece(humanPiece, firstPlayer, x, y);
            humanPiece.setUsed(true);
            firstPlayer.setStepNumber(firstPlayer.getStepNumber() + 1);
            System.out.println("Человек сделал ход на: " + x + ", " + y);

            // Обновляем UI после хода человека
            refreshBoardUI(centerPanel, board, firstPlayer, aiPlayer, emptyCellColor, firstPlayerColor, secondPlayerColor);

            // Ход бота
            Object[] aiMove = aiPlayer.makeMove(board);
            if (aiMove != null) {
              Piece aiPiece = (Piece) aiMove[0];
              int aiX = (Integer) aiMove[1];
              int aiY = (Integer) aiMove[2];

              board.setPiece(aiPiece, aiPlayer, aiX, aiY);
              for (Piece p : aiPlayer.getInventory()) {
                if (p.getId() == aiPiece.getId() && !p.isUsed()) {
                  p.setUsed(true);
                  break;
                }
              }
              aiPlayer.setStepNumber(aiPlayer.getStepNumber() + 1);
              System.out.println("Бот сделал ход на: " + aiX + ", " + aiY);

              // Обновляем UI после хода бота
              refreshBoardUI(centerPanel, board, firstPlayer, aiPlayer, emptyCellColor, firstPlayerColor, secondPlayerColor);
            } else {
              System.out.println("Бот не нашел доступных ходов.");
            }
          } else {
            System.out.println("Невалидный ход человека!");
          }
        }
      });

      centerPanel.add(panel);
    }
  }

  centerPanel.revalidate();
  centerPanel.repaint();
}

void main() {
  Board board = new Board();
  Player firstPlayer = new Player(FIRSTPLAYER_ID, "FirstPlayer");
  SimpleAIBot aiPlayer = new SimpleAIBot(SECONDPLAYER_ID, "AI Bot");

  Color emptyCellColor = new Color(220, 220, 220);
  Color firstPlayerColor = new Color(255, 0, 0);
  Color secondPlayerColor = new Color(0, 0, 255);
  Color firstPlayerCornerColor = new Color(255, 219, 0);
  Color secondPlayerCornerColor = new Color(0, 255, 0);

  SwingUtilities.invokeLater(() -> {
    JFrame frame = new JFrame("Мое первое окно");
    frame.setSize(1000, 1000);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setResizable(false);

    frame.setLayout(new BorderLayout());

    JPanel bottomPanel = new JPanel();
    bottomPanel.setBackground(new Color(140, 140, 140));
    bottomPanel.setPreferredSize(new Dimension(1000, 200));
    bottomPanel.add(new JButton("Старт"));
    bottomPanel.add(new JButton("Стоп"));
    bottomPanel.add(new JButton("Сброс"));

    JTextArea textArea = new JTextArea("Поле с прокруткой...\n");
    textArea.setBackground(new Color(180, 180, 180));
    JScrollPane rightScrollPane = new JScrollPane(textArea);
    rightScrollPane.setPreferredSize(new Dimension(200, 0));

    JPanel centerPanel = new JPanel();
    centerPanel.setLayout(new GridLayout(14, 14));
    centerPanel.setBackground(emptyCellColor);

    // Первоначальная отрисовка доски
    refreshBoardUI(centerPanel, board, firstPlayer, aiPlayer, emptyCellColor, firstPlayerColor, secondPlayerColor);

    frame.add(bottomPanel, BorderLayout.SOUTH);
    frame.add(rightScrollPane, BorderLayout.EAST);
    frame.add(centerPanel, BorderLayout.CENTER);

    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  });
}