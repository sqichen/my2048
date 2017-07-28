/**
 * Created by ShuqiChen on 2017/7/27.
 * 2048 in Swing
 */
import java.util.*;

public class Tile {
    private final NumbersAndColors nac;
    //cache maps Integers 0, 2, 4 etc. to their  corresponding tiles
    private final static HashMap<Integer, Tile> cache = new HashMap<>();

    public final static Tile ZERO = new Tile(NumbersAndColors._0);
    public final static Tile TWO = new Tile(NumbersAndColors._2);
    public final static Tile FOUR = new Tile(NumbersAndColors._4);

    //frequently used numbers - 0, 2, and 4 - are put in the cache
    //0 refers to empty tiles
    static {
        for (NumbersAndColors n : NumbersAndColors.values()) {
            switch (n) {
                case _0:
                    cache.put(n.value(), ZERO);
                    break;
                case _2:
                    cache.put(n.value(), TWO);
                    break;
                case _4:
                    cache.put(n.value(), FOUR);
                    break;
                default:
                    cache.put(n.value(), new Tile(n));
                    break;
            }
        }
    }

    //constructor
    public Tile(NumbersAndColors n) {
        nac = n;
    }

    NumbersAndColors value() {
        return nac;
    }

    //relate a number to its according tile
    public static Tile valueOf(int num) {
        return cache.get(num);
    }

    //for merging
    public Tile getDouble() {
        // << 1 refers to "*2"
        return valueOf(nac.value() << 1);
    }

    boolean isEmpty() {
        return nac == NumbersAndColors._0;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Tile)) {
            return false;
        }
        Tile other = (Tile) obj;
        if (nac != other.nac) {
            return false;
        }
        return true;
    }

    //randomTile to determine a 4 or 2 for a new tile
    static Tile randomTile() {
        return Math.random() < 0.15 ? FOUR : TWO;
    }

    public String toString() {
        return String.format("%1$4d", nac.value());
    }
}
