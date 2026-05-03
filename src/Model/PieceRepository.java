package Model;

import java.util.ArrayList;
import java.util.List;

public class PieceRepository {
  private static final List<Shape> SHAPES = List.of(
      new Shape(new int[][] { { 1 } }), // 0: I1
      new Shape(new int[][] { { 1, 1 } }), // 1: I2
      new Shape(new int[][] { { 1, 1, 1 } }), // 2: I3
      new Shape(new int[][] { { 1, 1 }, { 1, 0 } }), // 3: V3
      new Shape(new int[][] { { 1, 1, 1, 1 } }), // 4: I4
      new Shape(new int[][] { { 1, 0, 0 }, { 1, 1, 1 } }), // 5: L4
      new Shape(new int[][] { { 1, 1, 1 }, { 0, 1, 0 } }), // 6: T4
      new Shape(new int[][] { { 1, 1 }, { 1, 1 } }), // 7: O4
      new Shape(new int[][] { { 1, 1, 0 }, { 0, 1, 1 } }), // 8: Z4
      new Shape(new int[][] { { 0, 1, 1 }, { 1, 1, 0 }, { 0, 1, 0 } }), // 9: F5
      new Shape(new int[][] { { 0, 1, 0 }, { 1, 1, 1 }, { 0, 1, 0 } }), // 10: X5
      new Shape(new int[][] { { 1, 1 }, { 1, 1 }, { 1, 0 } }), // 11: P5
      new Shape(new int[][] { { 1, 0, 0 }, { 1, 1, 0 }, { 0, 1, 1 } }), // 12: W5
      new Shape(new int[][] { { 1, 1, 0 }, { 0, 1, 0 }, { 0, 1, 1 } }), // 13: Z5
      new Shape(new int[][] { { 0, 1, 0, 0 }, { 1, 1, 1, 1 } }), // 14: Y5
      new Shape(new int[][] { { 1, 0, 0, 0 }, { 1, 1, 1, 1 } }), // 15: L5
      new Shape(new int[][] { { 1, 0, 1 }, { 1, 1, 1 } }), // 16: U5
      new Shape(new int[][] { { 1, 1, 1 }, { 0, 1, 0 }, { 0, 1, 0 } }), // 17: T5
      new Shape(new int[][] { { 1, 1, 1 }, { 1, 0, 0 }, { 1, 0, 0 } }), // 18: V5
      new Shape(new int[][] { { 1, 1, 0, 0 }, { 0, 1, 1, 1 } }), // 19: N5
      new Shape(new int[][] { { 1, 1, 1, 1, 1 } }) // 20: I5
  );

  public static Shape getVariant(int id, int variantIndex) {
    return VARIANTS_CACHE.get(id).get(variantIndex);
  }

  public static int variantCount(int id) {
    return VARIANTS_CACHE.get(id).size();
  }

  private static final List<List<Shape>> VARIANTS_CACHE;
  static {
    VARIANTS_CACHE = new ArrayList<>(SHAPES.size());
    for (Shape shape : SHAPES) {
      VARIANTS_CACHE.add(computeVariants(shape));
    }
  }

  private static List<Shape> computeVariants(Shape shape) {
    List<Shape> unique = new ArrayList<>(1);
    Shape current = shape;
    for (int row = 0; row < 4; row++) {
      addIfUnique(unique, current);
      addIfUnique(unique, current.mirror());
      current = current.rotate();
    }

    return unique;
  }

  private static void addIfUnique(List<Shape> list, Shape shape) {
    if (!list.contains(shape)) {
      list.add(shape);
    }
  }
}
