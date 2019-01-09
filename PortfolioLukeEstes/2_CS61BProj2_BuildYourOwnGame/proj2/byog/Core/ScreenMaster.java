package byog.Core;

import edu.princeton.cs.introcs.StdDraw;

import java.awt.Font;
import java.awt.Color;
import java.util.Random;
import java.io.Serializable;

public class ScreenMaster implements Serializable {

    protected static void myCanvas() {
        StdDraw.setCanvasSize(Game.WIDTH * 10, Game.HEIGHT * 16);
        StdDraw.setXscale(0, Game.WIDTH);
        StdDraw.setYscale(0, Game.HEIGHT);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
    }

    protected static void mainMenuScreen() {
        Font bigFont = new Font("Monaco", Font.BOLD, 35);
        StdDraw.setFont(bigFont);
        StdDraw.setPenColor(Color.white);
        StdDraw.text(Game.WIDTH / 2, Game.HEIGHT * 3 / 4, "Put Sum Respec On My Grave");
        Font smallFont = new Font("Monaco", Font.BOLD, 20);
        StdDraw.setFont(smallFont);
        StdDraw.text(Game.WIDTH / 2, Game.HEIGHT / 2, "(N) New Game");
        StdDraw.text(Game.WIDTH / 2, Game.HEIGHT / 2 - 2, "(L) Load Game");
        StdDraw.text(Game.WIDTH / 2, Game.HEIGHT / 2 - 4, "(Q) Quit");
        StdDraw.show();
    }

    /* This captures the key presses for either:
     * creating a new game,
     * loading a saved game,
     * or exiting the game.
     */
    protected static void mainMenuInteraction() {
        boolean exitMainMenu = false;
        while (!exitMainMenu) {
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }
            char input = StdDraw.nextKeyTyped();
            if (input == 'N' || input == 'n') {
                seedInputScreen();
                exitMainMenu = true;
            } else if (input == 'L' || input == 'l') {
                Game gameLoaded = Game.loadGame();
                gameLoaded.ter.initialize(Game.WIDTH, Game.HEIGHT + 10, 0, 5);
                gameLoaded.renderWorld();
                gameLoaded.renderHUD();
                gameLoaded.characterToWorldInteraction();
                gameLoaded.gameOverScreen();
                gameLoaded.gameOverScreenInteraction();
            } else if (input == 'Q' || input == 'q') {
                System.exit(0);
                exitMainMenu = true;
            }
        }
    }

    /* Screen displayed when a user presses N
     * that takes in an input seed and generates
     * the world for the user to play the game in.
     */
    protected static void seedInputScreen() {
        StdDraw.clear(Color.BLACK);
        Font smallFont = new Font("Monaco", Font.BOLD, 15);
        StdDraw.setFont(smallFont);
        StdDraw.setPenColor(Color.white);
        StdDraw.text(Game.WIDTH / 2, Game.HEIGHT / 2,
                "Enter a sequence of number and press S to start.");
        StdDraw.show();
        boolean seedInputted = false;
        String seedInput = "";
        while (!seedInputted) {
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }
            char key = StdDraw.nextKeyTyped();
            if (key == 'S' || key == 's') {
                seedInputted = true;
            } else if (isASingleDigitNumber(key)) {
                seedInput += String.valueOf(key);
                StdDraw.clear(Color.BLACK);
                StdDraw.setFont(smallFont);
                StdDraw.setPenColor(Color.white);
                StdDraw.text(Game.WIDTH / 2, Game.HEIGHT / 2,
                        "Enter a sequence of number and press S to start.");
                StdDraw.text(Game.WIDTH / 2, Game.HEIGHT / 2 - 2, seedInput);
                StdDraw.show();
            }
        }
        if (seedInput.equals("")) {
            noSeedInputted();
            return;
        }
        long seed = Long.parseLong(seedInput);
        Game.worldSeed = seed;
        Game.generator = new Random(seed);
    }

    /* Returns if input char is between 0 and 9. */
    protected static boolean isASingleDigitNumber(char inputChar) {
        int asciiValue = (int) inputChar;
        return asciiValue > 47 && asciiValue < 58;
    }

    /* Tells user that input must at least have one number
     * and then returns them to the main menu again.
     */
    protected static void noSeedInputted() {
        StdDraw.clear(Color.BLACK);
        Font smallFont = new Font("Monaco", Font.BOLD, 20);
        StdDraw.setFont(smallFont);
        StdDraw.setPenColor(Color.white);
        StdDraw.text(Game.WIDTH / 2, Game.HEIGHT / 2 + 2,
                "Seed must be at least one number long!");
        StdDraw.text(Game.WIDTH / 2, Game.HEIGHT / 2 - 2,
                "Returning to the main menu, please wait...");
        StdDraw.show();
        StdDraw.pause(3500);
        StdDraw.clear(Color.BLACK);
        mainMenuScreen();
        mainMenuInteraction();
    }
}
