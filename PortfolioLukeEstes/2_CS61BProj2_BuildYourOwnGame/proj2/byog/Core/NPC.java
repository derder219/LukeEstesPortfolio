package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.io.Serializable;

public class NPC implements Serializable {
    private int xPosition;
    private int yPosition;
    private boolean putToRest; // Whether or not the ghost has been put to rest.

    public NPC(int xPos, int yPos, boolean pTR) {
        xPosition = xPos;
        yPosition = yPos;
        putToRest = pTR;
    }

    protected TETile[][] addToWorld(TETile[][] thisWorld) {
        TETile[][] worldCopy = TETile.copyOf(thisWorld);
        worldCopy[xPosition][yPosition] = Tileset.GHOST;
        return worldCopy;
    }

    protected boolean inPeace() {
        return putToRest;
    }

    protected int getxPosition() {
        return xPosition;
    }

    protected int getyPosition() {
        return yPosition;
    }

    protected void putToRest() {
        putToRest = true;
    }
}
