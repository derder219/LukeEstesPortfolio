package byog.Core;

import java.io.Serializable;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

public class UserCharacter implements Serializable {
    private int xPosition;
    private int yPosition;
    private String direction;

    public UserCharacter(int xPos, int yPos, String dir) {
        xPosition = xPos;
        yPosition = yPos;
        direction = dir;
    }

    protected TETile[][] addToWorld(TETile[][] thisWorld) {
        TETile[][] worldCopy = TETile.copyOf(thisWorld);
        TETile characterDisplay = directionalTile();
        worldCopy[xPosition][yPosition] = characterDisplay;
        return worldCopy;
    }

    private TETile directionalTile() {
        if (direction.equals("up")) {
            return Tileset.PLAYER1FF;
        }
        if (direction.equals("down")) {
            return Tileset.PLAYER1FB;
        }
        if (direction.equals("left")) {
            return Tileset.PLAYER1FL;
        } else {
            return Tileset.PLAYER1FR;
        }
    }

    /* Returns a copy of the world with the character in the spot
     * and facing the direction that results after the directional
     * key has been pressed.
     */
    protected TETile[][] move(char directionalKeyPressed, TETile[][] worldCopy, TETile floor) {
        if (directionalKeyPressed == 'a') { // Move left
            direction = "left";
            int tarXP = xPosition - 1;
            if (tarXP > -1) {
                if (isTraversible(worldCopy[tarXP][yPosition])) {
                    xPosition = tarXP;
                    worldCopy = addToWorld(worldCopy);
                    worldCopy[xPosition + 1][yPosition] = floor;
                } else {
                    worldCopy = addToWorld(worldCopy);
                }
            } else {
                worldCopy = addToWorld(worldCopy);
            }
        }
        if (directionalKeyPressed == 's') { // Move down
            direction = "down";
            int tarYP = yPosition - 1;
            if (tarYP > -1) {
                if (isTraversible(worldCopy[xPosition][tarYP])) {
                    yPosition = tarYP;
                    worldCopy = addToWorld(worldCopy);
                    worldCopy[xPosition][yPosition + 1] = floor;
                } else {
                    worldCopy = addToWorld(worldCopy);
                }
            } else {
                worldCopy = addToWorld(worldCopy);
            }
        }
        if (directionalKeyPressed == 'd') { // Move right
            direction = "right";
            int tarXP = xPosition + 1;
            if (tarXP < Game.WIDTH) {
                if (isTraversible(worldCopy[tarXP][yPosition])) {
                    xPosition = tarXP;
                    worldCopy = addToWorld(worldCopy);
                    worldCopy[xPosition - 1][yPosition] = floor;
                } else {
                    worldCopy = addToWorld(worldCopy);
                }
            } else {
                worldCopy = addToWorld(worldCopy);
            }
        }
        if (directionalKeyPressed == 'w') { // Move upward
            direction = "up";
            int tarYP = yPosition + 1;
            if (tarYP < Game.HEIGHT) {
                if (isTraversible(worldCopy[xPosition][tarYP])) {
                    yPosition = tarYP;
                    worldCopy = addToWorld(worldCopy);
                    worldCopy[xPosition][yPosition - 1] = floor;
                } else {
                    worldCopy = addToWorld(worldCopy);
                }
            } else {
                worldCopy = addToWorld(worldCopy);
            }
        }
        return worldCopy;
    }

    /* Returns if input TETile is traversible by a
     * player character.
     */
    protected boolean isTraversible(TETile x) {
        return x.equals(Tileset.FLOOR)
                || x.equals(Tileset.LIGHTGRASS)
                || x.equals(Tileset.LIGHTSNOW)
                || x.equals(Tileset.SAND);
    }

    protected String interact(TETile[][] currWorld) {
        if (direction.equals("down")) {
            return spotBelow(currWorld);
        }
        if (direction.equals("up")) {
            return spotUp(currWorld);
        }
        if (direction.equals("left")) {
            return spotLeft(currWorld);
        }
        if (direction.equals("right")) {
            return spotRight(currWorld);
        }
        return "What direction am I facing :o";
    }

    private String spotBelow(TETile[][] currWorld) {
        if (!inVertically(yPosition - 1)) {
            return "Ah, the abyss.";
        } else {
            return interactWithTile(currWorld[xPosition][yPosition - 1]);
        }
    }

    private String spotUp(TETile[][] currWorld) {
        if (!inVertically(yPosition + 1)) {
            return "Ah, the abyss.";
        } else {
            return interactWithTile(currWorld[xPosition][yPosition + 1]);
        }
    }

    private String spotLeft(TETile[][] currWorld) {
        if (!inHorizontally(xPosition - 1)) {
            return "Ah, the abyss.";
        } else {
            return interactWithTile(currWorld[xPosition - 1][yPosition]);
        }
    }

    private String spotRight(TETile[][] currWorld) {
        if (!inHorizontally(xPosition + 1)) {
            return "Ah, the abyss.";
        } else {
            return interactWithTile(currWorld[xPosition + 1][yPosition]);
        }
    }

    private boolean inVertically(int i) {
        return i > -1 && i < Game.HEIGHT;
    }

    private boolean inHorizontally(int i) {
        return i > -1 && i < Game.WIDTH;
    }

    private String interactWithTile(TETile tile) {
        if (isTraversible(tile)) {
            return "I could step here...";
        }
        if (tile.equals(Tileset.SNOWTREE)) {
            return "ICE to meet you. Haha! no? :(";
        }
        if (tile.equals(Tileset.TREE)) {
            return "WOOD you like to hear a joke?";
        }
        if (tile.equals(Tileset.FLOWER)) {
            return "Hey BUD! How's it GROWIN'?";
        }
        if (tile.equals(Tileset.GHOST)) {
            return "You put one spooker to rest.";
        }
        return "What is this tile :o";
    }

    protected int[] coordinatesFacing() {
        int[] temp = new int[2];
        if (direction.equals("down")) {
            temp[0] = xPosition;
            temp[1] = yPosition - 1;
        }
        if (direction.equals("up")) {
            temp[0] = xPosition;
            temp[1] = yPosition + 1;
        }
        if (direction.equals("left")) {
            temp[0] = xPosition - 1;
            temp[1] = yPosition;
        }
        if (direction.equals("right")) {
            temp[0] = xPosition + 1;
            temp[1] = yPosition;
        }
        return temp;
    }
}
