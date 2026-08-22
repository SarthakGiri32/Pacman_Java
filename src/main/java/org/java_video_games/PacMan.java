package org.java_video_games;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;
import java.util.HashSet;
import java.util.Objects;

public class PacMan extends JPanel implements ActionListener, KeyListener {
    public class Block {
        int x, y, width, height;
        Image image;
        char direction = 'U'; // Valid directions: 'U', 'D', 'L', 'R'
        int velocityX = 0, velocityY = 0; // default behavior for the pacman is to remain static and not move

        /*
        for restarting the game, need to save the starting positions of the ghost characters and
        the pacman character
         */
        int startX, startY;

        Block(Image image, int x, int y, int width, int height) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startX = x;
            this.startY = y;
        }

        // update direction and velocity based on the arrow key pressed
        void updateDirection(char direction) {
            char prevDirection = this.direction;
            this.direction = direction;
            updateVelocity();

            /*
            looping through the 'walls' hashset to ensure Pacman (or the ghosts) does not change directions
            if it is hitting a wall. The current (updated) direction of the character is changed to the previous
            direction, along with re-updating the velocity
             */
            this.x += this.velocityX;
            this.y += this.velocityY;
            for (Block wall: walls) {
                if (collision(this, wall)) {
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = prevDirection;
                    updateVelocity();
                }
            }
        }

        // update velocity based on the direction
        void updateVelocity() {
            // we are moving by 8px in every frame
            switch (this.direction) {
                case 'U' -> {
                    this.velocityX = 0;
                    this.velocityY = - tileSize / 4;
                }
                case 'D' -> {
                    this.velocityX = 0;
                    this.velocityY = tileSize / 4;
                }
                case 'L' -> {
                    this.velocityX = - tileSize / 4;
                    this.velocityY = 0;
                }
                case 'R' -> {
                    this.velocityX = tileSize / 4;
                    this.velocityY = 0;
                }
            }
        }
    }

    // variables explained in "App" class
    // This JPanel will have the same size as the JFrame main game window
    private final int rowCount = 21;
    private final int columnCount = 19;
    private final int tileSize = 32;
    private final int boardWidth = columnCount * tileSize;
    private final int boardHeight = rowCount * tileSize;

    private final Image wallImage;
    private final Image blueGhostImage;
    private final Image orangeGhostImage;
    private final Image pinkGhostImage;
    private final Image redGhostImage;

    private final Image pacmanUpImage;
    private final Image pacmanDownImage;
    private final Image pacmanLeftImage;
    private final Image pacmanRightImage;

    //X = wall, O = skip, P = pac man, ' ' = food
    //Ghosts: b = blue, o = orange, p = pink, r = red
    private final String[] tileMap = {
            "XXXXXXXXXXXXXXXXXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X                 X",
            "X XX X XXXXX X XX X",
            "X    X       X    X",
            "XXXX XXXX XXXX XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXrXX X XXXX",
            "O       bpo       O",
            "XXXX X XXXXX X XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXXXX X XXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X  X     P     X  X",
            "XX X X XXXXX X X XX",
            "X    X   X   X    X",
            "X XXXXXX X XXXXXX X",
            "X                 X",
            "XXXXXXXXXXXXXXXXXXX"
    };

    HashSet<Block> walls, foods, ghosts; // will be used for calculating and dealing with collisions
    Block pacman;

    /*
     * Create the game loop timer object to constantly re-paint the game window for every movement of
     * every moveable character on screen
     */
    Timer gameLoopTimer;

    PacMan() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);

        /*
        No need to create a separate key listener object, since the 'PacMan' class is implementing 'KeyListener'.
        The functions implemented by the 'PacMan' class from 'KeyListener' interface will be used by calling the
        'addKeyListener' function
         */
        addKeyListener(this);
        // To make sure that the 'PacMan' JPanel listens to the key presses, we are calling the 'setFocusable' function
        setFocusable(true);

        // load images: get the image from the corresponding image icon
        wallImage = new ImageIcon(Objects.requireNonNull(getClass().getResource(
                "/Game_Images/wall.png"))).getImage();
        blueGhostImage = new ImageIcon(Objects.requireNonNull(getClass().getResource(
                "/Game_Images/blueGhost.png"))).getImage();
        orangeGhostImage = new ImageIcon(Objects.requireNonNull(getClass().getResource(
                "/Game_Images/orangeGhost.png"))).getImage();
        pinkGhostImage = new ImageIcon(Objects.requireNonNull(getClass().getResource(
                "/Game_Images/pinkGhost.png"))).getImage();
        redGhostImage = new ImageIcon(Objects.requireNonNull(getClass().getResource(
                "/Game_Images/redGhost.png"))).getImage();

        pacmanUpImage = new ImageIcon(Objects.requireNonNull(getClass().getResource(
                "/Game_Images/pacmanUp.png"))).getImage();
        pacmanDownImage = new ImageIcon(Objects.requireNonNull(getClass().getResource(
                "/Game_Images/pacmanDown.png"))).getImage();
        pacmanLeftImage = new ImageIcon(Objects.requireNonNull(getClass().getResource(
                "/Game_Images/pacmanLeft.png"))).getImage();
        pacmanRightImage = new ImageIcon(Objects.requireNonNull(getClass().getResource(
                "/Game_Images/pacmanRight.png"))).getImage();

        // loading the tile map image objects in the object hash sets
        loadMap();

        /*
        How long it takes to start timer - or - milliseconds spent between frames:
        1. The game window will be re-painted every 50 milliseconds.
        2. Since there are 1000 milliseconds in a second, the frame-rate will be 20 FPS (1000 / 50)
         */
        gameLoopTimer = new Timer(50, this);
        gameLoopTimer.start();
    }

    public void loadMap() {
        // initializing the hashsets
        walls = new HashSet<>();
        foods = new HashSet<>();
        ghosts = new HashSet<>();

        // iterating through the tileMap array
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                String currentRow = tileMap[r];
                char currentTileMapChar = currentRow.charAt(c);

                /*
                x and y coordinates are based on the pixels on the screen; hence the 'r' and 'c' values are multiplied
                by the 'tileSize' pixel value to get the exact position to store the current game image location
                on the game window
                 */
                int x = c * tileSize;
                int y = r * tileSize;

                switch (currentTileMapChar) {
                    case 'X' -> { // block wall
                        Block wall = new Block(wallImage, x, y, tileSize, tileSize);
                        walls.add(wall);
                    }
                    case 'b' -> { // blue ghost
                        Block ghost = new Block(blueGhostImage, x, y, tileSize, tileSize);
                        ghosts.add(ghost);
                    }
                    case 'o' -> { // orange ghost
                        Block ghost = new Block(orangeGhostImage, x, y, tileSize, tileSize);
                        ghosts.add(ghost);
                    }
                    case 'p' -> { // pink ghost
                        Block ghost = new Block(pinkGhostImage, x, y, tileSize, tileSize);
                        ghosts.add(ghost);
                    }
                    case 'r' -> { // red ghost
                        Block ghost = new Block(redGhostImage, x, y, tileSize, tileSize);
                        ghosts.add(ghost);
                    }
                    case 'P' -> pacman = new Block(pacmanRightImage, x, y, tileSize, tileSize); // Pacman
                    case ' ' -> { // food
                        /*
                        the 'food' image has width = 4 and height = 4. So:
                            1. 32 - 4 = 28, which the remaining width and/or height of the empty tile area
                            2. 28 / 2 = 14
                            3. on adding 14 to both x and y coordinates, we will reach the center of the empty tile,
                               where we are leaving enough space for the 'food' image
                         */
                        Block food = new Block(null, x + 14, y + 14, 4, 4);
                        foods.add(food);
                    }
                }
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    // creating the graphics for the game window using the 32px by 32px images and the tile map
    public void draw(Graphics g) {
        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);

        for (Block ghost: ghosts) {
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        }

        for (Block wall: walls) {
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
        }

        g.setColor(Color.WHITE); // setting the color for 'food' image filled rectangles of size 4px by 4px
        for (Block food: foods) {
            g.fillRect(food.x, food.y, food.width, food.height); // since the 'food' image is just an empty rectangle
        }
    }

    // the position of the characters is changed based on the velocity and direction of the velocity
    public void move() {
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        // checking wall collisions
        for (Block wall: walls) {
            /*
            If pacman collides with a wall, the movement made to try to cross into the wall's rectangle
            will be reversed, and in that frame pacman stop before the wall.
             */
            if (collision(pacman, wall)) {
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                break; // once a wall has been encountered, we don't to check for any other wall collisions
            }
        }
    }

    /**
     * This function will detect collision between all characters inside the game.
     * A specific formula will be used to detect the collision between two rectangles on the screen.
     * Every food, ghost, wall, empty space - even Pacman (whose image is circular) - is a rectangle
     * @param a the first 'Block' character object
     * @param b the second 'Block' character object
     * @return the boolean result of the collision detection between the two 'Block' characters
     */
    public boolean collision(Block a, Block b) {
        // the collision detection formula:
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    /**
     * the positions of all movable characters is updated before the screen is repainted for every frame in
     * the game loop
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        move(); // update the character positions
        repaint(); // will call the 'paintComponent' method defined above
    }

    /**
     * Will listen and process the values from a keyboard key with a character (alphanumeric
     * or special characters). (We are only using arrow keys to move the 'Pacman' around,
     * we don't need the values from any character keys)
     * @param e the event to be processed
     */
    @Override
    public void keyTyped(KeyEvent e) {}

    /**
     * Will listen and process the action for a key being pressed and held in the pressed position.
     * (We are not interested in listening to a key press. The main change in the position of 'Pacman'
     * will happen when an arrow key is released after being pressed)
     * @param e the event to be processed
     */
    @Override
    public void keyPressed(KeyEvent e) {}

    /**
     * We will use this function to update the direction, velocity and directional image
     * of 'Pacman' in the game window
     * @param e the event to be processed
     */
    @Override
    public void keyReleased(KeyEvent e) {
//        System.out.println("KeyEvent: " + e.getKeyCode());
        // the key codes for the arrow keys are between 37 and 40 (inclusive)

        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP -> pacman.updateDirection('U');
            case KeyEvent.VK_DOWN -> pacman.updateDirection('D');
            case KeyEvent.VK_LEFT -> pacman.updateDirection('L');
            case KeyEvent.VK_RIGHT -> pacman.updateDirection('R');
        }

        switch (pacman.direction) {
            case 'U' -> pacman.image = pacmanUpImage;
            case 'D' -> pacman.image = pacmanDownImage;
            case 'L' -> pacman.image = pacmanLeftImage;
            case 'R' -> pacman.image = pacmanRightImage;
        }
        /*
        The pacman image change is not being done with the arrow-key press, because an arrow-key press does not result
        in a direction change if the arrow-key direction is blocked by a wall.
         */
    }
}
