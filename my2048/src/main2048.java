/**
 * Created by ShuqiChen on 2017/7/26.
 * 2048 in Swing
 * This is the main file
 */

import java.awt.*;
import javax.swing.*;

//main class
public class main2048 extends JFrame {

    private static final long serialVersionUID = 1L;

    JLabel statusBar;
    private static final String TITLE = "2048";
    //result messages
    public static final String WIN_MSG = "Congrats! Continue?";
    public static final String LOSE_MSG = "Lose. You can press R to reset the game.";

    public static void main(String[] args) {
        main2048 game = new main2048();
        Grid board = new Grid(game);
        if (args.length != 0 && args[0].matches("[0-9]*")){
            Grid.goal = NumbersAndColors.of(Integer.parseInt(args[0]));
        }
        KeyPress kb = KeyPress.getKeyPress(board);
        board.addKeyListener(kb);
        game.add(board);

        game.setLocationRelativeTo(null);
        game.setVisible(true);
    }

    //constructor
    public main2048(){
        setTitle(TITLE);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(340, 400);
        setResizable(false); //ToDo

        statusBar = new JLabel("");
        add(statusBar, BorderLayout.SOUTH);
    }

    void win() {
        statusBar.setText(WIN_MSG);
    }

    void lose() {
        statusBar.setText(LOSE_MSG);
    }
}
