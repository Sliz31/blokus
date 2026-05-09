package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// one of the 21 blokus pieces
// instead of storing a matrix, we store the piece id and variant index (0-7)
public class Piece {

  // all 21 base shapes
  private static final List<Shape> BASE_SHAPES = List.of(
      new Shape(new int[][] { { 1 } }),
      new Shape(new int[][] { { 1, 1 } }),
      new Shape(new int[][] { { 1, 1, 1 } }),
      new Shape(new int[][] { { 1, 1 }, { 1, 0 } }),
      new Shape(new int[][] { { 1, 1, 1, 1 } }),
      new Shape(new int[][] { { 1, 0, 0 }, { 1, 1, 1 } }),
      new Shape(new int[][] { { 1, 1, 1 }, { 0, 1, 0 } }),
      new Shape(new int[][] { { 1, 1 }, { 1, 1 } }),
      new Shape(new int[][] { { 1, 1, 0 }, { 0, 1, 1 } }),
      new Shape(new int[][] { { 0, 1, 1 }, { 1, 1, 0 }, { 0, 1, 0 } }),
      new Shape(new int[][] { { 0, 1, 0 }, { 1, 1, 1 }, { 0, 1, 0 } }),
      new Shape(new int[][] { { 1, 1 }, { 1, 1 }, { 1, 0 } }),
      new Shape(new int[][] { { 1, 0, 0 }, { 1, 1, 0 }, { 0, 1, 1 } }),
      new Shape(new int[][] { { 1, 1, 0 }, { 0, 1, 0 }, { 0, 1, 1 } }),
      new Shape(new int[][] { { 0, 1, 0, 0 }, { 1, 1, 1, 1 } }),
      new Shape(new int[][] { { 1, 0, 0, 0 }, { 1, 1, 1, 1 } }),
      new Shape(new int[][] { { 1, 0, 1 }, { 1, 1, 1 } }),
      new Shape(new int[][] { { 1, 1, 1 }, { 0, 1, 0 }, { 0, 1, 0 } }),
      new Shape(new int[][] { { 1, 1, 1 }, { 1, 0, 0 }, { 1, 0, 0 } }),
      new Shape(new int[][] { { 1, 1, 0, 0 }, { 0, 1, 1, 1 } }),
      new Shape(new int[][] { { 1, 1, 1, 1, 1 } }));

  // cache for all 8 states of each piece (4 rotations x 2 mirror groups)
  // variant 0-3: original + 3 rotations, variant 4-7: flipped + 3 rotations
  private static final List<List<Shape>> CACHE;

  static {
    List<List<Shape>> cache = new ArrayList<>(BASE_SHAPES.size());
    for (Shape baseShape : BASE_SHAPES) {
      List<Shape> variants = new ArrayList<>(8);
      Shape currentShape = baseShape;
      for (int index = 0; index < 4; index++) {
        variants.add(currentShape);
        currentShape = currentShape.rotateRight();
      }
      currentShape = baseShape.flip();
      for (int index = 0; index < 4; index++) {
        variants.add(currentShape);
        currentShape = currentShape.rotateRight();
      }
      cache.add(List.copyOf(variants));
    }
    CACHE = List.copyOf(cache);
  }

  private final int id;
  // which of the 8 shapes is active (0 to 7)
  private int variant;
  private boolean isUsed;

  public Piece(int id) {
    this(id, 0);
  }

  public Piece(int id, int variant) {
    if (id < 1 || id > BASE_SHAPES.size()) {
      throw new IllegalArgumentException("Invalid piece id: " + id);
    }
    if (variant < 0 || variant > 7) {
      throw new IllegalArgumentException("Invalid variant: " + variant);
    }
    this.id = id;
    this.variant = variant;
    this.isUsed = false;
  }

  public int getId() {
    return id;
  }

  public int getVariant() {
    return variant;
  }

  public boolean isUsed() {
    return isUsed;
  }

  public void setUsed(boolean used) {
    this.isUsed = used;
  }

  // returns current shape from cache - no calculation needed
  public Shape getShape() {
    return CACHE.get(id - 1).get(variant);
  }

  public int getSize() {
    return getShape().countCells();
  }

  // rotate 90 degrees clockwise - just moves the variant index
  public void rotate() {
    variant = (variant < 4) ? (variant + 1) % 4 : 4 + (variant - 4 + 1) % 4;
  }

  // switch between original group (0-3) and mirrored group (4-7)
  public void flip() {
    variant = (variant + 4) % 8;
  }

  // all 8 shapes for a given piece id - used by AI to try all orientations
  public static List<Shape> getVariants(int id) {
    return CACHE.get(id - 1);
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (object == null || getClass() != object.getClass()) {
      return false;
    }
    Piece other = (Piece) object;
    return id == other.id && variant == other.variant;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, variant);
  }
}
