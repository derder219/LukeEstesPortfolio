package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.Font;
import java.awt.Color;
import java.io.Serializable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.util.Random;

public class Game implements Serializable {
    TERenderer ter = new TERenderer();
    protected static final int WIDTH = 80; // Fixed map width.
    protected static final int HEIGHT = 40; // Fixed map height.
    protected static long worldSeed; // Seed input by the user converted to long.
    protected static Random generator; // Generator that will be based on worldSeed.
    private int gameTheme; // Created by generator, the theme the map will have.
    private static double magicRatio = .3; // Arbitrary ratio.
    protected static int maxWidth; // = Fixed max things to be added width.
    protected static int maxHeight; // = Fixed max things to be added height.
    protected TETile[][] world = new TETile[WIDTH][HEIGHT]; // 2-D world array of set size.
    protected static Room[] roomList; // List that holds all rooms generated.
    protected int[] smallTownGirls = new int[0]; // List of small town girls.
    protected static int numRooms;
    private final int maxRooms = 8; // + 3 = Max number of rooms.
    private ScreenMaster screenMaster = new ScreenMaster(); // Screen generator for the game.
    private ThemeImplementer themeImplementer; // Object to implement game themes.
    protected UserCharacter player; // User controlled character to manipulate in the world.
    protected NPC[] npcList = new NPC[4]; // List of NPCs in the world.
    private boolean gameOver = false; // Changes to true once user has won!
    protected TETile gameFloorTile; // Floor tiles for the given game.
    private static final long serialVersionUID = 2;
    private String interactiveDisplay; // What's said after player interacts with something.
    private int mouseRelativePosX;
    private int mouseRelativePosY;

    /* Sets the maxWidth and maxHeight of things to be added based on
     * the game WIDTH and HEIGHT. magicRatio isn't too mathematically
     * right; it's just a filler ratio for now.
     */
    public Game() {
        long longWidth = Math.round(magicRatio * WIDTH);
        long longHeight = Math.round(magicRatio * HEIGHT);
        maxWidth = (int) longWidth;
        maxHeight = (int) longHeight;
        makeWorldEmpty();
    }

    /* Fills the world with NOTHING tiles. */
    private void makeWorldEmpty() {
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }

    /* Creates valid rooms in the roomList Room-array. */
    private void createRooms() {
        for (int i = 0; i < roomList.length; i++) {
            Room roomToAdd = new Room();
            if (roomToAdd.overExtends()) { // If initial room overextends, retarget.
                roomToAdd.retargetRoom();
            }
            while (roomToAdd.overlapsARoom()) { // While the room we want to add overlaps another,
                roomToAdd = new Room(); // Create a new room.
                if (roomToAdd.overExtends()) { // If this new room overextends, then retarget.
                    roomToAdd.retargetRoom();
                }
            }
            roomList[i] = roomToAdd; // Finally valid and ready to add.
            numRooms += 1;
            roomToAdd.roomListIndex = i; // Adds roomListIndex value to room.
            roomToAdd.connectedRoomsIndices =
                    Room.appendAndResizeIndex(roomToAdd.connectedRoomsIndices, i);
        }
    }

    /* Returns a copy of the input world with each room added. */
    private TETile[][] implementRooms() {
        TETile[][] currWorld = TETile.copyOf(world);
        for (Room currRoom: roomList) {
            currWorld = currRoom.addToWorld(currWorld);
        }
        return currWorld;
    }

    /* Creates all room connections. */
    private void createConnections() {
        for (int i = 0; i < numRooms; i++) {
            roomList[i].setupRoomConnections();
        }
    }

    /* Returns copy of the input world with
     * each room connected to another room.
     */
    private TETile[][] implementConnections(TETile[][] thisWorld) {
        TETile[][] currWorld = TETile.copyOf(thisWorld);
        for (int i = 0; i < numRooms; i++) {
            currWorld = roomList[i].connectFour(currWorld);
        }
        return currWorld;
    }

    /* Creates list of small town girl indices and then
     * implements the midnight train for each of those rooms.
     */
    private TETile[][] smallTownGirls(TETile[][] thisWorld) {
        TETile[][] currWorld = TETile.copyOf(thisWorld);
        for (Room currRoom: roomList) {
            if (!currRoom.isConnected()) {
                smallTownGirls = Room.appendAndResizeIndex(smallTownGirls, currRoom.roomListIndex);
            }
        }
        for (int sTG: smallTownGirls) {
            Room currSmallTownGirl = roomList[sTG];
            if (!currSmallTownGirl.isConnected()) {
                currWorld = currSmallTownGirl.takeTheMidnightTrain(currWorld);
            }
        }
        return currWorld;
    }

    /* Implements midnight train for all rooms that aren't
     * connected to every room.
     */
    private TETile[][] specialMidnightTrain(TETile[][] thisWorld) {
        for (Room currRoom: roomList) {
            while (!currRoom.connectedToAll()) {
                thisWorld = currRoom.takeTheMidnightTrain(thisWorld);
            }
        }
        return thisWorld;
    }

    /* Returns whether or not the given xy coordinate is in bounds. */
    protected boolean indexInBounds(int x, int y) {
        return x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT;
    }

    /* Given an input xy coordinate of a tile that is a floor,
     * will return a TETile[][] world that has
     * converted surrounding nothing tiles to wall tiles.
     */
    private TETile[][] encloseFloorTile(int x, int y, TETile[][] currWorld) {
        if (indexInBounds(x - 1, y - 1)) {
            if (currWorld[x - 1][y - 1].equals(Tileset.NOTHING)) {
                currWorld[x - 1][y - 1] = Tileset.WALL;
            }
        }
        if (indexInBounds(x - 1, y + 1)) {
            if (currWorld[x - 1][y + 1].equals(Tileset.NOTHING)) {
                currWorld[x - 1][y + 1] = Tileset.WALL;
            }
        }
        if (indexInBounds(x + 1, y - 1)) {
            if (currWorld[x + 1][y - 1].equals(Tileset.NOTHING)) {
                currWorld[x + 1][y - 1] = Tileset.WALL;
            }
        }
        if (indexInBounds(x + 1, y + 1)) {
            if (currWorld[x + 1][y + 1].equals(Tileset.NOTHING)) {
                currWorld[x + 1][y + 1] = Tileset.WALL;
            }
        }
        return currWorld;
    }

    /* Given a world, will return a world that has
     * enclosed all floor tiles with wall tiles.
     */
    private TETile[][] encloseAllFloors(TETile[][] thisWorld) {
        TETile[][] worldCopy = TETile.copyOf(thisWorld);
        for (int x = 0; x < Game.WIDTH; x++) {
            for (int y = 0; y < Game.HEIGHT; y++) {
                if (worldCopy[x][y].equals(Tileset.FLOOR) || worldCopy[x][y].equals(Tileset.SAND)) {
                    worldCopy = encloseFloorTile(x, y, worldCopy);
                }
            }
        }
        return worldCopy;
    }

    /* Generates random xPos and yPos for player character
     * that is within the games boundaries and checks if that
     * position is a floor. If so, it will return a copy of the
     * input world with the player added to that xPos and yPos.
     */
    private TETile[][] addPlayerTo(TETile[][] thisWorld) {
        TETile[][] worldCopy = TETile.copyOf(thisWorld);
        int xPos = generator.nextInt(WIDTH);
        int yPos = generator.nextInt(HEIGHT);
        while (!worldCopy[xPos][yPos].equals(Tileset.FLOOR)) {
            xPos = generator.nextInt(WIDTH);
            yPos = generator.nextInt(HEIGHT);
        }
        player = new UserCharacter(xPos, yPos, "down");
        worldCopy = player.addToWorld(worldCopy);
        return worldCopy;
    }

    /* Returns a copy of the input world with 4 NPCs generated
     * and added to the world.
     */
    private TETile[][] addNPCsTo(TETile[][] thisWorld) {
        TETile[][] worldCopy = TETile.copyOf(thisWorld);
        int xPos = generator.nextInt(WIDTH);
        int yPos = generator.nextInt(HEIGHT);
        for (int i = 0; i < npcList.length; i++) {
            while (!player.isTraversible(worldCopy[xPos][yPos])) {
                xPos = generator.nextInt(WIDTH);
                yPos = generator.nextInt(HEIGHT);
            }
            NPC newNPC = new NPC(xPos, yPos, false);
            worldCopy = newNPC.addToWorld(worldCopy);
            npcList[i] = newNPC;
            xPos = generator.nextInt(WIDTH);
            yPos = generator.nextInt(HEIGHT);
        }
        return worldCopy;
    }

    /* Interaction the user has with the world using
     * their character. Goes until game is over or if
     * it is exited.
     */
    protected void characterToWorldInteraction() {
        while (!gameOver) {
            if (needToUpdateTileDes()) {
                displayNewTileDescription();
            }
            renderHUD();
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }
            char key = StdDraw.nextKeyTyped();
            if (isDirectionalKey(key)) {
                world = player.move(key, world, gameFloorTile);
                renderWorld();
            }
            if (key == 'f' || key == 'F') {
                interactiveDisplay = player.interact(world);
                displayInteraction();
                gameOver = isGameOver();
            }
            // If q, then quitsave or if m, start a new game.
            // Otherwise will interact with the world normally
            // using a specific input key.
            if (key == ':') {
                while (!StdDraw.hasNextKeyTyped()) {
                    continue;
                }
                char key2 = StdDraw.nextKeyTyped();
                if (key2 == 'q' || key2 == 'Q') {
                    saveGame(this);
                    System.exit(0);
                }
                if (key2 == 'm' || key2 == 'M') {
                    Game newGameStarted = new Game();
                    newGameStarted.playWithKeyboard();
                } else {
                    characterToWorldInteractionWithKey(key2);
                }
            }
        }
        gameOverScreen();
        gameOverScreenInteraction();
    }

    /* Displays string in top right corner based off of
     * player interaction with whatever their character is
     * facing. If it is a ghost, it will replace that ghost
     * with a floor tile, as well as put the ghost to rest.
     */
    private void displayInteraction() {
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(Color.white);
        if (interactiveDisplay.equals("You put one spooker to rest.")) {
            int[] coordsFacing = player.coordinatesFacing();
            int xC = coordsFacing[0];
            int yC = coordsFacing[1];
            putSpookerToRestAtPosition(xC, yC);
            world[xC][yC] = gameFloorTile;
        }
        renderWorld();
        Font smallFont = new Font("Monaco", Font.BOLD, 15);
        StdDraw.setFont(smallFont);
        StdDraw.text(Game.WIDTH - 15, Game.HEIGHT + 7, interactiveDisplay);
        StdDraw.text(Game.WIDTH / 10, Game.HEIGHT + 7,
                world[mouseRelativePosX][mouseRelativePosY].description());
        StdDraw.show();
    }

    private void putSpookerToRestAtPosition(int x, int y) {
        for (NPC currNPC: npcList) {
            if (currNPC.getxPosition() == x && currNPC.getyPosition() == y) {
                currNPC.putToRest();
                return;
            }
        }
    }

    /* Updates displayedTileDescription to match
     * whatever is currently being hovered over.
     * Then, displays the description of whichever tile the mouse is
     * hovering over in the top left.
     */
    private void displayNewTileDescription() {
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(Color.white);
        renderWorld();
        Font smallFont = new Font("Monaco", Font.BOLD, 15);
        StdDraw.setFont(smallFont);
        StdDraw.text(Game.WIDTH / 10, Game.HEIGHT + 7,
                world[mouseRelativePosX][mouseRelativePosY].description());
        StdDraw.show();
    }

    /* Returns if the mouse is hovering
     * a tile that is in a different position
     * than the previous mouse position.
     * If so, returns true and resets mousePosX and
     * mousePosY.
     * */
    private boolean needToUpdateTileDes() {
        int x;
        int y;
        double mouseX = StdDraw.mouseX();
        double mouseY = StdDraw.mouseY();
        boolean roundDownX = ((int) ((mouseX - (int) mouseX) * 10)) < 5;
        boolean roundDownY = ((int) ((mouseY - (int) mouseY) * 10)) < 5;
        if (roundDownX) {
            x = (int) mouseX;
        } else {
            x = Math.round((float) mouseX);
        }
        if (roundDownY) {
            y = (int) mouseX - 6;
        } else {
            y = Math.round((float) mouseY) - 6;
        }
        if (indexInBounds(x, y)) {
            if (x != mouseRelativePosX
                    || y != mouseRelativePosY) {
                mouseRelativePosX = x;
                mouseRelativePosY = y;
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /* Returns true if the input key is either:
     * w, a, s, d, or them in capital form.
     */
    private boolean isDirectionalKey(char key) {
        return key == 'w'
                || key == 'a'
                || key == 's'
                || key == 'd'
                || key == 'A'
                || key == 'S'
                || key == 'D'
                || key == 'W';
    }

    /* Copies the world, creates the world
     * in the copy. Resets Game.world to this
     * currWorld after everything has been added to it
     * and has been enclosed.
     */
    protected void createWorld() {
        TETile[][] currWorld;
        numRooms = 0;
        createRooms();
        currWorld = implementRooms();
        createConnections();
        currWorld = implementConnections(currWorld);
        currWorld = smallTownGirls(currWorld);
        currWorld = specialMidnightTrain(currWorld);
        currWorld = encloseAllFloors(currWorld);
        currWorld = addPlayerTo(currWorld);
        currWorld = addNPCsTo(currWorld);
        themeImplementer =
                new ThemeImplementer(currWorld, gameTheme);
        currWorld = themeImplementer.createThemedWorld();
        world = currWorld;
    }

    private void setPostGeneratorFields() {
        gameTheme = generator.nextInt(3);
        gameFloorTile = genGFT(gameTheme);
        roomList = new Room[generator.nextInt(maxRooms) + 3];
    }

    protected TETile genGFT(int themeNumber) {
        if (themeNumber == 0) {
            return Tileset.SAND;
        }
        if (themeNumber == 1) {
            return Tileset.LIGHTSNOW;
        } else {
            return Tileset.LIGHTGRASS;
        }
    }

    /* Calls the TERenderer on the Game's world. */
    protected void renderWorld() {
        ter.renderFrame(world);
    }

    protected void renderHUD() {
        Font bigFont = new Font("Monaco", Font.BOLD, 25);
        StdDraw.setPenColor(Color.white);
        StdDraw.setFont(bigFont);
        StdDraw.text(Game.WIDTH / 2, Game.HEIGHT + 7,
                "Put Sum Respec On My Grave");
        Font smallFont =
                new Font("Monaco", Font.BOLD, 15);
        StdDraw.setFont(smallFont);
        StdDraw.text(Game.WIDTH - 10, 3,
                "Press :Q to save and quit.");
        StdDraw.text(Game.WIDTH / 10, 3,
                "Press :M to exit to the main menu.");
        StdDraw.text(Game.WIDTH / 2, 3,
                "Press F to interact with what you're facing.");
        StdDraw.show();
    }

    /*
     * Method used for playing a fresh game. The game should start from the main menu.
     */
    public void playWithKeyboard() {
        screenMaster.myCanvas();
        screenMaster.mainMenuScreen();
        screenMaster.mainMenuInteraction();
        setPostGeneratorFields();
        createWorld();
        ter.initialize(Game.WIDTH, Game.HEIGHT + 10, 0, 5);
        renderWorld();
        renderHUD();
        characterToWorldInteraction();
    }

    /**
     * Method used for autograding and testing the game code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The game should
     * behave exactly as if the user typed these characters into the game after playing
     * playWithKeyboard. If the string ends in ":q", the same world should be returned as if the
     * string did not end with q. For example "n123sss" and "n123sss:q" should return the same
     * world. However, the behavior is slightly different. After playing with "n123sss:q", the game
     * should save, and thus if we then called playWithInputString with the string "l", we'd expect
     * to get the exact same world back again, since this corresponds to loading the saved game.
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     * Maybe fix static type seed and generator by making an update method?
     */
    public TETile[][] playWithInputString(String input) {
        String[] splitInput = input.split(""); // Split string input into a String array
        if (splitInput[0].equals("n") || splitInput[0].equals("N")) {
            Game returnGame = new Game();
            String seedString = getSeedFromString(splitInput);
            int charMovementBegins = getDirectionalKeyIndex(splitInput);
            returnGame.worldSeed = Long.parseLong(seedString);
            returnGame.generator = new Random(returnGame.worldSeed);
            returnGame.setPostGeneratorFields();
            returnGame.createWorld();
            /* If nothing after the S in the input, return the freshly generated world. */
            if (charMovementBegins == splitInput.length - 1) {
                return returnGame.world;
            }
            for (int i = charMovementBegins + 1; i < splitInput.length; i++) {
                char currKey = splitInput[i].charAt(0);
                if (isDirectionalKey(currKey)) {
                    returnGame.world =
                            returnGame.player.move
                                    (currKey, returnGame.world, returnGame.gameFloorTile);
                }
                if (currKey == ':') {
                    char nextKey = splitInput[i + 1].charAt(0);
                    if (nextKey == 'q' || nextKey == 'Q') {
                        saveGame(returnGame);
                        return returnGame.world;
                    }
                }
            }
            return returnGame.world;
            // If not an N first, then must be an L for load.
        } else {
            Game gameLoaded = Game.loadGame();
            if (splitInput.length == 1) {
                return gameLoaded.world;
            }
            for (int i = 1; i < splitInput.length; i++) {
                char currKey = splitInput[i].charAt(0);
                if (isDirectionalKey(currKey)) {
                    gameLoaded.world =
                            gameLoaded.player.move
                                    (currKey, gameLoaded.world, gameLoaded.gameFloorTile);
                }
                if (currKey == ':') {
                    char nextKey = splitInput[i + 1].charAt(0);
                    if (nextKey == 'q' || nextKey == 'Q') {
                        saveGame(gameLoaded);
                        return gameLoaded.world;
                    }
                }
            }
            return gameLoaded.world;
        }
    }

    /* Starting at second item (since this is
     * called after N is known to be first key), returns
     * a string that contains all the numbers in the input
     * array starting at index 1 until the key value interpretted
     * is an S. Then, we will output the seed we have created.
     */
    private String getSeedFromString(String[] sArray) {
        String seedInput = "";
        boolean seedInputted = false;
        int index = 0;
        while (!seedInputted) {
            index += 1;
            char key = sArray[index].charAt(0);
            if (key == 'S' || key == 's') {
                seedInputted = true;
            } else if (ScreenMaster.isASingleDigitNumber(key)) {
                seedInput += String.valueOf(key);
            }
        }
        return seedInput;
    }

    /* Returns the index where S is in the input string
     * after calling a new game. This is used to find the
     * index of where player movement (or quitsaving)
     * in the game begins.
     */
    private int getDirectionalKeyIndex(String[] sArray) {
        boolean seedInputted = false;
        int index = 0;
        while (!seedInputted) {
            index += 1;
            char key = sArray[index].charAt(0);
            if (key == 'S' || key == 's') {
                seedInputted = true;
            }
        }
        return index;
    }

    protected static Game loadGame() {
        File file = new File("./game.txt");
        if (file.exists()) {
            try {
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream oos = new ObjectInputStream(fis);
                Game gameLoaded = (Game) oos.readObject();
                oos.close();
                return gameLoaded;
            } catch (FileNotFoundException e) {
                System.out.println("file not found");
                System.exit(0);
            } catch (IOException e) {
                System.out.println(e);
                System.exit(0);
            } catch (ClassNotFoundException e) {
                System.out.println("class not found");
                System.exit(0);
            }
        }
        ScreenMaster.mainMenuScreen();
        ScreenMaster.mainMenuInteraction();
        return new Game();
    }

    /* Overwrites current save with the current
     * state of the game. If no current save exists,
     * creates a new save with the current state of
     * the game.
     */
    private void saveGame(Game g) {
        File file = new File("./game.txt");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(g);
            oos.close();
        }  catch (FileNotFoundException e) {
            System.out.println("file not found");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(0);
        }
    }

    /* Interaction the user has with the world using
     * their character. Goes until game is over or if
     * it is exited.
     */
    private void characterToWorldInteractionWithKey(char inputKey) {
        if (isDirectionalKey(inputKey)) {
            world = player.move(inputKey, world, gameFloorTile);
            renderWorld();
        }
        if (inputKey == 'f' || inputKey == 'F') {
            interactiveDisplay = player.interact(world);
            displayInteraction();
            gameOver = isGameOver();
        }
        // If q, then quitsave or if m, start a new game.
        if (inputKey == ':') {
            while (!StdDraw.hasNextKeyTyped()) {
                continue;
            }
            char key2 = StdDraw.nextKeyTyped();
            if (key2 == 'q' || key2 == 'Q') {
                saveGame(this);
                System.exit(0);
            }
            if (key2 == 'm' || key2 == 'M') {
                Game newGameStarted = new Game();
                newGameStarted.playWithKeyboard();
            } else {
                characterToWorldInteractionWithKey(key2);
            }
        }
    }

    /* Used to update the game after an interaction has been made
     * to see if the player has put all NPCs to rest.
     */
    private boolean isGameOver() {
        for (NPC currNPC: npcList) {
            if (!currNPC.inPeace()) {
                return false;
            }
        }
        return true;
    }

    /* Screen to display when user has won. */
    protected void gameOverScreen() {
        StdDraw.pause(1000);
        StdDraw.clear(Color.BLACK);
        Font bigFont = new Font("Monaco", Font.BOLD, 35);
        StdDraw.setFont(bigFont);
        StdDraw.setPenColor(Color.white);
        StdDraw.text(Game.WIDTH / 2, Game.HEIGHT,
                "Congratulations! You've put all the ghosts to rest!");
        StdDraw.text(Game.WIDTH / 2, Game.HEIGHT / 2 - 2,
                "Press Q to quit or Press M to go to the main menu.");
        StdDraw.show();
    }

    /* How the player interacts with the screen after
     * winning/putting all ghosts to rest.
     */
    protected void gameOverScreenInteraction() {
        boolean noPlayerAction = true;
        while (noPlayerAction) {
            while (!StdDraw.hasNextKeyTyped()) {
                continue;
            }
            char key = StdDraw.nextKeyTyped();
            if (key == 'q' || key == 'Q') {
                System.exit(0);
            }
            if (key == 'm' || key == 'M') {
                Game newGameStarted = new Game();
                newGameStarted.playWithKeyboard();
            }
        }
    }
}
