package Model;

public class Piece {
  private final int id;
  private int variant;

  public Piece(int id, int variant) {
    this.id = id;
    this.variant = variant;
  }

  public Piece(int id) {
    this.id = id;
    this.variant = 0;
  }

  public int getId() {
    return id;
  }

  public Shape getShape() {
    return PieceRepository.getVariant(id, variant);
  }

  public int getSize() {
    return PieceRepository.getVariant(id, variant).countCells();
  }

  public int getRows() {
    return getShape().rows();
  }

  public int getColumns() {
    return getShape().cols();
  }

  public Piece copy() {
    Piece piece = new Piece(id, variant);
    return piece;
  }

  public Piece rotate() {
    Shape rotated = getShape().rotate();
    for (int variant = 0; variant < PieceRepository.variantCount(id); variant++) {
      if (PieceRepository.getVariant(id, variant).equals(rotated)) {
        return new Piece(id, variant);
      }
    }

    throw new IllegalStateException("Rotated shape not found in variants");
  }

  public Piece mirrored() {
    Shape mirrored = getShape().mirror();
    for (int variant = 0; variant < PieceRepository.variantCount(id); variant++) {
      if (PieceRepository.getVariant(id, variant).equals(mirrored)) {
        return new Piece(id, variant);
      }
    }

    throw new IllegalStateException("Mirrored shape not found in variants");
  }

}
