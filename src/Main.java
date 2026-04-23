import Model.Board;
import Model.Cell;
import Model.Piece;
import Model.Player;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.GridLayout;

int FIRSTPLAYER_ID = 1;
int SECONDPLAYER_ID = 2;

void main() {
  Board board = new Board();
  Player firstPlayer = new Player(FIRSTPLAYER_ID, "FirstPlayer");
  Player secondPlayer = new Player(SECONDPLAYER_ID, "SecondPlayer");
  board.setPiece(new Piece(5), firstPlayer, 5, 7);
  board.setPiece(new Piece(7), secondPlayer, 12, 12);

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

    // Устанавливаем макет BorderLayout для точного контроля размеров
    frame.setLayout(new BorderLayout());

    // 1. Нижняя часть (Юг): высота 200, ширина растягивается на все 1000
    JPanel bottomPanel = new JPanel();
    bottomPanel.setBackground(new Color(140, 140, 140)); // Темно-серый
    bottomPanel.setPreferredSize(new Dimension(1000, 200));
    bottomPanel.add(new JButton("Старт"));
    bottomPanel.add(new JButton("Стоп"));
    bottomPanel.add(new JButton("Сброс"));

    // 2. Правая часть (Восток): ширина 200, высота растягивается до нижней панели
    JTextArea textArea = new JTextArea("Поле с прокруткой...\n");
    textArea.setBackground(new Color(180, 180, 180)); // Средне-серый
    JScrollPane rightScrollPane = new JScrollPane(textArea);
    rightScrollPane.setPreferredSize(new Dimension(200, 0)); // 0 означает, что высота игнорируется менеджером

    // 3. Левая верхняя часть (Центр): сетка 14x14, занимает всё оставшееся место
    JPanel centerPanel = new JPanel();
    centerPanel.setLayout(new GridLayout(14, 14));
    centerPanel.setBackground(emptyCellColor); // Светло-серый

    //
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

        JButton btn = new JButton();
        btn.setBackground(color);
        btn.setMargin(new java.awt.Insets(0, 0, 0, 0));
        centerPanel.add(btn);
      }
    }

    // Сборка окна с указанием позиций
    frame.add(bottomPanel, BorderLayout.SOUTH);
    frame.add(rightScrollPane, BorderLayout.EAST);
    frame.add(centerPanel, BorderLayout.CENTER);

    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  });
}