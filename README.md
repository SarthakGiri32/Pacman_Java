# PACMAN Java Video Game  

This repo contains the code written while following this YouTube tutorial for creating a Pacman game using the Java 
Programming Language:  
- [Code Pacman in Java](https://youtu.be/lB_J-VNMVpE) from [Kenny Yip Coding](https://www.youtube.com/@KennyYipCoding)  

## Game Window Dimensions:  
- The window has been divided into tiles, which is considered the smallest unit in the window screen for the game  
- Each tile has a width of 32px and a height of 32px  
- In terms of the tiles:  
  - The number of columns is 19 (indexed 0 to 18)  
  - The number of rows is 21 (indexed 0 to 20)  
- So, the size of the game window is:  
  - Width: 19 * 32px  
  - Height: 21 * 32px  

## Features to add:  

\(see [CHANGELOG](CHANGELOG) for specific feature implementation dates\)  

### 1. Main Features:
- [ ] A loop around feature for the pacman character (partially completed):  
  - [ ] If the pacman goes beyond the up/down/left/right border of the game window, it should loop around to the 
        opposite side and keep moving in the same direction
  - [x] implement a left/right looping for the 9th row of the map, based on the tutorial tilemap
- [ ] A pause/start feature, based on pressing a key on the keyboard to start/stop the game
- [ ] A high-score tracker, which keeps a track of the highest score ever achieved in the game across all game 
      restarts  

### 2. Other Features:  
- A 'power-pellet' boost:
  - Create a 'power-pellet' food item, which converts the ghosts into scared-ghosts. These 'scared-ghost' characters
    cannot harm pacman for a certain period of time, after which the ghosts will revert back to their default form.
    (A 'scared-ghost' sprite is already provided with the tutorial image files)
- Add the 'cherry' sprites as a feature of your own
- Implement an advanced movement mechanic for the ghosts so that they actually try to follow pacman around the map
- You can create your own tilemap, and even add new tilemaps for higher levels reached by consuming all food pellets
  in a lower level's map
