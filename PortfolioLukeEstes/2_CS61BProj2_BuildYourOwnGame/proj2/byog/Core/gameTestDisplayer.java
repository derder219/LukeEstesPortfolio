package byog.Core;

import byog.TileEngine.TERenderer;

public class gameTestDisplayer {
    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        Tester tester = new Tester();
        //tester.screenTest();
        //ter.initialize(Game.WIDTH, Game.HEIGHT);
        //ter.renderFrame(tester.gameSetup());
        Game testGame = new Game();
        testGame.playWithKeyboard();
    }
}
