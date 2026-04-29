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
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;

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

void showGameOverDialog(JFrame frame, Player human, SimpleAIBot ai) {
  int humanPenalty = 0;
  for (Piece p : human.getInventory()) {
    if (!p.isUsed()) {
      humanPenalty += p.getSize();
    }
  }

  int aiPenalty = 0;
  for (Piece p : ai.getInventory()) {
    if (!p.isUsed()) {
      aiPenalty += p.getSize();
    }
  }

  String resultMessage;
  if (humanPenalty < aiPenalty) {
    resultMessage = "You win!";
  } else if (aiPenalty < humanPenalty) {
    resultMessage = "You lose!";
  } else {
    resultMessage = "Draw!";
  }

  String finalMessage = "The game is over!\n\n" +
      "Your penalty points: " + humanPenalty + "\n" +
      "Bot penalty points: " + aiPenalty + "\n\n" +
      resultMessage;

  JOptionPane.showMessageDialog(frame, finalMessage, "Game Over", JOptionPane.INFORMATION_MESSAGE);
}

void refreshInventoryUI(JPanel rightPanel, Player player, Piece[] selectedPiece) {
  rightPanel.removeAll();

  for (Piece p : player.getInventory()) {
    if (!p.isUsed()) {
      JButton btn = new JButton("Piece " + p.getId());
      btn.addActionListener(e -> {
        selectedPiece[0] = p;
        System.out.println("Piece selected " + p.getId());
      });
      rightPanel.add(btn);
    }
  }

  rightPanel.revalidate();
  rightPanel.repaint();
}

void refreshBoardUI(JFrame frame, JPanel centerPanel, Board board, Player firstPlayer, SimpleAIBot aiPlayer, Color emptyCellColor, Color firstPlayerColor, Color secondPlayerColor, JPanel rightPanel, Piece[] selectedPiece, boolean[] consecutivePasses) {
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

          Piece humanPiece = selectedPiece[0];

          if (humanPiece != null && board.isValidMove(humanPiece, firstPlayer, x, y)) {
            // Ход человека
            board.setPiece(humanPiece, firstPlayer, x, y);
            humanPiece.setUsed(true);
            firstPlayer.setStepNumber(firstPlayer.getStepNumber() + 1);
            System.out.println("The person made a move to: " + x + ", " + y);
            
            consecutivePasses[0] = false; // Человек сделал успешный ход

            // Очищаем выбор и обновляем инвентарь
            selectedPiece[0] = null;
            refreshInventoryUI(rightPanel, firstPlayer, selectedPiece);

            // Обновляем UI после хода человека
            refreshBoardUI(frame, centerPanel, board, firstPlayer, aiPlayer, emptyCellColor, firstPlayerColor, secondPlayerColor, rightPanel, selectedPiece, consecutivePasses);

            // Ход бота
            Object[] aiMove = aiPlayer.makeMove(board);
            if (aiMove != null) {
              Piece aiPiece = (Piece) aiMove[0];
              int aiX = (Integer) aiMove[1];
              int aiY = (Integer) aiMove[2];

              board.setPiece(aiPiece, aiPlayer, aiX, aiY);
              // Находим эту фигуру в инвентаре бота и помечаем как использованную
              for (Piece p : aiPlayer.getInventory()) {
                if (p.getId() == aiPiece.getId() && !p.isUsed()) {
                  p.setUsed(true);
                  break;
                }
              }
              aiPlayer.setStepNumber(aiPlayer.getStepNumber() + 1);
              System.out.println("The bot made a move to: " + aiX + ", " + aiY);
              
              consecutivePasses[1] = false; // Бот сделал успешный ход

              // Обновляем UI после хода бота
              refreshBoardUI(frame, centerPanel, board, firstPlayer, aiPlayer, emptyCellColor, firstPlayerColor, secondPlayerColor, rightPanel, selectedPiece, consecutivePasses);
            } else {
              System.out.println("The bot couldn't find any available moves.");
              consecutivePasses[1] = true; // Бот пропускает ход
            }
            
            // Проверка на окончание игры после обоих ходов
            if (consecutivePasses[0] && consecutivePasses[1]) {
              showGameOverDialog(frame, firstPlayer, aiPlayer);
            }
            
          } else {
            System.out.println("That's an illegal move!");
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

  Piece[] selectedPiece = {null};
  boolean[] consecutivePasses = {false, false};

  Color emptyCellColor = new Color(220, 220, 220);
  Color firstPlayerColor = new Color(255, 0, 0);
  Color secondPlayerColor = new Color(0, 0, 255);
  Color firstPlayerCornerColor = new Color(255, 219, 0);
  Color secondPlayerCornerColor = new Color(0, 255, 0);

  SwingUtilities.invokeLater(() -> {
    JFrame frame = new JFrame("My First Window");
    frame.setSize(1000, 1000);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setResizable(false);

    frame.setLayout(new BorderLayout());

    JPanel bottomPanel = new JPanel();
    bottomPanel.setBackground(new Color(140, 140, 140));
    bottomPanel.setPreferredSize(new Dimension(1000, 200));
    
    JButton rotateBtn = new JButton("Rotate");
    rotateBtn.addActionListener(e -> {
      if (selectedPiece[0] != null) {
        selectedPiece[0].rotate();
        System.out.println("The piece is rotated");
      }
    });

    JButton flipBtn = new JButton("Flip");
    flipBtn.addActionListener(e -> {
      if (selectedPiece[0] != null) {
        selectedPiece[0].flip();
        System.out.println("The piece is flipped");
      }
    });
    
    JPanel rightPanel = new JPanel();
    rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
    JScrollPane rightScrollPane = new JScrollPane(rightPanel);
    rightScrollPane.setPreferredSize(new Dimension(200, 0));

    refreshInventoryUI(rightPanel, firstPlayer, selectedPiece);

    JPanel centerPanel = new JPanel();
    centerPanel.setLayout(new GridLayout(14, 14));
    centerPanel.setBackground(emptyCellColor);

    JButton passBtn = new JButton("Skip a turn");
    passBtn.addActionListener(e -> {
      System.out.println("A player skips a turn");
      consecutivePasses[0] = true;
      selectedPiece[0] = null;
      refreshInventoryUI(rightPanel, firstPlayer, selectedPiece);

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
        System.out.println("The bot made a move to: " + aiX + ", " + aiY);

        consecutivePasses[1] = false;
      } else {
        System.out.println("The bot couldn't find any available moves.");
        consecutivePasses[1] = true;
      }

      refreshBoardUI(frame, centerPanel, board, firstPlayer, aiPlayer, emptyCellColor, firstPlayerColor, secondPlayerColor, rightPanel, selectedPiece, consecutivePasses);

      if (consecutivePasses[0] && consecutivePasses[1]) {
        showGameOverDialog(frame, firstPlayer, aiPlayer);
      }
    });

    bottomPanel.add(rotateBtn);
    bottomPanel.add(flipBtn);
    bottomPanel.add(passBtn);

    // Первоначальная отрисовка доски
    refreshBoardUI(frame, centerPanel, board, firstPlayer, aiPlayer, emptyCellColor, firstPlayerColor, secondPlayerColor, rightPanel, selectedPiece, consecutivePasses);

    frame.add(bottomPanel, BorderLayout.SOUTH);
    frame.add(rightScrollPane, BorderLayout.EAST);
    frame.add(centerPanel, BorderLayout.CENTER);

    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  });
}