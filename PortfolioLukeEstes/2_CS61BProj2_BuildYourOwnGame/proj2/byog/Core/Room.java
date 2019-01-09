package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import java.io.Serializable;

public class Room implements Serializable {
    private int width; // A given rooms width.
    private int height; // A given rooms height.
    private int xIndex; // A given rooms starting xIndex.
    private int yIndex; // A given rooms staring yIndex.
    private int typeIdentifier; // Number given to identify type of room made.
    protected int roomListIndex; // Index of a room in the Game.roomList list.
    private int cLRI; // Index of room closest to the left of this room.
    //private int cRRI; // Index of room closest to the right of this room.
    private int cARI; // Index of room closest above this room.
    //private int cBRI; // Index of room closest below this room.
    // roomList indices of rooms that this one is connected to.
    protected int[] connectedRoomsIndices = new int[0];

    /* Creates new Room with pseudorandomly generated field values,
     * created from Game's generator,
     * and default connected values:
     * room is rectangular type
     * and it is not connected to anything yet.
     */
    protected Room() {
        width = Game.generator.nextInt(Game.maxWidth) + 2;
        height = Game.generator.nextInt(Game.maxHeight) + 2;
        xIndex = Game.generator.nextInt(Game.WIDTH);
        yIndex = Game.generator.nextInt(Game.HEIGHT);
        typeIdentifier = 0;
    }

    /* Returns rooms source xIndex. */
    protected int getXIndex() {
        return this.xIndex;
    }

    /* Returns rooms source yIndex. */
    protected int getYIndex() {
        return this.yIndex;
    }

    /* Returns max x index value of this room. */
    protected int getXEndex() {
        return xIndex + width - 1;
    }

    /* Returns max y index value of this room. */
    protected int getYEndex() {
        return yIndex + height - 1;
    }

    /* Returns rooms type identifying number. */
    protected int getTypeIdentifier() {
        return this.typeIdentifier;
    }

    protected boolean isConnected() {
        return connectedRoomsIndices.length > 1;
    }

    /* Returns unique xCoordinates of the given room. */
    private int[] outerXCoordinates() {
        int[] xOuterCoordinates = new int[width];
        for (int x = 0; x < width; x++) {
            xOuterCoordinates[x] = xIndex + x;
        }
        return xOuterCoordinates;
    }

    /* Returns unique yCoordinates of the given room. */
    private int[] outerYCoordinates() {
        int[] yOuterCoordinates = new int[height];
        for (int y = 0; y < height; y++) {
            yOuterCoordinates[y] = yIndex + y;
        }
        return yOuterCoordinates;
    }

    /* Returns if this room has a matching x coordinate with the currRoom being looked at. */
    private boolean sharedXCoordinates(Room currRoom) {
        int[] myXCoordinates = outerXCoordinates();
        int[] currRoomXCoordinates = currRoom.outerXCoordinates();
        for (int myX: myXCoordinates) {
            for (int currX: currRoomXCoordinates) {
                if (myX == currX) {
                    return true;
                }
            }
        }
        return false;
    }

    /* Returns if this room has a matching y coordinate with the currRoom being looked at. */
    private boolean sharedYCoordinates(Room currRoom) {
        int[] myYCoordinates = outerYCoordinates();
        int[] currRoomYCoordinates = currRoom.outerYCoordinates();
        for (int myY: myYCoordinates) {
            for (int currY: currRoomYCoordinates) {
                if (myY == currY) {
                    return true;
                }
            }
        }
        return false;
    }

    /* Returns if this room has at least
     * one matching x,y coordinate with
     * the current room being looked at.
     */
    private boolean hasMatchingCoordinates(Room currRoom) {
        return sharedXCoordinates(currRoom) && sharedYCoordinates(currRoom);
    }

    /* Returns whether or not the given room
     * touches any rooms already created.
     */
    public boolean overlapsARoom() {
        for (int i = 0; i < Game.roomList.length; i++) {
            if (Game.roomList[i] != null) {
                if (hasMatchingCoordinates(Game.roomList[i])) {
                    return true;
                }
            }
        }
        return false;
    }

    /* Returns if the room overextends in the xy direction. */
    public boolean overExtends() {
        return (xIndex + width - 1 >= Game.WIDTH) || (yIndex + height - 1 >= Game.HEIGHT);
    }

    /* Resets the rooms xIndex and yIndex to maintain the same size
     * but be placed in a valid location for a room of that size.
     * This location is determined by the difference from the end of
     * the map the room extends. If it doesn't overextend, it will
     * maintain the same indices.
     */
    public void retargetRoom() {
        // Number of tiles from xIndex to the end of the map.
        int xTileLimit = Game.WIDTH - xIndex;
        // Number of tiles from yIndex to the end of the map.
        int yTileLimit = Game.HEIGHT - yIndex;
        // How many tiles from the end of the map in the x-direction.
        int xDiff = xTileLimit - width;
        // How many tiles from the end of the map in the y-direction.
        int yDiff = yTileLimit - height;
        // Resets values accordingly.
        if (xDiff < 0) {
            xIndex = xIndex + xDiff;
        }
        if (yDiff < 0) {
            yIndex = yIndex + yDiff;
        }
    }

    /* Takes in the world and outputs a new copied world with the RectangularRoom added. */
    private TETile[][] addRectangularRoom(TETile[][] world) {
        TETile[][] worldCopy = TETile.copyOf(world);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                worldCopy[xIndex + x][yIndex + y] = Tileset.FLOOR;
            }
        }
        return worldCopy;
    }

    /* Takes in the world and outputs a new world with the DiamondRoom added.
    public TETile[][] addDiamondRoom(TETile[][] world) {
        return world;
    }
    */

    /* Invoked my a room to add itself to a copy of the input world.
     * Then returns the copied world with the room added correctly.
     */
    public TETile[][] addToWorld(TETile[][] world) {
        TETile[][] worldCopy = TETile.copyOf(world);
        if (typeIdentifier == 0) {
            // Adds the rectangular room to the worldCopy.
            worldCopy = addRectangularRoom(worldCopy);
        } /*else {
            // Adds other rooms types to the worldCopy.
        } */
        return worldCopy;
    }

    /* Creates indices of all rooms to the left of this room,
     * and will output the Game.roomList index of the
     * relatively closest left room.
     */
    private int findClosestLeftRoomIndex() {
        int[] leftRoomIndices = leftRoomIndices();
        if (leftRoomIndices.length == 0) {
            return Game.roomList.length; // Returns roomList length if no close rooms.
        }
        int output = 0;
        int closestDiff = xIndex - Game.roomList[leftRoomIndices[output]].getXEndex();
        for (int i = 1; i < leftRoomIndices.length; i++) {
            int currDiff = xIndex - Game.roomList[leftRoomIndices[i]].getXEndex();
            if (currDiff < closestDiff) {
                closestDiff = currDiff;
                output = i;
            }
        }
        return leftRoomIndices[output];
    }

    /* Returns Game.roomList indices of
     * rooms to the left of this room.
     */
    private int[] leftRoomIndices() {
        int[] leftI = new int[0];
        for (int i = 0; i < Game.numRooms; i++) {
            if (i != roomListIndex) {
                if (roomOffToLeft(Game.roomList[i])) {
                    leftI = appendAndResizeIndex(leftI, i);
                }
            }
        }
        return leftI;
    }

    /* Returns if tarRoom is offset to the left of this room. */
    private boolean roomOffToLeft(Room tarRoom) {
        return sharedYCoordinates(tarRoom) && xIndex - tarRoom.getXEndex() > 1;
    }

    /* Creates indices of all rooms above this room,
     * and will output the Game.roomList index of the
     * relatively closest right room.
     */
    private int findClosestAboveRoomIndex() {
        int[] aboveRoomIndices = aboveRoomIndices();
        if (aboveRoomIndices.length == 0) {
            return Game.roomList.length; // Returns roomList length if no close rooms.
        }
        int output = 0;
        int closestDiff = Game.roomList[aboveRoomIndices[output]].getYIndex() - getYEndex();
        for (int i = 1; i < aboveRoomIndices.length; i++) {
            int currDiff = Game.roomList[aboveRoomIndices[i]].getYIndex() - getYEndex();
            if (currDiff < closestDiff) {
                closestDiff = currDiff;
                output = i;
            }
        }
        return aboveRoomIndices[output];
    }

    /* Returns Game.roomList indices of
     * rooms to the right of this room.
     */
    private int[] aboveRoomIndices() {
        int[] rightI = new int[0];
        for (int i = 0; i < Game.numRooms; i++) {
            if (i != roomListIndex) {
                if (roomOffAbove(Game.roomList[i])) {
                    rightI = appendAndResizeIndex(rightI, i);
                }
            }
        }
        return rightI;
    }

    /* Returns if tarRoom is offset above this room. */
    private boolean roomOffAbove(Room tarRoom) {
        return sharedXCoordinates(tarRoom) && tarRoom.getYIndex() - getXEndex() > 1;
    }

    protected void setupRoomConnections() {
        cLRI = findClosestLeftRoomIndex();
        //cRRI = findClosestRightRoomIndex();
        cARI = findClosestAboveRoomIndex();
        /*cBRI = findClosestBelowRoomIndex();
        */
    }

    /* Creates a copy of the world and returns it with this room
     * connected to closest rooms in each direction if they
     * aren't already connected.
     */
    protected TETile[][] connectFour(TETile[][] world) {
        TETile[][] currWorld = TETile.copyOf(world);
        int roomListLength = Game.roomList.length;
        if (cLRI != roomListLength) {
            if (!alreadyConnected(Game.roomList[cLRI])) {
                currWorld = connectToXYRoom(cLRI, currWorld);
            }
        }
        if (cARI != roomListLength) {
            if (!alreadyConnected(Game.roomList[cARI])) {
                currWorld = connectToXYRoom(cARI, currWorld);
            }
        }
        return currWorld;
    }

    /* Creates a copy of the world and returns it with
     * this room correctly connected to tarRoom.
     */
    private TETile[][] connectToXYRoom(int tarRoomIndex, TETile[][] world) {
        TETile[][] currWorld = TETile.copyOf(world);
        Room tarRoom = Game.roomList[tarRoomIndex];
        /* Connects the two rooms in the correct direction
         * and then updates their connectedRoomIndices.
         */
        if (tarRoomIndex == cLRI) {
            currWorld = connectLeft(tarRoom, currWorld);
        }
        if (tarRoomIndex == cARI) {
            currWorld = connectAbove(tarRoom, currWorld);
        }
        updateCRIs(tarRoom);
        return currWorld;
    }

    /* Creates a copy of world and returns that copy
     * with this room connected leftward to tarRoom.
     */
    private TETile[][] connectLeft(Room tarRoom, TETile[][] world) {
        TETile[][] currWorld = TETile.copyOf(world);
        int y = lowestSharedYValue(tarRoom);
        for (int i = 1; i < xIndex - tarRoom.getXEndex(); i++) {
            currWorld[xIndex - i][y] = Tileset.FLOOR;
        }
        return currWorld;
    }

    /* Creates a copy of world and returns that copy
     * with this room connected upwards to tarRoom.
     */
    private TETile[][] connectAbove(Room tarRoom, TETile[][] world) {
        TETile[][] currWorld = TETile.copyOf(world);
        int x = lowestSharedXValue(tarRoom);
        int endY = getYEndex();
        for (int i = 1; i < tarRoom.getYIndex() - endY; i++) {
            currWorld[x][endY + i] = Tileset.FLOOR;
        }
        return currWorld;
    }

    /* Returns lowest shared y value between this room and
     * tarRoom if they share y values.
     */
    private int lowestSharedYValue(Room tarRoom) {
        int srcI = yIndex;
        int tarI = tarRoom.getYIndex();
        if (srcI > tarI) { // If this room starts above tarRoom.
            return srcI; // Branch off this rooms starting index.
        } else {
            return tarI; // Else branch off minimum shared y-value.
        }
    }

    /* Returns lowest shared x value between this room and
     * tarRoom if they share x values.
     */
    private int lowestSharedXValue(Room tarRoom) {
        int srcI = xIndex;
        int tarI = tarRoom.getXIndex();
        if (srcI > tarI) { // If this room starts above tarRoom.
            return srcI; // Branch off this rooms starting index.
        } else {
            return tarI; // Else branch off minimum shared y-value.
        }
    }

    /* Resizes given int array to its size + 1, with
     * the input int appended to it.
     */
    protected static int[] appendAndResizeIndex(int[] array, int roomLIndexToAdd) {
        int[] temp = new int[array.length + 1];
        System.arraycopy(array, 0, temp, 0, array.length);
        temp[array.length] = roomLIndexToAdd;
        return temp;
    }

    /* Returns an array with the two input arrays values
     * concatenated together.
     */
    private static int[] appendAndResizeIndices(int[] array, int[] array2) {
        int[] temp = new int[array.length + array2.length];
        System.arraycopy(array, 0, temp, 0, array.length);
        System.arraycopy(array2, 0, temp, array.length, array2.length);
        return temp;
    }

    /* Returns whether or not the current room is already
     * connected to tarRoom.
     */
    private boolean alreadyConnected(Room tarRoom) {
        for (int i = 0; i < connectedRoomsIndices.length; i++) {
            if (connectedRoomsIndices[i] == tarRoom.roomListIndex) {
                return true;
            }
        }
        return false;
    }

    protected TETile[][] takeTheMidnightTrain(TETile[][] world) {
        int cityBRI = findCityBoyIndex();
        world = connectTo(Game.roomList[cityBRI], world);
        return world;
    }

    private int findCityBoyIndex() {
        // If every room is a small town girl, uses a different method!
        if (noCityBoys()) {
            return closestSmallTownGirl();
        }
        // Starting at roomList index 0, find closest city boy...
        int output = 0;
        // Increase until it isn't already connected to tarRoom,
        // tarRoom is connected itself,
        // and output isn't currRoom!
        while (roomListIndex == output || !Game.roomList[output].isConnected()
                || alreadyConnected(Game.roomList[output])) {
            output += 1;
        }
        int temp = output;
        int closestDiff = tilesToRoom(Game.roomList[temp]);
        for (int i = temp + 1; i < Game.numRooms; i++) {
            if (i != roomListIndex && !(alreadyConnected(Game.roomList[i]))
                    && Game.roomList[i].isConnected()) {
                int currDiff = tilesToRoom(Game.roomList[i]);
                if (currDiff < closestDiff) {
                    closestDiff = currDiff;
                    output = i;
                }
            }
        }
        return output;
    }

    private int closestSmallTownGirl() {
        int output = 0;
        if (roomListIndex == output) {
            output += 1;
        }
        int temp = output;
        int closestDiff = tilesToRoom(Game.roomList[temp]);
        for (int i = temp + 1; i < Game.numRooms; i++) {
            if (i != roomListIndex) {
                int currDiff = tilesToRoom(Game.roomList[i]);
                if (currDiff < closestDiff) {
                    closestDiff = currDiff;
                    output = i;
                }
            }
        }
        return output;
    }

    /* Returns a copy of the world after connecting to given city boy room, which is determined by
     * a different method.
     */
    private TETile[][] connectTo(Room myCityBoy, TETile[][] world) {
        int[] relevantRD = relevantRoomDirection(myCityBoy);
        if (bottomRight(relevantRD)) { // Bottom right.
            for (int y = getYIndex() - 1; y > myCityBoy.getYEndex() - 1; y--) {
                world[getXEndex()][y] = Tileset.FLOOR;
            }
            for (int x = getXEndex() + 1; x < myCityBoy.getXIndex(); x++) {
                world[x][myCityBoy.getYEndex()] = Tileset.FLOOR;
            }
        }
        if (topLeft(relevantRD)) { // Top left.
            for (int y = getYEndex() + 1; y < myCityBoy.getYIndex() + 1; y++) {
                world[getXIndex()][y] = Tileset.FLOOR;
            }
            for (int x = getXIndex() - 1; x > myCityBoy.getXEndex(); x--) {
                world[x][myCityBoy.getYIndex()] = Tileset.FLOOR;
            }
        }
        if (topRight(relevantRD)) { // Top right.
            for (int y = getYEndex() + 1; y < myCityBoy.getYIndex() + 1; y++) {
                world[getXEndex()][y] = Tileset.FLOOR;
            }
            for (int x = getXEndex() + 1; x < myCityBoy.getXIndex(); x++) {
                world[x][myCityBoy.getYIndex()] = Tileset.FLOOR;
            }
        }
        if (bottomLeft(relevantRD)) { // Bottom left.
            for (int y = getYIndex() - 1; y > myCityBoy.getYEndex() - 1; y--) {
                world[getXIndex()][y] = Tileset.FLOOR;
            }
            for (int x = getXIndex() - 1; x > myCityBoy.getXEndex(); x--) {
                world[x][myCityBoy.getYEndex()] = Tileset.FLOOR;
            }
        }
        updateCRIs(myCityBoy);
        return world;
    }

    private int tilesToRoom(Room tarRoom) {
        int[] relevantRD = relevantRoomDirection(tarRoom);
        int xTiles = 0;
        int yTiles = 0;
        if (bottomRight(relevantRD)) { // Bottom right.
            xTiles = tarRoom.getXIndex() - getXEndex();
            yTiles = getYIndex() - tarRoom.getYEndex();
        }
        if (topLeft(relevantRD)) { // Top left.
            xTiles = getXIndex() - tarRoom.getXEndex();
            yTiles = tarRoom.getYIndex() - getYEndex();
        }
        if (topRight(relevantRD)) { // Top right.
            xTiles = tarRoom.getXIndex() - getXEndex();
            yTiles = tarRoom.getYIndex() - getYEndex();
        }
        if (bottomLeft(relevantRD)) { // Bottom left.
            xTiles = getXIndex() - tarRoom.getXEndex();
            yTiles = getYIndex() - tarRoom.getYEndex();
        }
        return xTiles + yTiles;
    }

    private int[] relevantRoomDirection(Room tarRoom) {
        int[] output = new int[2];
        if (getXEndex() > tarRoom.getXEndex()) {
            output[0] = -1;
        } else {
            output[0] = 1;
        }
        if (getYEndex() > tarRoom.getYEndex()) {
            output[1] = -1;
        } else {
            output[1] = 1;
        }
        return output;
    }

    private boolean topLeft(int[] direction) {
        return direction[0] == -1 && direction[1] == 1;
    }

    private boolean bottomLeft(int[] direction) {
        return direction[0] == -1 && direction[1] == -1;
    }

    private boolean topRight(int[] direction) {
        return direction[0] == 1 && direction[1] == 1;
    }

    private boolean bottomRight(int[] direction) {
        return direction[0] == 1 && direction[1] == -1;
    }

    /* Returns whether or not there are any other rooms that are connected. */
    private boolean noCityBoys() {
        int numOfConnectedRooms = 0;
        for (int i = 0; i < Game.numRooms; i++) {
            if (i != roomListIndex) {
                if (Game.roomList[i].isConnected()) {
                    numOfConnectedRooms += 1;
                }
            }
        }
        return numOfConnectedRooms == 0;
    }

    /* Returns if the player can get from the given room to every other room. */
    protected boolean connectedToAll() {
        for (int i = 0; i < Game.numRooms; i++) {
            if (!connectedToIndex(i)) {
                return false;
            }
        }
        return true;
    }

    /* Returns if the given int is a room index that
     * this room is connected to already.
     */
    private boolean connectedToIndex(int i) {
        for (int cRI: connectedRoomsIndices) {
            if (cRI == i) {
                return true;
            }
        }
        return false;
    }

    /* Updates currRoom and tarRoom connectedRoomIndices
     * so that after they have been connected, it reflects
     * that all rooms these two are connected to, are now
     * connected to each other as well the original two rooms.aaadddd
     */
    private void updateCRIs(Room tarRoom) {
        int[] updatedCRIs =
                appendAndResizeIndices(connectedRoomsIndices, tarRoom.connectedRoomsIndices);
        for (int currRoomCRI : connectedRoomsIndices) {
            if (currRoomCRI != roomListIndex) {
                Game.roomList[currRoomCRI].connectedRoomsIndices = updatedCRIs;
            }
        }
        for (int tarRoomCRI : tarRoom.connectedRoomsIndices) {
            if (tarRoomCRI != roomListIndex) {
                Game.roomList[tarRoomCRI].connectedRoomsIndices = updatedCRIs;
            }
        }
        connectedRoomsIndices = updatedCRIs;
        tarRoom.connectedRoomsIndices = updatedCRIs;
    }
}
