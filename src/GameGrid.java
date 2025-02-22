import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class GameGrid extends JPanel {
    private final TileDescriber[][] world;
    public ArrayList<String[]> debugArrayList = new ArrayList<>();
    public final int tileSize;
    public int scale;
    public final int playerWidth;
    public final int playerHeight;
    public double playerX;
    public double playerY;

    public int playerLevel;

    public Vector2D previousCheckPoint;

    public ParticleSystem ps;
    public ParticleType waterParticleType = new ParticleType();
    public ParticleType walkParticleType = new ParticleType();
    public ParticleType dashTrailParticleType = new ParticleType();
    public ParticleType fireParticleType = new ParticleType();
    public ParticleType doubleJumpParticleType = new ParticleType();
    public int particlesAmount = 0;
    public boolean createDoubleJumpParticles = false;
    public boolean createDashParticle = false;
    private BufferedImage playerImg;
    private final int[] dashCooldownPolygonX = new int[50];
    private final int[] dashCooldownPolygonY = new int[dashCooldownPolygonX.length];


    public boolean gameWon = false;
    public boolean showDebug = true;

    public final TileDescriber dummyAirTile = new TileDescriber(0, 0, 0, 0, 0, 0, 0);

    public GameGrid(int tileSize, double playerX, double playerY, TileDescriber[][] level, double checkpointX, double checkpointY, int playerLevel) {
        super();

        this.setLayout(null);
        this.setLocation(0, 0);

        this.setBackground(new Color(0x21263F));

        this.world = level;

        this.tileSize = tileSize;

        this.playerX = playerX;
        this.playerY = playerY;

        this.previousCheckPoint = new Vector2D(checkpointX, checkpointY);
        this.playerLevel = playerLevel;

        setScale(4);

        this.playerWidth = scale * tileSize;
        playerHeight = scale * tileSize;
        ps = new ParticleSystem();

        dashTrailParticleType.setAcceleration(0, 0);
        dashTrailParticleType.setSpeed(0.02, 0.05, -0.002, 0);
        dashTrailParticleType.setDirection(0, 360, 0, 1.5);
        dashTrailParticleType.setRotation(0, 0, 0, 0);
        dashTrailParticleType.setAlpha(new int[]{255, 25});
        dashTrailParticleType.setColors(new int[][]{{90},{90},{90}});
        dashTrailParticleType.setLifeTime(16);
        dashTrailParticleType.setSize(15,17,-0.3,0);
        dashTrailParticleType.setShape(ParticleSystem.shapes.rectangle);

        doubleJumpParticleType.setAcceleration(0, 0.06);
        doubleJumpParticleType.setSpeed(0.3, 0.6, -0.01, 0);
        doubleJumpParticleType.setDirection(0, 360, 0, 1.5);
        doubleJumpParticleType.setRotation(0, 0, 0, 0);
        doubleJumpParticleType.setAlpha(new int[]{255,20});
        doubleJumpParticleType.setColors(new int[][]{{190},{190},{190}});
        doubleJumpParticleType.setLifeTime(50);
        doubleJumpParticleType.setSize(19,24,-0.3,0);
        doubleJumpParticleType.setShape(ParticleSystem.shapes.rectangle);

        fireParticleType.setAcceleration(0,0);
        fireParticleType.setShape(ParticleSystem.shapes.circle);
        fireParticleType.setSize(9,14,-0.02,0.008);
        fireParticleType.setSpeed(0.5, 0.75, 0.018, 0.001);
        fireParticleType.setDirection(265, 285, 0, 1.5);
        fireParticleType.setRotation(0, 360, 0, 0.1);
        fireParticleType.setAlpha(new int[]{255,255,255,255,128,0});
        fireParticleType.setColors(new int[][]{{253,242,236,100,50,0}, {207,125,82,100,50,0}, {88, 12,31,100,50,0}});
        fireParticleType.setLifeTime(80);

        waterParticleType.setSize(5,7,-0.03,0.008);
        waterParticleType.setDirection(200,340,0,0.1);
        waterParticleType.setColors(new int[][]{{255,0}, {255,85}, {255,171}});
        waterParticleType.setSpeed(0.6,1.2,0,0.01);
        waterParticleType.setAcceleration(0,0.02);
        waterParticleType.setShape(ParticleSystem.shapes.rectangle);


        waterParticleType.setAlpha(new int[]{255,0,255});


        walkParticleType.setShape(ParticleSystem.shapes.circle);
        walkParticleType.setSize(5,7, -0.2, 0.04);
        walkParticleType.setAlpha(new int[]{255, 0});
        walkParticleType.setColors(new int[][]{{150, 86},{148, 86},{141, 86}});

        for(int i=0;i<dashCooldownPolygonX.length;i++)
        {
            int x = (int) lengthDirX(45,(double) (i-1)/(dashCooldownPolygonX.length-3)*360-90);
            int y = (int) lengthDirY(45,(double) (i-1)/(dashCooldownPolygonY.length-3)*360-90);
            dashCooldownPolygonX[i] = x+70;
            dashCooldownPolygonY[i] = y+70;
        }
        dashCooldownPolygonX[0] = 70;
        dashCooldownPolygonY[0] = 70;
        dashCooldownPolygonX[dashCooldownPolygonX.length-1] = 70;
        dashCooldownPolygonY[dashCooldownPolygonY.length-1] = 70;

        try {
            playerImg = ImageIO.read(new File("assets/player/player.png"));
        } catch (IOException e) {
            System.err.println("Player Image not Found!");
        }

    }

    public void paint(final Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        super.paint(g);

        g.setColor(Color.white);

        int screenWidthHalf = this.getSize().width / 2;
        int screenHeightHalf = this.getSize().height / 2;

        PointerInfo a = MouseInfo.getPointerInfo();
        Point b = a.getLocation();

        for(int i = 0; i<particlesAmount; i++) {
            ps.addParticle(playerX + b.x + randomRange(-40, 40), b.y + playerY, waterParticleType);
        }
        if (createDashParticle) {
            ps.addParticle(playerX+getSize().width/2+playerWidth/2+randomRange(-10,10),playerY+getSize().height/2+playerHeight/2+randomRange(-10,10), dashTrailParticleType);
        }
        if (createDoubleJumpParticles) {
            for(int i = 0; i<14; i++) {
                ps.addParticle(playerX+getSize().width/2+playerWidth/2+randomRange(-scale*tileSize/2,scale*tileSize/2),playerY+getSize().height/2+playerHeight+randomRange(-6,6), doubleJumpParticleType);
            }
            createDoubleJumpParticles = false;
        }

        // draw world:
        for (TileDescriber[] tiles : world) {
            for (TileDescriber tile : tiles) {
                if (tile != null) {
                    int[] panelCords = getPanelCordsFromGridCords(tile.x, tile.y);
                    panelCords[0]+=-playerX+screenWidthHalf;
                    panelCords[1]+=-playerY+screenHeightHalf;
                    Image image = tile.getImage();
                    if (inView(panelCords[0], panelCords[1])) {
                        g.drawImage(image,panelCords[0],panelCords[1], null);
                    }
                    int[] pos = getPanelCordsFromGridCords(tile.x, tile.y);
                    switch(tile.tile) { // tiles on which particles should be set
                        case 66:
                            ps.addParticle(pos[0]+screenWidthHalf+tileSize*scale/2+randomRange(-7,7),pos[1]+screenHeightHalf+tileSize*scale/2+randomRange(2,7), fireParticleType);
                            break;
                        case 108:
                        case 109:
                        case 110:
                        case 111:
                            ps.addParticle(pos[0]+screenWidthHalf+tileSize*scale/2+randomRange(-tileSize*scale,tileSize*scale),pos[1]+screenHeightHalf+tileSize*scale/2+tileSize*scale-6, waterParticleType);
                            break;
                    }
                }
            }
        }


        // draw particles
        ArrayList<Particle> toRemove = new ArrayList<>();
        for (Particle part : ps.particles) {
            part.update();
            if (part.isDead()) {
                toRemove.add(part);
            }
            int x = (int) (part.x - playerX);
            int y = (int) (part.y - playerY);
            if (inView(x,y)) {
                if (part.hasSprite) {
                    double alpha = (double) part.currentAlpha / 255;
                    AffineTransform rotated = AffineTransform.getRotateInstance(part.rotation, x, y);
                    AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) alpha);
                    g2d.setComposite(ac);
                    g2d.setTransform(rotated);
                    g2d.drawImage(part.sprite, (x - part.spriteWidth / 2), (y - part.spriteHeight / 2), null);
                } else {
                    g.setColor(part.currentColor);
                    drawParticleShape(g, part.type.shape, x, y, (int) part.size);
                }
            }
        }
        ps.particles.removeAll(toRemove); // Otherwise it would delete from iterating ArrayList

        g.setColor(Color.yellow);
        g.fillPolygon(dashCooldownPolygonX,dashCooldownPolygonY, (int) (dashCooldownPolygonX.length*(1-(double)PlayerHelper.dashCooldownRemaining/(double)PlayerHelper.dashCooldown)));

        g.drawImage(playerImg.getScaledInstance(tileSize * scale, tileSize * scale, Image.SCALE_DEFAULT), screenWidthHalf, screenHeightHalf, null);

        if(gameWon) {
            g.setColor(Color.ORANGE);
            Font winFont = new Font("Arial", Font.BOLD, 100);

            g.setFont(winFont);
            g.drawString("You WIN!", screenWidthHalf, screenHeightHalf);
            g.setFont(winFont.deriveFont(Font.PLAIN).deriveFont(14f));
            g.drawString("Press ESC to exit. (Your Progress will be backed up and deleted.)", screenWidthHalf, screenHeightHalf + 14);
        }

        if(showDebug) {
            g.setColor(Color.white);
            for (int i = 0; i < debugArrayList.size() - 1; i++) {
                String[] currentArray = debugArrayList.get(i);
                g.drawString(currentArray[0], 100, 100 + i * 25);
                g.drawString(":  " + currentArray[1], 220, 100 + i * 25);
            }
            debugArrayList.clear();
        }

    }

    void drawParticleShape(Graphics g,ParticleSystem.shapes shape,int x,int y,int size) {
        if (shape == ParticleSystem.shapes.circle) {
            g.fillOval(x-size/2, y-size/2, size, size);
        } else if (shape == ParticleSystem.shapes.rectangle) {
            g.fillRect(x-size/2,y-size/2,size,size);
        }
    }
    boolean inView(double x, double y)
    {
        return (x > -getWidth()*0.2 &&
                x < getWidth()*1.2 &&
                y > -getHeight()*0.2 &&
                y < getHeight()*1.2
        );
    }
    public void movePlayer(double x, double y) {
        this.playerX += x;
        this.playerY += y;
    }
    double randomRange(double min, double max) {
        return Math.random() * (max - min) + min;
    }

    public void setPlayer(double x, double y) {
        this.playerX = x;
        this.playerY = y;
    }
    public void showDebugMessage(String key, String value)
    {
        String[] debugStringArray = new String[2];
        debugStringArray[0] = key;
        debugStringArray[1] = value;
        debugArrayList.add(debugStringArray);
    }
    public boolean detectPlayerCollision(double velocityX,double velocityY) {
        int[] playerGridCords = getGridCordsFromPanelCords((int) (playerX + playerWidth/2), (int) (playerY + playerHeight/2));

        for (int x = Math.max(playerGridCords[0] - 2,0); x <= Math.min(playerGridCords[0] + 2,world.length-1); x++) {
            for (int y = Math.max(playerGridCords[1] - 2,0); y <= Math.min(playerGridCords[1] + 2,world[x].length-1); y++) {

                if (world[x][y] != null && world[x][y].type == 0) //type=0 -> Has Collision
                {
                    Vector2D tileTopLeft = new Vector2D(x*tileSize*scale,y*tileSize*scale);
                    Vector2D tileBottomRight = new Vector2D((x+1)*tileSize*scale,(y+1)*tileSize*scale);
                    Vector2D playerTopLeft = new Vector2D((playerX+velocityX),(playerY+velocityY));
                    Vector2D playerBottomRight = new Vector2D((playerX+playerWidth+velocityX),(playerY+playerHeight+velocityY));
                    if (playerBottomRight.x >= tileTopLeft.x &&
                            playerTopLeft.x <= tileBottomRight.x &&
                            playerBottomRight.y >= tileTopLeft.y &&
                            playerTopLeft.y <= tileBottomRight.y) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public TileDescriber getTileUnderPlayer() {
        int[] realPlayerCords = getPlayerPanelCords();
        int[] playerGridCords = getGridCordsFromPanelCords(realPlayerCords[0],realPlayerCords[1]);

        playerGridCords[0] = clamp(playerGridCords[0], 0, world.length-1);
        playerGridCords[1] = clamp(playerGridCords[1]+1, 0, world[0].length-2);

        if (world[playerGridCords[0]][playerGridCords[1]] != null) {
            return world[playerGridCords[0]][playerGridCords[1]];
        }
        return dummyAirTile; // null is equal to air which is 0
    }

    private int clamp(int value,int min,int max)
    {
        return Math.max(min, Math.min(max, value));
    }
    public void setScale(int scale) {
        this.scale = scale;

        for (TileDescriber[] row : world) {
            for (TileDescriber block : row) {
                if (block != null) {
                    block.updateImage(tileSize*scale);
                }
            }
        }

    }

    public void setTile(int x, int y, TileDescriber tile) {
        world[x][y] = tile;
    }

    public int[] getGridCordsFromPanelCords(int x, int y) {
        return new int[]{x/(tileSize*scale), y/(tileSize*scale)};
    }

    public int[] getPanelCordsFromGridCords(int x, int y) {
        return new int[]{x*(tileSize*scale), y*(tileSize*scale)};
    }

    public Point getWorldSize() {
        return new Point(world.length, world[0].length);
    }

    public TileDescriber[][] getWorld() {
        return world;
    }

    public int[] getPlayerPanelCords() {
        return new int[]{(int) (playerX + playerWidth/2), (int) (playerY + playerHeight / 2)};
    }
    double lengthDirX(double len, double dir) {
        double rad = Math.toRadians(dir);
        return Math.cos(rad)*len;
    }
    double lengthDirY(double len, double dir) {
        double rad = Math.toRadians(dir);
        return Math.sin(rad)*len;
    }
}
