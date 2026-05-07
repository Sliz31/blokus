package Logic;

import java.util.ArrayList;
import java.util.List;

// one of the 21 blokus pieces owned by a player
// instead of storing a matrix directly, we just remember the piece id and which of the 8 variants is active
public class Piece {

    // all 21 base shapes, one entry per piece (index 0 = piece id 1)
    private static final List<Shape> BASE_SHAPES = List.of(
        new Shape(new int[][]{{1}}),                                       //  1: I1
        new Shape(new int[][]{{1, 1}}),                                    //  2: I2
        new Shape(new int[][]{{1, 1, 1}}),                                 //  3: I3
        new Shape(new int[][]{{1, 1}, {1, 0}}),                            //  4: V3
        new Shape(new int[][]{{1, 1, 1, 1}}),                              //  5: I4
        new Shape(new int[][]{{1, 0, 0}, {1, 1, 1}}),                      //  6: L4
        new Shape(new int[][]{{1, 1, 1}, {0, 1, 0}}),                      //  7: T4
        new Shape(new int[][]{{1, 1}, {1, 1}}),                            //  8: O4
        new Shape(new int[][]{{1, 1, 0}, {0, 1, 1}}),                      //  9: Z4
        new Shape(new int[][]{{0, 1, 1}, {1, 1, 0}, {0, 1, 0}}),           // 10: F5
        new Shape(new int[][]{{0, 1, 0}, {1, 1, 1}, {0, 1, 0}}),           // 11: X5
        new Shape(new int[][]{{1, 1}, {1, 1}, {1, 0}}),                    // 12: P5
        new Shape(new int[][]{{1, 0, 0}, {1, 1, 0}, {0, 1, 1}}),           // 13: W5
        new Shape(new int[][]{{1, 1, 0}, {0, 1, 0}, {0, 1, 1}}),           // 14: Z5
        new Shape(new int[][]{{0, 1, 0, 0}, {1, 1, 1, 1}}),                // 15: Y5
        new Shape(new int[][]{{1, 0, 0, 0}, {1, 1, 1, 1}}),                // 16: L5
        new Shape(new int[][]{{1, 0, 1}, {1, 1, 1}}),                      // 17: U5
        new Shape(new int[][]{{1, 1, 1}, {0, 1, 0}, {0, 1, 0}}),           // 18: T5
        new Shape(new int[][]{{1, 1, 1}, {1, 0, 0}, {1, 0, 0}}),           // 19: V5
        new Shape(new int[][]{{1, 1, 0, 0}, {0, 1, 1, 1}}),                // 20: N5
        new Shape(new int[][]{{1, 1, 1, 1, 1}})                            // 21: I5
    );

    // cache for all 8 states of each piece (4 rotations x 2 mirror groups)
    // variant 0-3 = original + 3 rotations
    // variant 4-7 = flipped + 3 rotations
    private static final List<List<Shape>> CACHE;

    // build the cache one time when the class loads
    static {
        List<List<Shape>> cache = new ArrayList<>(BASE_SHAPES.size());
        for (Shape baseShape : BASE_SHAPES) {
            List<Shape> variants = new ArrayList<>(8);
            // first group: original and 3 clockwise rotations
            Shape currentShape = baseShape;
            for (int index = 0; index < 4; index++) {
                variants.add(currentShape);
                currentShape = currentShape.rotateRight();
            }
            // second group: flipped original and 3 clockwise rotations
            currentShape = baseShape.flip();
            for (int index = 0; index < 4; index++) {
                variants.add(currentShape);
                currentShape = currentShape.rotateRight();
            }
            cache.add(List.copyOf(variants));
        }
        CACHE = List.copyOf(cache);
    }

    // piece id goes from 1 to 21
    private final int id;
    // which of the 8 shapes is currently active (0 to 7)
    private int variant;
    // true when the player already placed this piece on the board
    private boolean isUsed;

    // creates a piece in its default orientation
    public Piece(int id) {
        this(id, 0);
    }

    // creates a piece with a specific variant already set
    public Piece(int id, int variant) {
        if (id < 1 || id > BASE_SHAPES.size())
            throw new IllegalArgumentException("Invalid piece id: " + id);
        if (variant < 0 || variant > 7)
            throw new IllegalArgumentException("Invalid variant: " + variant);
        this.id = id;
        this.variant = variant;
        this.isUsed = false;
    }

    public int getId()        { return id; }
    public int getVariant()   { return variant; }
    public boolean isUsed()   { return isUsed; }
    public void setUsed(boolean used) { this.isUsed = used; }

    // returns the current shape directly from cache - no calculation needed
    public Shape getShape() {
        return CACHE.get(id - 1).get(variant);
    }

    // counts how many cells this piece fills
    public int getSize() {
        return getShape().countCells();
    }

    // rotate 90 degrees clockwise - just moves the variant index, no matrix work
    // stays inside the same group: 0->1->2->3->0 or 4->5->6->7->4
    public void rotate() {
        variant = (variant < 4)
                ? (variant + 1) % 4
                : 4 + (variant - 4 + 1) % 4;
    }

    // switch between original group (0-3) and mirrored group (4-7)
    public void flip() {
        variant = (variant + 4) % 8;
    }

    // returns all 8 shapes for a given piece id - useful for AI when trying all variants
    public static List<Shape> getVariants(int id) {
        return CACHE.get(id - 1);
    }
}