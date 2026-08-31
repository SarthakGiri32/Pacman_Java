package org.java_video_games;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Objects;
import java.util.Random;

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
        Image startImage;

        Block(Image image, int x, int y, int width, int height) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startX = x;
            this.startY = y;
            this.startImage = image;
        }

        // update direction and velocity based on the arrow key pressed
        protected void updateDirection(char direction) {
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
            for (Block wall : walls) {
                if (collision(this, wall)) {
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = prevDirection;
                    updateVelocity();
                }
            }
        }

        // update velocity based on the direction
        private void updateVelocity() {
            // we are moving by 8px in every frame
            switch (this.direction) {
                case 'U' -> {
                    this.velocityX = 0;
                    this.velocityY = -tileSize / 4;
                }
                case 'D' -> {
                    this.velocityX = 0;
                    this.velocityY = tileSize / 4;
                }
                case 'L' -> {
                    this.velocityX = -tileSize / 4;
                    this.velocityY = 0;
                }
                case 'R' -> {
                    this.velocityX = tileSize / 4;
                    this.velocityY = 0;
                }
            }
        }

        // resets the x and y positions of pacman and ghosts after a collision
        protected void reset() {
            this.x = this.startX;
            this.y = this.startY;
            this.image = this.startImage;
        }
    }

    // variables explained in "App" class
    // This JPanel will have the same size as the JFrame main game window
    private final int rowCount = 22;
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
            "OOOOOOOOOOOOOOOOOOO",
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

    private HashSet<Block> walls, foods, ghosts; // will be used for calculating and dealing with collisions
    private Block pacman;

    /*
     * Create the game loop timer object to constantly re-paint the game window for every movement of
     * every moveable character on screen
     */
    private final Timer gameLoopTimer;

    private final char[] directions = {'U', 'D', 'L', 'R'}; // up, down, left and right
    private final Random random = new Random();
    // for each ghost, we are randomly selecting the direction

    // variables to track interactions between pacman, the ghosts and the food
    private int score = 0; // tracks the food score of pacman
    private int lives = 3; // the default lives of pacman
    private boolean gameOver = false;
    /*
    Pacman has 3 lives (by default). If pacman collides with a ghost, it will lose 1 life. If pacman loses all 3 lives,
    'gameOver' is set to true. If 'gameOver' is true, the player is unable to move pacman.
     */

    private int level = 1; // variable to track the level
    private boolean isGamePaused = false; // boolean variable to keep track of the game's paused/resumed state

    /*
    keeps track of the highest score ever achieved in the game across all game restarts after game-overs
     */
    private int allTimeHighScore = 0;

    protected PacMan() {
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

        for (Block ghost : ghosts) {
            // a new direction is selected randomly from the 'directions' array
            char newDirection = directions[random.nextInt(4)];
            // we update each ghost's direction and velocity based on the new random direction
            ghost.updateDirection(newDirection);
        }

        /*
        How long it takes to start timer - or - milliseconds spent between frames:
        1. The game window will be re-painted every 50 milliseconds.
        2. Since there are 1000 milliseconds in a second, the frame-rate will be 20 FPS (1000 / 50)
         */
        gameLoopTimer = new Timer(50, this);
        gameLoopTimer.start();
    }

    private void loadMap() {
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
    private void draw(Graphics g) {
        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);

        for (Block ghost : ghosts) {
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        }

        for (Block wall : walls) {
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
        }

        g.setColor(Color.WHITE); // setting the color for 'food' image filled rectangles of size 4px by 4px
        for (Block food : foods) {
            g.fillRect(food.x, food.y, food.width, food.height); // since the 'food' image is just an empty rectangle
        }

        // score/'game over' display
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        if (gameOver) {
            g.drawString("Game Over! Final Score: " + score + " Final Level: " + level + " All-Time High Score: "
                    + allTimeHighScore, tileSize / 4, tileSize / 2);
            g.drawString("Press 'Space' to play again", tileSize / 4, tileSize + 1);
        } else {
            g.drawString("x" + lives + " Score: " + score + " Level: " + level + " All-Time High Score: "
                    + allTimeHighScore, tileSize / 4, tileSize / 2);
            g.drawString("Press 'P' to pause/resume the game", tileSize / 4, tileSize + 1);
        }
    }

    // the position of the characters is changed based on the velocity and direction of the velocity
    private void move() {
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        // checking wall collisions
        for (Block wall : walls) {
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

        /*
        if pacman encounters a boundary wall of the game window with no 'wall' block object, it will teleport to the
        opposite side of the window on the (in the default tutorial tile map) same row
         */
        beyondBoundaryMoveLoop(pacman);

        // check ghost collisions
        for (Block ghost : ghosts) {
            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;
            for (Block wall : walls) {
                if (collision(ghost, wall)) {
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    // a ghost should change directions immediately on colliding with a wall
                    char newDirection = directions[random.nextInt(4)];
                    ghost.updateDirection(newDirection);
                }
            }

            /*
            This if case is only valid for the default tutorial tile map. if the ghost is moving left or right on the
            9th row, then either up or down will be selected as the direction for the ghost randomly
             */
            if (ghost.y == tileSize * 10 && ghost.direction != 'U' && ghost.direction != 'D') {
                ghost.updateDirection(directions[random.nextInt(2)]);
            }

            beyondBoundaryMoveLoop(ghost);

            /*
            collision will be checked between pacman and ghosts, and their positions will be reset to their starting
            positions
             */
            if (collision(ghost, pacman)) {
                lives -= 1;
                if (lives == 0) {
                    // we need to stop moving the pacman and ghosts if lives == 0
                    gameOver = true;
                    if (allTimeHighScore < score) {
                        allTimeHighScore = score;
                    }
                    return;
                }
                resetPositions();
            }
        }

        // check food collisions
        /*
        Everytime pacman 'eats' a food block, the block should be removed from the hashset, since the food has been
        'consumed' by pacman and doesn't exist anymore. Also, 10 points will be added to the 'score' variables for
        every food item consumed by pacman.
         */
        Block foodEaten = null;
        for (Block food : foods) {
            if (collision(pacman, food)) {
                foodEaten = food; // pacman will 'eat' the food
                score += 10;
                break;
            }
        }
        foods.remove(foodEaten);

        /*
        if all food has been consumed by pacman, we progress to the next level (in the default tutorial, the next
        level's tile map is the same as the 1st level's)
         */
        if (foods.isEmpty()) {
            level += 1;
            loadMap();
            resetPositions();
        }
    }

    // handles beyond game window boundary movement for pacman and ghosts
    private void beyondBoundaryMoveLoop(Block movableCharacter) {
        if (movableCharacter.x <= 0) { // crossing the left border beyond the game window
            movableCharacter.x = boardWidth - movableCharacter.width;
        } else if (movableCharacter.x + movableCharacter.width >= boardWidth) { // crossing the right border beyond the window
            movableCharacter.x = 0;
        } else if (movableCharacter.y <= 0) { // crossing beyond the upper boundary of the game window
            movableCharacter.y = boardHeight - movableCharacter.height;
        } else if (movableCharacter.y + movableCharacter.height >= boardHeight) { // crossing beyond the lower boundary of the game window
            movableCharacter.y = 0;
        }
    }

    /**
     * This function will detect collision between all characters inside the game.
     * A specific formula will be used to detect the collision between two rectangles on the screen.
     * Every food, ghost, wall, empty space - even Pacman (whose image is circular) - is a rectangle
     *
     * @param a the first 'Block' character object
     * @param b the second 'Block' character object
     * @return the boolean result of the collision detection between the two 'Block' characters
     */
    private boolean collision(Block a, Block b) {
        // the collision detection formula:
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    private void resetPositions() {
        pacman.reset();

        /*
        stop the existing movement of pacman; essentially, wait for the user input to move pacman after its position
        has been reset
         */
        pacman.velocityX = 0;
        pacman.velocityY = 0;

        /*
        do the same position reset for all ghosts, and set a new random movement direction and velocity for the ghosts
        from their starting positions
         */
        for (Block ghost : ghosts) {
            ghost.reset();
            char directionAfterReset = directions[random.nextInt(4)];
            ghost.updateDirection(directionAfterReset);
        }
    }

    /**
     * the positions of all movable characters is updated before the screen is repainted for every frame in
     * the game loop
     *
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        move(); // update the character positions
        repaint(); // will call the 'paintComponent' method defined above

        // if 'gameOver' is true, we need to stop the game loop (stop moving and drawing for all characters)
        if (gameOver) {
            gameLoopTimer.stop();
        }
    }

    /**
     * Will listen and process the values from a keyboard key with a character (alphanumeric
     * or special characters). (We are only using arrow keys to move the 'Pacman' around,
     * we don't need the values from any character keys)
     *
     * @param e the event to be processed
     */
    @Override
    public void keyTyped(KeyEvent e) {
    }

    /**
     * Will listen and process the action for a key being pressed and held in the pressed position.
     * When an arrow key is pressed, the pacman starts moving. We will also use this function to update
     * the direction, velocity and directional image of 'Pacman' in the game window
     *
     * @param e the event to be processed
     */
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP -> pacman.updateDirection('U');
            case KeyEvent.VK_DOWN -> pacman.updateDirection('D');
            case KeyEvent.VK_LEFT -> pacman.updateDirection('L');
            case KeyEvent.VK_RIGHT -> pacman.updateDirection('R');
            default -> {
                /*
                any key pressed and held other than an arrow key is ignored by the pacman sprite image selection
                switch case below
                 */
                return;
            }
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

    /**
     * The pacman will stop moving when an already pressed arrow key is released. Also, we will restart the
     * game in this window based on the 'space' key being released on the keyboard, and the 'gameOver'
     * condition being true.
     *
     * @param e the event to be processed
     */
    @Override
    public void keyReleased(KeyEvent e) {
        // the key codes for the arrow keys are between 37 and 40 (inclusive)
//        System.out.println(e.getExtendedKeyCode());
        // stopping the movement of the pacman when an arrow key is released
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT -> {
                pacman.velocityX = 0;
                pacman.velocityY = 0;
            }
            case KeyEvent.VK_P -> { // game pause/resume mechanic
                if (!isGamePaused) {
                    isGamePaused = true;
                    gameLoopTimer.stop();
                } else {
                    isGamePaused = false;
                    gameLoopTimer.start();
                }
            }
        }

        if (gameOver && e.getKeyCode() == KeyEvent.VK_SPACE) {
            loadMap(); // reload the default tile map (along-with all 'eaten' food tiles)
            resetPositions(); // resets the positions of ghosts and pacman
            lives = 3;
            score = 0;
            level = 1;
            gameOver = false;
            gameLoopTimer.start();
        }
    }
}
