package View;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class CellPanel extends JPanel {
  private Color bgColor;
  private Color circleColor; // null = нет кружка

  public CellPanel(Color bgColor, Color circleColor) {
    this.bgColor = bgColor;
    this.circleColor = circleColor;
    setBorder(BorderFactory.createLineBorder(Color.GRAY));
  }

  public void setState(Color bgColor, Color circleColor) {
    this.bgColor = bgColor;
    this.circleColor = circleColor;
    repaint();
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);

    // фон
    g2.setColor(bgColor);
    g2.fillRect(0, 0, getWidth(), getHeight());

    // кружок (если есть)
    if (circleColor != null) {
      int d = Math.min(getWidth(), getHeight()) / 2;
      int x = (getWidth() - d) / 2;
      int y = (getHeight() - d) / 2;

      g2.setColor(circleColor);
      g2.fillOval(x, y, d, d);
    }
  }
}