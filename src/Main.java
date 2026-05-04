import Logic.Game;
import javax.swing.UIManager;

public class Main {
  public static void main(String[] args) {
    try {
        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    } catch (Exception e) {
        e.printStackTrace();
    }

    System.out.println("Hello and welcome to Blokus!");
    // Initialize the game
    Game game = new Game();
  }
}