package org.java_video_games;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class PacMan extends JPanel {
    // variables explained in "App" class
    // This JPanel will have the same size as the JFrame main game window
    private final int rowCount = 21;
    private final int columnCount = 19;
    private final int tileSize = 32;
    private final int boardWidth = columnCount * tileSize;
    private final int boardHeight = rowCount * tileSize;

    private Image wallImage;
    private Image blueGhostImage;
    private Image orangeGhostImage;
    private Image pinkGhostImage;
    private Image redGhostImage;

    private Image pacmanUpImage;
    private Image pacmanDownImage;
    private Image pacmanLeftImage;
    private Image pacmanRightImage;

    PacMan() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);

        // load images: get the image from the corresponding image icon
        wallImage = new ImageIcon(Objects.requireNonNull(getClass().getResource(
                "../../../resources/Game_Images/wall.png"))).getImage();
        blueGhostImage = new ImageIcon(Objects.requireNonNull(getClass().getResource(
                "../../../resources/Game_Images/blueGhost.png"))).getImage();
        orangeGhostImage = new ImageIcon(Objects.requireNonNull(getClass().getResource(
                "../../../resources/Game_Images/orangeGhost.png"))).getImage();
        pinkGhostImage = new ImageIcon(Objects.requireNonNull(getClass().getResource(
                "../../../resources/Game_Images/pinkGhost.png"))).getImage();
        redGhostImage = new ImageIcon(Objects.requireNonNull(getClass().getResource(
                "../../../resources/Game_Images/redGhost.png"))).getImage();

        pacmanUpImage = new ImageIcon(Objects.requireNonNull(getClass().getResource(
                "../../../resources/Game_Images/pacmanUp.png"))).getImage();
        pacmanDownImage = new ImageIcon(Objects.requireNonNull(getClass().getResource(
                "../../../resources/Game_Images/pacmanDown.png"))).getImage();
        pacmanLeftImage = new ImageIcon(Objects.requireNonNull(getClass().getResource(
                "../../../resources/Game_Images/pacmanLeft.png"))).getImage();
        pacmanRightImage = new ImageIcon(Objects.requireNonNull(getClass().getResource(
                "../../../resources/Game_Images/pacmanRight.png"))).getImage();
    }
}
