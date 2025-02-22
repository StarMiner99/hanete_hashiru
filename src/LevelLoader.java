import java.io.*;
import java.util.Locale;
import java.util.Scanner;

public class LevelLoader {
    public static GameGrid loadWorld(String levelPath){
        /*
        first line: x_y_tileSize
        second line: playerX_playerY_playerLevel
        third line: checkpointX_checkpointY
        block line: blockX_blockY_decoration_block_rotation  -- rotation in 90 degree clockwise
         */


        File worldFile = new File(levelPath);
        try {
            Scanner fileScanner = new Scanner(worldFile);
            String[] metaData = fileScanner.nextLine().split("_");
            String[] camData = fileScanner.nextLine().split("_");
            String[] checkpointData = fileScanner.nextLine().split("_");

            int worldWidth = Integer.parseInt(metaData[0]);
            int worldHeight = Integer.parseInt(metaData[1]);

            int tileSize = Integer.parseInt(metaData[2]);

            double playerX = Double.parseDouble(camData[0]);
            double playerY = Double.parseDouble(camData[1]);
            int playerLvl = Integer.parseInt(camData[2]);

            double checkPointX = Double.parseDouble(checkpointData[0]);
            double checkPointY = Double.parseDouble(checkpointData[1]);

            TileDescriber[][] tiles = new TileDescriber[worldWidth][worldHeight];

            while (fileScanner.hasNextLine()) {
                String[] tileInfo = fileScanner.nextLine().split("_");
                int tileX = Integer.parseInt(tileInfo[0]);
                int tileY = Integer.parseInt(tileInfo[1]);
                int tileType = Integer.parseInt(tileInfo[2]);
                int tile = Integer.parseInt(tileInfo[3]);
                int rotation = Integer.parseInt(tileInfo[4]);

                tiles[tileX][tileY] = new TileDescriber(tile, tileX, tileY, tileSize, tileSize, rotation, tileType);
            }

            return new GameGrid(tileSize, playerX, playerY, tiles, checkPointX, checkPointY, playerLvl);

        } catch (FileNotFoundException e) {
            System.err.println("Level not found.");
            System.exit(-1);
            return null;
        }
    }

    public static void saveWorld(GameGrid gameGrid, String levelPath) {
        try {
            PrintWriter printWriter = new PrintWriter(levelPath);
            printWriter.printf("%d_%d_%d\n", gameGrid.getWorldSize().x, gameGrid.getWorldSize().y, gameGrid.tileSize);
            printWriter.printf(Locale.US, "%f_%f_%d\n", gameGrid.playerX, gameGrid.playerY, gameGrid.playerLevel); // floats have to be saved with . as decimal point
            printWriter.printf(Locale.US, "%f_%f", gameGrid.previousCheckPoint.x, gameGrid.previousCheckPoint.y);

            TileDescriber[][] world = gameGrid.getWorld();

            for (TileDescriber[] row : world) {
                for (TileDescriber block : row) {
                    if (block != null) {
                        int tileX = block.x;
                        int tileY = block.y;
                        int tileType = block.type;
                        int tile = block.tile;
                        int rotation = block.rotation;
                        printWriter.printf("\n%d_%d_%d_%d_%d", tileX, tileY, tileType, tile, rotation);
                    }
                }
            }

            printWriter.close();
            System.out.println("Progress Saved!");

        } catch (IOException e) {
            System.err.println("An error has occurred while saving the level.");
        }

    }
}
