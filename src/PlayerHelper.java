public class PlayerHelper {

    public static final double jumpSpeedAdd = -25.8;
    public static final double playerAcc = 5.25;
    public static int tileUnder;
    public static double friction = 0.5;
    public static final double gravityBase = 1.6;
    public static double g = 1.6;
    private static int jumpsMax = 1;
    private static int jumps = jumpsMax;
    private final GameGrid gameGrid;
    private static int dashesMax = 0;
    public static int dashCooldown = 120;
    private static final int dashDuration = 12;
    private static int dashActiveRemaining = 0;
    public static int dashCooldownRemaining = 0;
    private static final double dashSpeedMultiplier = 3;
    private static int jumpButtonBuffer = 0;
    private static boolean jumpButtonBufferSet = false;
    private static int dashes = dashesMax;

    public boolean onGround = true;
    private boolean cheatMode = false;

    private static boolean jumping = false;
    private static Vector2D speedVector;
    public PlayerHelper(GameGrid gameGrid) {
        this.gameGrid = gameGrid;

        speedVector = new Vector2D();
    }



    public void update(boolean[] keys) {
        tileUnder = gameGrid.getTileUnderPlayer().tile;

        if (gameGrid.playerLevel == 1 && !cheatMode) {
            jumpsMax = 2;
        } else if (gameGrid.playerLevel == 2 && !cheatMode) {
            jumpsMax = 2;
            dashesMax = 1;
        }

        int xAxis = ((keys[1]) ? 1 : 0)-((keys[0]) ? 1 : 0);//Bool -> Int

        speedVector.x*=friction;//Friction

        gameGrid.createDashParticle = false;

        if (dashActiveRemaining > 0) {
            speedVector.x += xAxis * playerAcc * dashSpeedMultiplier;
            gameGrid.createDashParticle = true;
            g=0.5*gravityBase;
            dashActiveRemaining--;
        } else {
            g=gravityBase;
            speedVector.x += xAxis * playerAcc;
        }

        if (dashCooldownRemaining > 0 && dashes < dashesMax) {
            dashCooldownRemaining--;
        } else if (dashes < dashesMax) {
            dashCooldownRemaining = dashCooldown;
            dashes++;
        }

        if (jumpButtonBuffer >= 0) //Input buffering
        {
            jumpButtonBuffer--;
        } else if (jumps == jumpsMax && !onGround) {
           jumps = (byte) (jumpsMax-1);
        }

        gameGrid.showDebugMessage("jumpBuffer",String.valueOf(jumpButtonBuffer));
        gameGrid.showDebugMessage("dashes", dashes +"/"+ dashesMax);
        gameGrid.showDebugMessage("jumps", jumps +"/"+ jumpsMax);
        gameGrid.showDebugMessage("dashActive",String.valueOf(dashActiveRemaining));
        gameGrid.showDebugMessage("dashCooldown",String.valueOf(dashCooldownRemaining));

        if (!gameGrid.detectPlayerCollision(0,1))
        {
            speedVector.y+=g;
            onGround = false;
            if (jumpButtonBuffer <= 0 && jumps == jumpsMax && !jumpButtonBufferSet)
            {
                jumpButtonBufferSet = true;
                jumpButtonBuffer = 9;
            }
        }

        if ((((jumpButtonBuffer > 0 || onGround) && jumping) && jumps == jumpsMax) || (jumping && jumps > 0 && jumps < jumpsMax) && dashActiveRemaining <= 0)
        {
            if (jumps < jumpsMax) {
                gameGrid.createDoubleJumpParticles = true;
            }
            speedVector.y = jumpSpeedAdd;
            onGround = false;
            jumps--;
            jumpButtonBuffer = 0;
        }

        if (gameGrid.detectPlayerCollision(speedVector.x,0))
        {
            while(!gameGrid.detectPlayerCollision(Math.signum(speedVector.x),0))
            {
                gameGrid.movePlayer(Math.signum(speedVector.x),0);
            }
            speedVector.x=0;
        }
        gameGrid.movePlayer(speedVector.x,0);

        if (gameGrid.detectPlayerCollision(0,speedVector.y))
        {
            if (Math.signum(speedVector.y) > 0)
            {
                onGround = true;
                jumpButtonBufferSet = false;
                jumpButtonBuffer = 0;
                jumps = jumpsMax;
            }

            while(!gameGrid.detectPlayerCollision(0, Math.signum(speedVector.y)))
            {
                gameGrid.movePlayer(0,Math.signum(speedVector.y));
            }
            speedVector.y=0;
        }

        gameGrid.movePlayer(0,speedVector.y);

        jumping=false;
    }

    public static void jump() {
        jumping = true;
    }
    public static void dash() {
        if(dashes>0) {
            dashes--;
            dashActiveRemaining=dashDuration;
        }
    }

    public void checkForCheckpoint() {
        TileDescriber tile = gameGrid.getTileUnderPlayer();

        if (tile.tile == 22) {
            gameGrid.previousCheckPoint = new Vector2D(gameGrid.playerX, gameGrid.playerY);

            TileDescriber newTile = new TileDescriber(10, tile.x, tile.y, gameGrid.tileSize, gameGrid.tileSize, 0, 0);
            newTile.updateImage(gameGrid.tileSize*gameGrid.scale); // replace checkpoint with active checkpoint

            gameGrid.setTile(tile.x, tile.y, newTile);

            gameGrid.playerLevel++;
        }
    }

    public boolean checkForWin() {
        TileDescriber tile = gameGrid.getTileUnderPlayer();

        if (tile.tile == 11) {
            gameGrid.gameWon = true;
        }

        return  gameGrid.gameWon;
    }

    public void checkForLos() {
        int[] panelCords = gameGrid.getPanelCordsFromGridCords(gameGrid.getWorldSize().x, gameGrid.getWorldSize().y);
        if (gameGrid.playerY > panelCords[1]) {
            speedVector = new Vector2D();
            gameGrid.setPlayer(gameGrid.previousCheckPoint.x, gameGrid.previousCheckPoint.y);
        }

    }

    public void updateSpecial(boolean[] specialKeys) {
        gameGrid.showDebug = specialKeys[0];
        if (specialKeys[1]) {
            jumpsMax = 10;
            cheatMode = true;
        }
    }
}
