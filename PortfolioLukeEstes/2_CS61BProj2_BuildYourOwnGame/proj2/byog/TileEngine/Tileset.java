package byog.TileEngine;

import java.awt.Color;
import java.io.Serializable;

/**
 * Contains constant tile objects, to avoid having to remake the same tiles in different parts of
 * the code.
 *
 * You are free to (and encouraged to) create and add your own tiles to this file. This file will
 * be turned in with the rest of your code.
 *
 * Ex:
 *      world[x][y] = Tileset.FLOOR;
 *
 * The style checker may crash when you try to style check this file due to use of unicode
 * characters. This is OK.
 * https://opengameart.org/content/16x16-snowy-town-tiles
 *      This source gave me snow tree, light snow, and heavy snow.
 * https://opengameart.org/content/town-tiles
 *      This source gave me tree, light grass, and heavy grass.
 * https://opengameart.org/content/16x16-overworld-tiles
 *      This source gave me water.
 * https://opengameart.org/content/tiny-16-basic
 *      This source gave me sand, flowers, and all characters with their respective positions.
 */


public class Tileset implements Serializable {
    public static final TETile PLAYER1FF = new TETile('@',
            Color.white, Color.black, "Facing Front",
            "/Users/Luke Estes/Documents/cs61b/sp18-bfm"
                    +
                    "/proj2/byog/Core/images/character1FacingForward.png");
    public static final TETile PLAYER1FB = new TETile('@',
            Color.white, Color.black, "Facing Back",
            "/Users/Luke Estes/Documents/cs61b/sp18-bfm"
                    +
                    "/proj2/byog/Core/images/character1FacingBack.png");
    public static final TETile PLAYER1FR = new TETile('@',
            Color.white, Color.black, "Facing Right",
            "/Users/Luke Estes/Documents/cs61b/sp18-bfm"
                    +
                    "/proj2/byog/Core/images/character1FacingRight.png");
    public static final TETile PLAYER1FL = new TETile('@',
            Color.white, Color.black, "Facing Left",
            "/Users/Luke Estes/Documents/cs61b/sp18-bfm"
                    +
                    "/proj2/byog/Core/images/character1FacingLeft.png");
    public static final TETile GHOST = new TETile('@',
            Color.white, Color.black, "Ghost",
            "/Users/Luke Estes/Documents/cs61b/sp18-bfm"
                    +
                    "/proj2/byog/Core/images/ghost.png");
    public static final TETile WALL = new TETile('#',
            new Color(216, 128, 128), Color.darkGray,
            "wall");
    public static final TETile FLOOR = new TETile('·',
            new Color(128, 192, 128), Color.black,
            "floor");
    public static final TETile NOTHING = new TETile(' ',
            Color.black, Color.black, "nothing");
    public static final TETile LIGHTGRASS = new TETile('"',
            Color.green, Color.black, "light grass",
            "/Users/Luke Estes/Documents/cs61b/sp18-bfm"
                    +
                    "/proj2/byog/Core/images/lightGrass.png");
    public static final TETile HEAVYGRASS = new TETile('"',
            Color.green, Color.black, "heavy grass",
            "/Users/Luke Estes/Documents/cs61b/sp18-bfm"
                    +
                    "/proj2/byog/Core/images/heavyGrass.png");
    public static final TETile WATER = new TETile('≈',
            Color.blue, Color.black, "water",
            "/Users/Luke Estes/Documents/cs61b/sp18-bfm"
                    +
                    "/proj2/byog/Core/images/water.png");
    public static final TETile FLOWER = new TETile('❀',
            Color.magenta, Color.pink, "flower",
            "/Users/Luke Estes/Documents/cs61b/sp18-bfm"
                    +
                    "/proj2/byog/Core/images/flowers.png");
    public static final TETile LOCKED_DOOR = new TETile('█',
            Color.orange, Color.black,
            "locked door");
    public static final TETile UNLOCKED_DOOR = new TETile('▢',
            Color.orange, Color.black,
            "unlocked door");
    public static final TETile SAND = new TETile('▒', Color.yellow,
            Color.black, "sand",
            "/Users/Luke Estes/Documents/cs61b/sp18-bfm"
                    +
                    "/proj2/byog/Core/images/sand.png");
    public static final TETile MOUNTAIN = new TETile('▲', Color.gray,
            Color.black, "mountain");
    public static final TETile TREE = new TETile('♠', Color.green,
            Color.black, "tree",
            "/Users/Luke Estes/Documents/cs61b/sp18-bfm"
                    +
                    "/proj2/byog/Core/images/tree.png");
    public static final TETile SNOWTREE = new TETile('B',
            Color.green, Color.black, "snow tree",
            "/Users/Luke Estes/Documents/cs61b/sp18-bfm"
                    +
                    "/proj2/byog/Core/images/snowTree.png");
    public static final TETile HEAVYSNOW = new TETile('$',
            Color.white, Color.black, "heavy snow",
            "/Users/Luke Estes/Documents/cs61b/sp18-bfm"
                    +
                    "/proj2/byog/Core/images/heavySnow.png");
    public static final TETile LIGHTSNOW = new TETile('0',
            Color.white, Color.black, "light snow",
            "/Users/Luke Estes/Documents/cs61b/sp18-bfm"
                    +
                    "/proj2/byog/Core/images/lightSnow.png");
}


