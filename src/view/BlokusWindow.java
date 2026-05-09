package view;

import model.Board;
import model.Cell;
import model.Piece;
import model.Shape;
import model.Position;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

// the main game window - draws the board, inventory, and score
// the view does NOT contain any game logic - it just fires events through ViewListener
public class BlokusWindow extends JFrame {

    private ViewListener listener;

    // the piece the human has currently selected (set by controller via
    // setSelectedPiece)
    private Piece selectedPiece = null;
    private PiecePanel selectedPiecePanel = null;

    private JButton[][] cells;
    private JPanel boardPanel;
    private JPanel inventoryPanel;
    private JTextArea statusArea;
    private List<PiecePanel> piecePanels;
    private GhostGlassPane ghostPane;
    private JLabel humanScoreLabel;
    private JLabel aiScoreLabel;

    public BlokusWindow(ViewListener listener) {
        this.listener = listener;
        this.piecePanels = new ArrayList<>();

        setTitle("Blokus AI");
        setSize(1000, 750);
        setLocationRelativeTo(null); // center window on the screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        buildBoardPanel();
        buildGlassPane();
        buildEastPanel();
        buildSouthPanel();
    }

    // called by controller to set (or clear) the currently selected piece
    public void setSelectedPiece(Piece piece) {
        this.selectedPiece = piece;
    }

    // repaints the selected piece panel and ghost - call after rotate/flip
    public void repaintSelectedPieceAndGhost() {
        if (selectedPiecePanel != null)
            selectedPiecePanel.repaint();
        ghostPane.repaint();
    }

    // updates the board colors from the model's current state
    public void updateBoardState(Board board) {
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
    }

    // rebuilds the inventory list with available pieces
    public void updateInventory(List<Piece> availablePieces) {
        inventoryPanel.removeAll();
        piecePanels.clear();

        for (Piece piece : availablePieces) {
            PiecePanel piecePanel = new PiecePanel(piece);
            piecePanels.add(piecePanel);
            piecePanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    listener.onPieceSelected(piecePanel.getPiece());
                }
            });
            inventoryPanel.add(piecePanel);
            inventoryPanel.add(Box.createVerticalStrut(10));
        }

        inventoryPanel.revalidate();
        inventoryPanel.repaint();
    }

    // highlights a piece in the inventory as selected
    public void highlightSelectedPiece(Piece piece) {
        selectedPiecePanel = null;
        for (PiecePanel panel : piecePanels) {
            boolean match = piece != null && panel.getPiece().getId() == piece.getId();
            panel.setSelected(match);
            if (match)
                selectedPiecePanel = panel;
        }
        ghostPane.repaint();
    }

    // removes selection highlight from all panels
    public void clearSelectionHighlight() {
        selectedPiece = null;
        selectedPiecePanel = null;
        for (PiecePanel panel : piecePanels)
            panel.setSelected(false);
        ghostPane.repaint();
    }

    // updates score labels from remaining squares count
    public void updateScores(int humanRemaining, int aiRemaining) {
        humanScoreLabel.setText("Human Score: " + (89 - humanRemaining));
        aiScoreLabel.setText("AI Score: " + (89 - aiRemaining));
    }

    // appends a message to the game log
    public void showLogMessage(String message) {
        statusArea.append(message + "\n");
        statusArea.setCaretPosition(statusArea.getDocument().getLength());
    }

    // shows the game over dialog with result message
    public void showGameOverDialog(int humanSquares, int aiSquares, String resultMessage) {
        JOptionPane.showMessageDialog(this,
                "Human unplaced squares: " + humanSquares + "\nAI unplaced squares: " + aiSquares + "\n\n"
                        + resultMessage,
                "Game Over",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void buildBoardPanel() {
        boardPanel = new JPanel(new GridLayout(14, 14));
        // lock board to exactly 14 x 40px = 560px so cells stay square
        Dimension boardSize = new Dimension(560, 560);
        boardPanel.setPreferredSize(boardSize);
        boardPanel.setMinimumSize(boardSize);
        boardPanel.setMaximumSize(boardSize);
        cells = new JButton[14][14];

        for (int row = 0; row < 14; row++) {
            for (int column = 0; column < 14; column++) {
                JButton btn = new JButton();
                btn.setBackground(Color.WHITE);
                btn.setFocusPainted(false);
                int cellRow = row;
                int cellColumn = column;

                // tell the controller which cell was clicked
                btn.addActionListener(e -> {
                    Position position = new Position(cellRow, cellColumn);
                    listener.onCellClicked(position);
                });

                // update ghost preview on hover
                btn.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        ghostPane.setHover(new Position(cellRow, cellColumn));
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        ghostPane.setHover(null);
                    }
                });

                cells[row][column] = btn;
                boardPanel.add(btn);
            }
        }

        // mark the two starting corners
        cells[4][4].setBorder(BorderFactory.createLineBorder(Color.ORANGE, 3));
        cells[9][9].setBorder(BorderFactory.createLineBorder(Color.ORANGE, 3));

        // GridBagLayout keeps the board centered without stretching it
        JPanel centerContainer = new JPanel(new GridBagLayout());
        centerContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        centerContainer.add(boardPanel);
        add(centerContainer, BorderLayout.CENTER);
    }

    private void buildGlassPane() {
        ghostPane = new GhostGlassPane();
        setGlassPane(ghostPane);
        ghostPane.setVisible(true);
    }

    private void buildEastPanel() {
        inventoryPanel = new JPanel();
        inventoryPanel.setLayout(new BoxLayout(inventoryPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(inventoryPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setPreferredSize(new Dimension(250, 0));

        JPanel eastContainer = new JPanel(new BorderLayout());
        eastContainer.add(new JLabel("Human Inventory", SwingConstants.CENTER), BorderLayout.NORTH);
        eastContainer.add(scrollPane, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        JButton rotateBtn = new JButton("Rotate");
        JButton flipBtn = new JButton("Flip");
        JButton passBtn = new JButton("Pass Turn");

        // just forward button clicks to the listener - no logic here
        rotateBtn.addActionListener(e -> listener.onRotateClicked());
        flipBtn.addActionListener(e -> listener.onFlipClicked());
        passBtn.addActionListener(e -> listener.onPassClicked());

        controlPanel.add(rotateBtn);
        controlPanel.add(flipBtn);
        controlPanel.add(passBtn);
        eastContainer.add(controlPanel, BorderLayout.SOUTH);
        add(eastContainer, BorderLayout.EAST);
    }

    private void buildSouthPanel() {
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
        southContainer.add(new JScrollPane(statusArea), BorderLayout.CENTER);

        add(southContainer, BorderLayout.SOUTH);
    }

    // draws a transparent ghost piece over the hovered board cell
    private class GhostGlassPane extends JComponent {
        private Position hoverPosition = null;

        public void setHover(Position position) {
            this.hoverPosition = position;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (hoverPosition == null || selectedPiece == null)
                return;

            int hoverRow = hoverPosition.getRow();
            int hoverColumn = hoverPosition.getColumn();

            Shape shape = selectedPiece.getShape();

            // ask the controller if the move is valid - wait, view should not call model
            // instead we store the validity result set by the controller
            // for now we draw gray (no validation in view - controller can push validity if
            // needed)
            Graphics2D graphics = (Graphics2D) g;
            graphics.setColor(new Color(100, 100, 255, 100)); // translucent blue

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

    // a small panel that draws one piece from the inventory list
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
            Graphics2D graphics = (Graphics2D) g;

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

            graphics.setColor(Color.DARK_GRAY);
            graphics.setFont(new Font("SansSerif", Font.PLAIN, 10));
            graphics.drawString("Piece " + piece.getId() + " (Size " + piece.getSize() + ")", 5, 12);
        }
    }
}
