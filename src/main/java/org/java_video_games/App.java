package org.java_video_games;

import javax.swing.JFrame;

public class App extends PacMan {
    public static void main(String[] args) {
        JFrame gameWIndowJFrame = getGameWIndowJFrame();

        // JPanel will be created in a separate class to draw and display the game graphics
        PacMan pacManGameJPanel = new PacMan();
        gameWIndowJFrame.add(pacManGameJPanel);
        gameWIndowJFrame.pack(); // to "fill out" the JPanel inside the JFrame
        // the pacman JPanel will now get the keyboard input focus
        pacManGameJPanel.requestFocus();
        // as a coding convention, the JFrame should be made visible after all components have been added
        gameWIndowJFrame.setVisible(true);
    }

    private static JFrame getGameWIndowJFrame() {
        // the number of tiles in the rows and columns of the main game window
        int rowCount = 22;
        int columnCount = 19;

        // the tile size and overall game window dimensions
        int tileSize = 32;
        int boardWidth = columnCount * tileSize;
        int boardHeight = rowCount * tileSize;

        // main game window
        JFrame gameWIndowJFrame = new JFrame("Pac Man");
        gameWIndowJFrame.setSize(boardWidth, boardHeight);
        gameWIndowJFrame.setLocationRelativeTo(null);
        gameWIndowJFrame.setResizable(false); // user cannot resize the game window
        // terminates the game when user closes the game window
        gameWIndowJFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        return gameWIndowJFrame;
    }
}
