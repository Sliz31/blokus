package GUI;

import Logic.Board;
import Logic.Game;
import Logic.Piece;
import Logic.Shape;
import Logic.Cell;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

// the main game window - builds and controls all GUI panels
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

        // build the 14x14 grid of buttons
        boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(14, 14));
        cells = new JButton[14][14];

        for (int row = 0; row < 14; row++) {
            for (int column = 0; column < 14; column++) {
                JButton btn = new JButton();
                btn.setBackground(Color.WHITE);
                btn.setFocusPainted(false);
                int cellRow = row;
                int cellColumn = column;

                btn.addActionListener(e -> game.handleCellClick(cellRow, cellColumn));

                // update ghost preview when mouse enters or leaves a cell
                btn.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        ghostPane.setHover(cellRow, cellColumn);
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        ghostPane.setHover(-1, -1);
                    }
                });

                cells[row][column] = btn;
                boardPanel.add(btn);
            }
        }

        // highlight the two starting corners
        cells[4][4].setBorder(BorderFactory.createLineBorder(Color.ORANGE, 3));
        cells[9][9].setBorder(BorderFactory.createLineBorder(Color.ORANGE, 3));

        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        centerContainer.add(boardPanel, BorderLayout.CENTER);
        add(centerContainer, BorderLayout.CENTER);

        // glass pane draws the ghost piece over the board
        ghostPane = new GhostGlassPane();
        setGlassPane(ghostPane);
        ghostPane.setVisible(true);

        // right panel: player inventory list
        inventoryPanel = new JPanel();
        inventoryPanel.setLayout(new BoxLayout(inventoryPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(inventoryPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setPreferredSize(new Dimension(250, 0));

        JPanel eastContainer = new JPanel(new BorderLayout());
        eastContainer.add(new JLabel("Human Inventory", SwingConstants.CENTER), BorderLayout.NORTH);
        eastContainer.add(scrollPane, BorderLayout.CENTER);

        // rotate, flip, and pass buttons
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

        // bottom panel: score labels and game log
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
        // score = total squares (89) minus remaining unplaced squares
        humanScoreLabel.setText("Human Score: " + (89 - humanRemaining));
        aiScoreLabel.setText("AI Score: " + (89 - aiRemaining));
    }

    public void logMessage(String message) {
        statusArea.append(message + "\n");
        statusArea.setCaretPosition(statusArea.getDocument().getLength());
    }

    // repaints the whole board based on the current state
    public void updateBoard(Board board) {
        Cell[][] grid = board.getGrid();
        for (int row = 0; row < 14; row++) {
            for (int column = 0; column < 14; column++) {
                if (!grid[row][column].isOccupied()) {
                    cells[row][column].setBackground(Color.WHITE);
                } else {
                    int playerId = grid[row][column].getPlayerId();
                    cells[row][column].setBackground(playerId == 1 ? Color.BLUE : Color.RED);
                }
            }
        }
        updateScores(game.getHuman().getRemainingSquares(), game.getAi().getRemainingSquares());
    }

    // rebuilds the inventory list to show only unused pieces
    public void updateInventory(List<Piece> availablePieces) {
        inventoryPanel.removeAll();
        piecePanels.clear();

        for (Piece piece : availablePieces) {
            PiecePanel piecePanel = new PiecePanel(piece);
            piecePanels.add(piecePanel);

            piecePanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    selectPiece(piecePanel);
                }
            });

            inventoryPanel.add(piecePanel);
            inventoryPanel.add(Box.createVerticalStrut(10));
        }

        // re-select the previously selected piece if it's still in the inventory
        PiecePanel panelToSelect = null;
        if (game.getSelectedPiece() != null) {
            for (PiecePanel panel : piecePanels) {
                if (panel.getPiece().getId() == game.getSelectedPiece().getId()) {
                    panelToSelect = panel;
                    break;
                }
            }
        }

        if (panelToSelect != null) {
            selectPiece(panelToSelect);
        } else {
            clearSelection();
        }

        inventoryPanel.revalidate();
        inventoryPanel.repaint();
    }

    private void selectPiece(PiecePanel panel) {
        for (PiecePanel otherPanel : piecePanels) {
            otherPanel.setSelected(false);
        }
        panel.setSelected(true);
        game.setSelectedPiece(panel.getPiece());
        selectedPiecePanel = panel;
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

    // draws a transparent ghost piece on the board where the mouse is hovering
    private class GhostGlassPane extends JComponent {
        private int hoverRow = -1;
        private int hoverColumn = -1;

        public void setHover(int row, int column) {
            this.hoverRow = row;
            this.hoverColumn = column;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (hoverRow == -1 || hoverColumn == -1 || game.getSelectedPiece() == null) {
                return;
            }

            Shape shape = game.getSelectedPiece().getShape();

            // green if the move is valid, red if not
            boolean isValid = game.getBoard().isValidMove(game.getSelectedPiece(), hoverRow, hoverColumn, game.getHuman());
            Graphics2D graphics = (Graphics2D) g;
            graphics.setColor(isValid ? new Color(0, 255, 0, 100) : new Color(255, 0, 0, 100));

            Component topLeftCell = cells[0][0];
            Point topLeftPoint = SwingUtilities.convertPoint(topLeftCell, 0, 0, this);
            int cellWidth = topLeftCell.getWidth();
            int cellHeight = topLeftCell.getHeight();

            for (int row = 0; row < shape.rows(); row++) {
                for (int column = 0; column < shape.cols(); column++) {
                    if (shape.cellAt(row, column) == 1) {
                        if (hoverRow + row < 14 && hoverColumn + column < 14) {
                            int drawX = topLeftPoint.x + (hoverColumn + column) * cellWidth;
                            int drawY = topLeftPoint.y + (hoverRow + row) * cellHeight;
                            graphics.fillRect(drawX, drawY, cellWidth, cellHeight);
                        }
                    }
                }
            }
        }
    }

    // a small panel that draws one piece from the inventory
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

        public Piece getPiece() { return piece; }

        public void setSelected(boolean selected) {
            this.isSelected = selected;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D graphics = (Graphics2D) g;

            // draw selection highlight or normal border
            if (isSelected) {
                graphics.setColor(new Color(200, 230, 255));
                graphics.fillRect(0, 0, getWidth(), getHeight());
                graphics.setColor(Color.BLACK);
                graphics.setStroke(new BasicStroke(2));
                graphics.drawRect(1, 1, getWidth() - 2, getHeight() - 2);
            } else {
                graphics.setColor(Color.LIGHT_GRAY);
                graphics.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
            }

            // draw the piece cells centered in the panel
            Shape shape = piece.getShape();
            int shapeWidth = shape.cols() * CELL_SIZE;
            int shapeHeight = shape.rows() * CELL_SIZE;
            int startX = (getWidth() - shapeWidth) / 2;
            int startY = (getHeight() - shapeHeight) / 2;

            for (int row = 0; row < shape.rows(); row++) {
                for (int column = 0; column < shape.cols(); column++) {
                    if (shape.cellAt(row, column) == 1) {
                        int drawX = startX + column * CELL_SIZE;
                        int drawY = startY + row * CELL_SIZE;

                        graphics.setColor(Color.BLUE);
                        graphics.fillRect(drawX, drawY, CELL_SIZE, CELL_SIZE);

                        graphics.setColor(Color.BLACK);
                        graphics.setStroke(new BasicStroke(1));
                        graphics.drawRect(drawX, drawY, CELL_SIZE, CELL_SIZE);
                    }
                }
            }

            // show piece id and size as a small label
            graphics.setColor(Color.DARK_GRAY);
            graphics.setFont(new Font("SansSerif", Font.PLAIN, 10));
            graphics.drawString("Piece " + piece.getId() + " (Size " + piece.getSize() + ")", 5, 12);
        }
    }
}
