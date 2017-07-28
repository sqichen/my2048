/**
 * Created by ShuqiChen on 2017/7/26.
 * 2048 in Swing
 * This class is for creating the grid, painting it
 * and containing all the algorithms needed for movements after keypresses
 * (i.e. moving and merging lines)
 */

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

public class Grid extends JPanel{
    //serialVersionUID for supressing warning
    private static final long serialVersionUID = 1L;
    //number of rows
    public static final int ROW = 4;
    //define the main as a host
    final main2048 host;

    //array of tiles used for drawing the grid
    private Tile[] tiles;
    //set goal as 2048 value from NumbersAndColors
    public static NumbersAndColors goal = NumbersAndColors._2048;

    private static final Color BG_COLOR = new Color(0xbbada0);
    private static final Font STR_FONT = new Font(Font.SERIF, Font.BOLD, 16);
    private static final int SIDE = 64;
    private static final int MARGIN = 16;

    //constructor
    public Grid(main2048 main) {
        host = main;
        setFocusable(true); //default
        initializeTiles();
    }

    //addNewTile finds a random && empty spot on the grid
    //and initializes a new random tile on it
    private void addNewTile() {
        //list of all empty tiles
        List<Integer> list_empty = findEmptyIndex();
        //choose randomly from list_empty
        int index = list_empty.get((int) (Math.random()*list_empty.size())); //get the ith(random) element of list_empty
        tiles[index] = Tile.randomTile();
    }

    //initialize the board,
    //setting all tiles to 0, then adding two new tiles
    public void initializeTiles() {
        tiles = new Tile[ROW*ROW];
        //set all tiles to 0
        for (int i = 0; i < tiles.length; i++) {
            tiles[i] = Tile.ZERO;
        }
        //add two tiles to start off
        addNewTile();
        addNewTile();
        host.statusBar.setText("");
    }

    //findEmptyIndex returns a list of the index of empty tiles
    private List<Integer> findEmptyIndex() {
        List<Integer> list = new LinkedList<>();
        for (int i = 0; i < tiles.length; i++) {
            if (tiles[i].isEmpty()) {
                list.add(i);
            }
        }
        return list;
    }

    //check if the grid is full (no empty tiles)
    private boolean isGridFull() {
        return findEmptyIndex().size() == 0;
    }

    /*
    [0] [4] [0] [0]
    [0] [0] [2] [0]
    [0] [0] [0] [0]
    [0] [0] [0] [0]
    tiles[1,0] = [4]
    tiles[2,1] = [2]
     */
    //return tile at [x, y]
    //x, y = 0, 1, 2, 3
    private Tile tileAt(int x, int y) {
        return tiles[x + y * ROW];
    }

    //check if there are tiles blocking a certain direction
    boolean checkIfCanMove() {
        if (!isGridFull()) {
            return true;
        }
        //check:
        //grid is full, but two adjacent tiles are the same so that they could be merged
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < ROW; j++) {
                Tile t = tileAt(i, j);
                if ((i < ROW-1 && t.equals(tileAt(i+1, j))) || ( j < ROW-1 && t.equals(tileAt(i, j+1)))) {
                    return true;
                }
            }
        }
        return false;
    }

    //rotate returns an array of tiles which is rotated the degrees given
    //only works with 90, 180, 270
    //this is used to literally rotate the board clockwise
    //used for cheating later
    private Tile[] rotate(int degree) {
        Tile[] newTiles = new Tile[ROW*ROW];
        int offsetX = 3;
        int offsetY = 3;
        if (degree == 90) {
            offsetY = 0;
        } else if (degree == 180) {
        } else if (degree == 270) {
            offsetX = 0;
        }
        double radians = Math.toRadians(degree);
        int cos = (int) Math.cos(radians);
        int sin = (int) Math.sin(radians);
        for (int x = 0; x < ROW; x++) {
            for (int y = 0; y < ROW; y++) {
                int newX = (x * cos) - (y * sin) + offsetX;
                int newY = (x * sin) + (y * cos) + offsetY;
                newTiles[(newX) + (newY) * ROW] = tileAt(x, y);
            }
        }
        return newTiles;
    }

    //the following 5 functions are used for code reuse
    //getLine returns the selected row as an array
    //given y
    private Tile[] getLine(int index) {
        Tile[] row = new Tile[ROW];
        for (int i = 0; i < ROW; i++) {
            row[i] = tileAt(i, index);
        }
        return row;
    }

    //make sure size is correct, add 0s if not
    //as when you shift the tiles over, you are left with empty spots
    private static void ensureSize(List<Tile> l, int s) {
        while (l.size() < s) {
            l.add(Tile.ZERO);
        }
    }

    //move the row, return a new row
    //e.g.
    //oldLine: [2] [0] [2] [4]
    //newLine: [2] [2] [4] [0]
    private Tile[] moveLine(Tile[] oldLine) {
        LinkedList<Tile> l = new LinkedList<>();
        for (int i = 0; i < ROW; i++) {
            if (!oldLine[i].isEmpty()) {
                l.addLast(oldLine[i]);
            }
        }
        if (l.size() == 0) {
            return oldLine;
        } else {
            Tile[] newLine = new Tile[ROW];
            ensureSize(l, ROW);
            for (int i = 0; i < ROW; i++) {
                newLine[i] = l.removeFirst();
            }
            return newLine;
        }
    }

    //merge lines
    //e.g.
    //oldLine: [2] [2] [4] [0]
    //newLine: [4] [4] [0] [0]
    private Tile[] mergeLine(Tile[] oldLine) {
        LinkedList<Tile> list = new LinkedList<>();
        for (int i = 0; i < ROW; i++) {
            if (i < ROW - 1 && !oldLine[i].isEmpty() && oldLine[i].equals(oldLine[i+1])) {
                Tile merged = oldLine[i].getDouble();
                i++;
                list.add(merged);
                if (merged.value() == goal) {
                    host.win();
                }
            } else {
                list.add(oldLine[i]);
            }
        }
        ensureSize(list, ROW);
        return list.toArray(new Tile[4]);
    }

    //set the line
    //copy re to tiles
    private void setLine(int index, Tile[] re) {
        for (int i = 0; i < ROW; i++) {
            tiles[i + index * ROW] = re[i];
        }
    }

    //use getLine, moveLine, mergeLine, setLine to move row to the left, and merge accordingly
    //row by row
    public void left(){
        boolean needAddTile = false;
        for (int i = 0; i < ROW; i++) {
            Tile[] origin = getLine(i);
            Tile[] afterMove = moveLine(origin);
            Tile[] merged = mergeLine(afterMove);
            setLine(i, merged);
            if (!needAddTile && !Arrays.equals(origin, merged)) {
                needAddTile = true;
            }
        }
        //generate a new [2] or [4]
        if (needAddTile) {
            addNewTile();
        }
    }

    //code reuse
    //right = rotate the board 180 degrees, move everything to the left
    //then rotate 180 degrees
    //same logic for up() and down()
    public void right() {
        tiles = rotate(180);
        left();
        tiles = rotate(180);
    }

    public void up() {
        tiles = rotate(270);
        left();
        tiles = rotate(90);
    }

    public void down() {
        tiles = rotate(90);
        left();
        tiles = rotate(270);
    }

    //function used for drawing
    private static int offsetCoors(int arg) {
        return arg * (MARGIN + SIDE) + MARGIN;
    }

    //draw the tile
    private void drawTile(Graphics g, Tile tile, int x, int y) {
        NumbersAndColors val = tile.value();
        int xOffset = offsetCoors(x);
        int yOffset = offsetCoors(y);
        g.setColor(val.color());
        g.fillRect(xOffset, yOffset, SIDE, SIDE);
        g.setColor(val.fontColor());

        if (val.value() != 0) {
            g.drawString(tile.toString(), xOffset + (SIDE >> 1) -MARGIN,
                    yOffset + (SIDE >> 1));
        }
    }

    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(BG_COLOR);
        g.setFont(STR_FONT);
        g.fillRect(0, 0, this.getSize().width, this.getSize().height);
        for (int x = 0; x < ROW; x++) {
            for (int y = 0; y < ROW; y++) {
                drawTile(g, tiles[x + y * ROW], x, y);
            }
        }
    }
}
