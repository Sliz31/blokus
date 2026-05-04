package GUI;

import Logic.Board;
import Logic.Game;
import Logic.Piece;
import Logic.Cell;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class BlokusWindow extends JFrame {
  private Game game;
  private PiecePanel selectedPiecePanel = null;

  private JButton[][] cells;
  private JPanel boardPanel;
  private JPanel inventoryPanel;
  private JTextArea statusArea;
  private List<PiecePanel> piecePanels;
  private GhostGlassPane ghostPane;

  private JLabel humanScoreLabel;
  private JLabel aiScoreLabel;

  public BlokusWindow(Game game) {
    this.game = game;
    this.piecePanels = new ArrayList<>();
    setTitle("Blokus AI");
    setSize(1000, 750);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());

    // Setup Board Panel
    boardPanel = new JPanel();
    boardPanel.setLayout(new GridLayout(14, 14));
    cells = new JButton[14][14];

    for (int r = 0; r < 14; r++) {
      for (int c = 0; c < 14; c++) {
        JButton btn = new JButton();
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        int row = r;
        int col = c;

        btn.addActionListener(e -> game.handleCellClick(row, col));

        // Track mouse hover for the Ghost outline
        btn.addMouseListener(new MouseAdapter() {
          @Override
          public void mouseEntered(MouseEvent e) {
            ghostPane.setHover(row, col);
          }

          @Override
          public void mouseExited(MouseEvent e) {
            ghostPane.setHover(-1, -1);
          }
        });

        cells[r][c] = btn;
        boardPanel.add(btn);
      }
    }

    // Setup internal highlighting for start corners
    cells[4][4].setBorder(BorderFactory.createLineBorder(Color.ORANGE, 3));
    cells[9][9].setBorder(BorderFactory.createLineBorder(Color.ORANGE, 3));

    JPanel centerContainer = new JPanel(new BorderLayout());
    centerContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    centerContainer.add(boardPanel, BorderLayout.CENTER);
    add(centerContainer, BorderLayout.CENTER);

    // Setup GlassPane for Ghost Previews
    ghostPane = new GhostGlassPane();
    setGlassPane(ghostPane);
    ghostPane.setVisible(true);

    // Setup Inventory Panel
    inventoryPanel = new JPanel();
    inventoryPanel.setLayout(new BoxLayout(inventoryPanel, BoxLayout.Y_AXIS));
    JScrollPane scrollPane = new JScrollPane(inventoryPanel);
    scrollPane.getVerticalScrollBar().setUnitIncrement(16);
    scrollPane.setPreferredSize(new Dimension(250, 0));

    JPanel eastContainer = new JPanel(new BorderLayout());
    eastContainer.add(new JLabel("Human Inventory", SwingConstants.CENTER), BorderLayout.NORTH);
    eastContainer.add(scrollPane, BorderLayout.CENTER);

    // Controls
    JPanel controlPanel = new JPanel();
    JButton rotateBtn = new JButton("Rotate");
    rotateBtn.addActionListener(e -> {
      if (game.getSelectedPiece() != null) {
        game.getSelectedPiece().rotate();
        if (selectedPiecePanel != null) {
          selectedPiecePanel.repaint();
        }
        ghostPane.repaint();
        logMessage("Piece rotated.");
      }
    });

    JButton flipBtn = new JButton("Flip");
    flipBtn.addActionListener(e -> {
      if (game.getSelectedPiece() != null) {
        game.getSelectedPiece().flip();
        if (selectedPiecePanel != null) {
          selectedPiecePanel.repaint();
        }
        ghostPane.repaint();
        logMessage("Piece flipped.");
      }
    });

    JButton passBtn = new JButton("Pass Turn");
    passBtn.addActionListener(e -> game.humanPass());

    controlPanel.add(rotateBtn);
    controlPanel.add(flipBtn);
    controlPanel.add(passBtn);
    eastContainer.add(controlPanel, BorderLayout.SOUTH);

    add(eastContainer, BorderLayout.EAST);

    // Setup Status Area & Scores
    JPanel southContainer = new JPanel(new BorderLayout());

    JPanel scorePanel = new JPanel(new GridLayout(1, 2));
    scorePanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    humanScoreLabel = new JLabel("Human Score: 0");
    humanScoreLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
    humanScoreLabel.setForeground(Color.BLUE);
    aiScoreLabel = new JLabel("AI Score: 0");
    aiScoreLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
    aiScoreLabel.setForeground(Color.RED);
    aiScoreLabel.setHorizontalAlignment(SwingConstants.RIGHT);

    scorePanel.add(humanScoreLabel);
    scorePanel.add(aiScoreLabel);
    southContainer.add(scorePanel, BorderLayout.NORTH);

    statusArea = new JTextArea(6, 50);
    statusArea.setEditable(false);
    statusArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
    JScrollPane statusScroll = new JScrollPane(statusArea);
    southContainer.add(statusScroll, BorderLayout.CENTER);

    add(southContainer, BorderLayout.SOUTH);
  }

  public void updateScores(int humanRemaining, int aiRemaining) {
    humanScoreLabel.setText("Human Score: " + (89 - humanRemaining));
    aiScoreLabel.setText("AI Score: " + (89 - aiRemaining));
  }

  public void logMessage(String msg) {
    statusArea.append(msg + "\n");
    statusArea.setCaretPosition(statusArea.getDocument().getLength());
  }

  public void updateBoard(Board board) {
    Cell[][] grid = board.getGrid();
    for (int r = 0; r < 14; r++) {
      for (int c = 0; c < 14; c++) {
        if (!grid[r][c].isOccupied()) {
          cells[r][c].setBackground(Color.WHITE);
        } else {
          int pId = grid[r][c].getPlayerId();
          if (pId == 1) {
            cells[r][c].setBackground(Color.BLUE);
          } else if (pId == 2) {
            cells[r][c].setBackground(Color.RED);
          }
        }
      }
    }
    updateScores(game.getHuman().getRemainingSquares(), game.getAi().getRemainingSquares());
  }

  public void updateInventory(List<Piece> availablePieces) {
    inventoryPanel.removeAll();
    piecePanels.clear();

    for (Piece p : availablePieces) {
      PiecePanel pp = new PiecePanel(p);
      piecePanels.add(pp);

      pp.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          selectPiece(pp);
        }
      });

      inventoryPanel.add(pp);
      inventoryPanel.add(Box.createVerticalStrut(10));
    }

    PiecePanel toSelect = null;
    if (game.getSelectedPiece() != null) {
      for (PiecePanel pp : piecePanels) {
        if (pp.getPiece().getId() == game.getSelectedPiece().getId()) {
          toSelect = pp;
          break;
        }
      }
    }

    if (toSelect != null) {
      selectPiece(toSelect);
    } else {
      clearSelection();
    }

    inventoryPanel.revalidate();
    inventoryPanel.repaint();
  }

  private void selectPiece(PiecePanel pp) {
    for (PiecePanel panel : piecePanels) {
      panel.setSelected(false);
    }
    pp.setSelected(true);
    game.setSelectedPiece(pp.getPiece());
    selectedPiecePanel = pp;
    logMessage("Selected Piece " + game.getSelectedPiece().getId());
  }

  public void clearSelection() {
    game.setSelectedPiece(null);
    selectedPiecePanel = null;
    for (PiecePanel panel : piecePanels) {
      panel.setSelected(false);
    }
    ghostPane.repaint();
  }

  // Custom GlassPane for drawing ghost transparent pieces
  private class GhostGlassPane extends JComponent {
    private int hoverRow = -1;
    private int hoverCol = -1;

    public void setHover(int row, int col) {
      this.hoverRow = row;
      this.hoverCol = col;
      repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);

      if (hoverRow == -1 || hoverCol == -1 || game.getSelectedPiece() == null) {
        return;
      }

      int[][] shape = game.getSelectedPieceMatrix();
      if (shape == null)
        return;

      boolean valid = game.getBoard().isValidMove(game.getSelectedPiece(), hoverRow, hoverCol, game.getHuman());

      Graphics2D g2 = (Graphics2D) g;
      if (valid) {
        g2.setColor(new Color(0, 255, 0, 100)); // Translucent Green
      } else {
        g2.setColor(new Color(255, 0, 0, 100)); // Translucent Red
      }

      Component baseCell = cells[0][0];
      Point basePoint = SwingUtilities.convertPoint(baseCell, 0, 0, this);
      int cellW = baseCell.getWidth();
      int cellH = baseCell.getHeight();

      for (int r = 0; r < shape.length; r++) {
        for (int c = 0; c < shape[0].length; c++) {
          if (shape[r][c] == 1) {
            if (hoverRow + r < 14 && hoverCol + c < 14) {
              int drawX = basePoint.x + (hoverCol + c) * cellW;
              int drawY = basePoint.y + (hoverRow + r) * cellH;
              g2.fillRect(drawX, drawY, cellW, cellH);
            }
          }
        }
      }
    }
  }

  // Custom JPanel to visually render a Blokus Piece in inventory
  private class PiecePanel extends JPanel {
    private Piece piece;
    private boolean isSelected;
    private final int CELL_SIZE = 15;

    public PiecePanel(Piece piece) {
      this.piece = piece;
      this.isSelected = false;

      setPreferredSize(new Dimension(150, 100));
      setMaximumSize(new Dimension(150, 100));
      setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public Piece getPiece() {
      return piece;
    }

    public void setSelected(boolean selected) {
      this.isSelected = selected;
      repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2 = (Graphics2D) g;

      if (isSelected) {
        g2.setColor(new Color(200, 230, 255));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(1, 1, getWidth() - 2, getHeight() - 2);
      } else {
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
      }

      int[][] shape = piece.getShape();

      int shapeWidth = shape[0].length * CELL_SIZE;
      int shapeHeight = shape.length * CELL_SIZE;
      int startX = (getWidth() - shapeWidth) / 2;
      int startY = (getHeight() - shapeHeight) / 2;

      for (int r = 0; r < shape.length; r++) {
        for (int c = 0; c < shape[0].length; c++) {
          if (shape[r][c] == 1) {
            int x = startX + c * CELL_SIZE;
            int y = startY + r * CELL_SIZE;

            g2.setColor(Color.BLUE);
            g2.fillRect(x, y, CELL_SIZE, CELL_SIZE);

            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(1));
            g2.drawRect(x, y, CELL_SIZE, CELL_SIZE);
          }
        }
      }

      g2.setColor(Color.DARK_GRAY);
      g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
      g2.drawString("Piece " + piece.getId() + " (Size " + piece.getSize() + ")", 5, 12);
    }
  }
}
