import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TileDescriber {
    private static final String tileSetPath = "assets/tiles/tilesetFinal.png";

    private static final int tileSetWidth = 12;
    private static final int tileSize = 8;
    private static BufferedImage tileSet;

    static {
        try {
            tileSet = ImageIO.read(new File(tileSetPath));
        } catch (IOException e) {
            System.err.println("Error loading tileset. Program will exit.");
            System.exit(-1);
            tileSet = null;
        }
    }

    public final int tile;
    public final int x; //Grid
    public final int y;
    public final int width;
    public final int height;
    public final int rotation;
    public final int type; // type 0: normal type 1: without collision

    private Image img;

    public TileDescriber(int tile, int x, int y, int width, int height, int rotation, int type) {
        this.tile = tile;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.rotation = rotation;
        this.type = type;
    }

    public void updateImage(int targetSize) {
        BufferedImage tmpImg = tileSet.getSubimage(tile % tileSetWidth * tileSize, tile / tileSetWidth * tileSize, tileSize, tileSize);

        int centerXY = tileSize / 2;

        AffineTransform transform = AffineTransform.getRotateInstance(Math.toRadians(rotation*90), centerXY, centerXY);
        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);

        tmpImg = op.filter(tmpImg, null);
        img = tmpImg.getScaledInstance(targetSize, targetSize, Image.SCALE_FAST);
    }

    public Image getImage() {
        return img;
    }
}
