package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.io.Serializable;

public class ThemeImplementer implements Serializable {
    private TETile[][] themedWorld;
    private int theme;

    /* Creates a new theme implementer which takes in a world and theme
     * type which is determined by an integer.
     */
    public ThemeImplementer(TETile[][] inputWorld, int themeNumber) {
        themedWorld = inputWorld;
        theme = themeNumber;
    }

    protected TETile[][] createThemedWorld() {
        addThemeToWorld();
        return themedWorld;
    }

    private void addThemeToWorld() {
        if (theme == 0) {
            implementInfamousIslandTheme();
        }
        if (theme == 1) {
            implementWinterWonderlandTheme();
        }
        if (theme == 2) {
            implementFunForestTheme();
        }
    }

    private void implementInfamousIslandTheme() {
        for (int x = 0; x < Game.WIDTH; x += 1) {
            for (int y = 0; y < Game.HEIGHT; y += 1) {
                if (themedWorld[x][y].equals(Tileset.NOTHING)) {
                    themedWorld[x][y] = Tileset.WATER;
                }
                if (themedWorld[x][y].equals(Tileset.FLOOR)) {
                    themedWorld[x][y] = Tileset.SAND;
                }
                if (themedWorld[x][y].equals(Tileset.WALL)) {
                    themedWorld[x][y] = Tileset.FLOWER;
                }
            }
        }
    }

    private void implementWinterWonderlandTheme() {
        for (int x = 0; x < Game.WIDTH; x += 1) {
            for (int y = 0; y < Game.HEIGHT; y += 1) {
                if (themedWorld[x][y].equals(Tileset.NOTHING)) {
                    themedWorld[x][y] = Tileset.HEAVYSNOW;
                }
                if (themedWorld[x][y].equals(Tileset.FLOOR)) {
                    themedWorld[x][y] = Tileset.LIGHTSNOW;
                }
                if (themedWorld[x][y].equals(Tileset.WALL)) {
                    themedWorld[x][y] = Tileset.SNOWTREE;
                }
            }
        }
    }

    private void implementFunForestTheme() {
        for (int x = 0; x < Game.WIDTH; x += 1) {
            for (int y = 0; y < Game.HEIGHT; y += 1) {
                if (themedWorld[x][y].equals(Tileset.NOTHING)) {
                    themedWorld[x][y] = Tileset.HEAVYGRASS;
                }
                if (themedWorld[x][y].equals(Tileset.FLOOR)) {
                    themedWorld[x][y] = Tileset.LIGHTGRASS;
                }
                if (themedWorld[x][y].equals(Tileset.WALL)) {
                    themedWorld[x][y] = Tileset.TREE;
                }
            }
        }
    }
}
