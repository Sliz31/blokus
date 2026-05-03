package Model;

public class MinimaxAI {

  public Move findBestMove(GameState state, int depth);

  private int minimax(GameState state, int depth, boolean maximizing);
}
