import controller.GameController;
import javax.swing.UIManager;

public class Main {
  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println("Hello and welcome to Blokus!");
    // GameController creates model and view and wires everything together
    new GameController();
  }
}