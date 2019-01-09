package byog.Core;
import org.junit.Test;
import static org.junit.Assert.*;
import byog.TileEngine.TETile;

public class Tester {
    Game game = new Game();
    //Room testRoom = new Room();
    //String testInputSeed = "n1956076975221446260swaaasasw:q";
    //String testInputSeed2 = "n3216899177661270307ssddwdwas";
    String testInputSeed3 = "n2495654345402949947sdsaasa";

    public TETile[][] gameSetup() {
        return game.playWithInputString(testInputSeed3);
    }

    @Test
    public void gameFieldTester() {
        gameSetup();
        assertEquals(game.WIDTH, 80);
        assertEquals(game.HEIGHT, 40);
        assertEquals(game.maxWidth, 24);
        assertEquals(game.maxHeight, 12);
    }

    @Test
    public void generateWorldWithTestSeed() {
        gameSetup();
    }
    /*@Test
    public void runWithCertainSeed() {
        game.playWithInputString("123123");
    }*/

    public void screenTest() {
        ScreenMaster sm = new ScreenMaster();
        sm.myCanvas();
        sm.mainMenuScreen();
    }
}
